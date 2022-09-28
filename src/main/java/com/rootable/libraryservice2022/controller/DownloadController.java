package com.rootable.libraryservice2022.controller;

import com.rootable.libraryservice2022.domain.Member;
import com.rootable.libraryservice2022.domain.Posts;
import com.rootable.libraryservice2022.service.BookService;
import com.rootable.libraryservice2022.service.FileService;
import com.rootable.libraryservice2022.service.PostsService;
import com.rootable.libraryservice2022.web.dto.FileDto;
import com.rootable.libraryservice2022.web.dto.PostDto;
import com.rootable.libraryservice2022.web.file.FileStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@RestController
@RequiredArgsConstructor
public class DownloadController {

    private final FileService fileService;
    private final PostsService postsService;
    private final BookService bookService;
    private final FileStore fileStore;

    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> fileDownload(@PathVariable("fileId") Long fileId) throws IOException {

        FileDto fileDto = fileService.getFile(fileId);
        Path path = Paths.get(fileDto.getFilePath());
        Resource resource = new InputStreamResource(Files.newInputStream(path));
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" +
                        fileDto.getOriginFilename() + "\"")
                .body(resource);

    }

    //파일 삭제
    @DeleteMapping("/posts/{postId}")
    public ModelAndView deleteFile(@PathVariable Long postId, HttpServletRequest request) {

        log.info("첨부파일 삭제");

        ModelAndView mav = new ModelAndView("/posts/postInfo");

        HttpSession session = request.getSession();
        Member member = (Member) session.getAttribute("loginMember");

        File file = null;

        try {
            PostDto postDto = postsService.getPost(postId); //현재 게시글 조회
            FileDto fileDto = fileService.getFile(postDto.getFileId()); //현재 게시글의 파일 조회
            file = new File(fileDto.getFilePath());
            if (file.delete()) { //서버로 업로드된 파일 삭제
                log.info("파일 삭제 성공");
            } else {
                log.info("파일 삭제 실패");
            }
            //삭제된 파일명 출력
            String originFileName = fileDto.getOriginFilename();
            log.info("removed file = {}", originFileName);
            //DB File 제거
            fileService.delete(fileDto.getId());
            //DB Posts.fileId 제거
            postDto.setFileId(null);
            Posts post = postsService.updateFile(postId, postDto);
            //갱신한 Posts 뷰로 전달
            mav.addObject("post", post);
            mav.addObject("bookList", bookService.books());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mav;

    }

    //파일 수정
    @PostMapping("/posts/{postId}/edit")
    public ModelAndView renewFile(@PathVariable Long postId, @RequestParam("file") MultipartFile files) {

        log.info("첨부파일 수정");

        ModelAndView mav = new ModelAndView("/posts/editPost");
        mav.addObject("bookList", bookService.books());

        try {
            String originFilename = files.getOriginalFilename(); //고객이 업로드한 파일명
            //업로드 파일이 없는 경우
            if ("".equals(originFilename)) {
                throw new IOException();
            }
            String storeFileName = fileStore.createStoreFileName(originFilename); //서버 저장 파일명
            String saveDir = fileStore.getFileDir(); //서버 저장 디렉토리
            //저장 디렉토리가 없는 경우
            if (!new File(saveDir).exists()) {
                try {
                    new File(saveDir).mkdir(); //생성
                } catch (Exception e) {
                    e.getStackTrace();
                }
            }
            String filePath = fileStore.getFullPath(originFilename);
            files.transferTo(new File(filePath)); //업로드

            //FileDto 셋팅
            FileDto fileDto = new FileDto();
            fileDto.setOriginFilename(originFilename);
            fileDto.setFilename(storeFileName);
            fileDto.setFilePath(filePath);

            //DB File 저장
            Long fileId = fileService.saveFile(fileDto);

            //현재 게시글 조회 + fileId 변경
            PostDto postDto = postsService.getPost(postId);
            postDto.setFileId(fileId);
            Posts posts = postsService.updateFile(postId, postDto);

            if (postDto.getFileId() != null) {
                FileDto file = fileService.getFile(posts.getFileId());
                mav.addObject("filename", file.getOriginFilename());
            }

            mav.addObject("posts", posts);
        } catch (IOException e) {
            mav = new ModelAndView("error/emptyFileError");
            mav.addObject("postId", postId);
            e.printStackTrace();
        }

        return mav;
    }

}
