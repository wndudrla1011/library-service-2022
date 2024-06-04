package com.rootable.libraryservice2022.controller;

import com.rootable.libraryservice2022.domain.Member;
import com.rootable.libraryservice2022.domain.Posts;
import com.rootable.libraryservice2022.domain.Role;
import com.rootable.libraryservice2022.service.BookService;
import com.rootable.libraryservice2022.service.FileService;
import com.rootable.libraryservice2022.service.MemberService;
import com.rootable.libraryservice2022.service.PostsService;
import com.rootable.libraryservice2022.web.MySecured;
import com.rootable.libraryservice2022.web.argumentresolver.Login;
import com.rootable.libraryservice2022.dto.FileDto;
import com.rootable.libraryservice2022.dto.PostDto;
import com.rootable.libraryservice2022.dto.SessionMember;
import com.rootable.libraryservice2022.web.file.FileStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Slf4j
@Controller
@RequiredArgsConstructor
public class PostsRestfulController {

    private final PostsService postsService;
    private final BookService bookService;
    private final MemberService memberService;
    private final FileService fileService;
    private final FileStore fileStore;

    @MySecured(role = Role.GUEST)
    @PostMapping("/posts/add")
    public String write(@Validated @ModelAttribute("posts") PostDto postDto, BindingResult bindingResult,
                        @RequestParam("file") MultipartFile files, Model model, @Login SessionMember loginMember) {

        log.info("게시글 등록");

        Member member = memberService.findOne(loginMember.getId());

        postDto.setMember(member);

        //파일 -> 서버 (저장/업로드)
        try {
            String originFilename = files.getOriginalFilename(); //고객이 업로드한 파일명
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

            //FileDto 바인딩
            FileDto fileDto = new FileDto();
            fileDto.setOriginFilename(originFilename);
            fileDto.setFilename(storeFileName);
            fileDto.setFilePath(filePath);

            Long fileId = fileService.saveFile(fileDto); //파일 DB 저장
            postDto.setFileId(fileId); //postDto fileId 셋팅

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            ////Bean Validation
            if (bindingResult.hasFieldErrors("title") || bindingResult.hasFieldErrors("book")) {
                model.addAttribute("bookList", bookService.books());
                log.info("검증 에러 errors={}", bindingResult);

                return "posts/addPost";
            }

            postsService.savePost(postDto); //게시글 저장
        }

        return "redirect:/posts";

    }

    @MySecured(role = Role.GUEST)
    @PutMapping("/posts/{postId}/edit")
    public String edit(@PathVariable Long postId, @Validated @ModelAttribute("posts") PostDto requestDto,
                       BindingResult bindingResult, Model model, @Login SessionMember loginMember) {

        log.info("게시글 수정");

        model.addAttribute("bookList", bookService.books());

        Member member = memberService.findOne(loginMember.getId());
        requestDto.setMember(member);

        PostDto savedPost = postsService.getPost(postId); //게시글 조회

        //업로드 파일이 존재할 경우
        if (savedPost.getFileId() != null) {
            FileDto file = fileService.getFile(savedPost.getFileId()); //조회
            requestDto.setFileId(file.getId()); //postDto fileId 셋팅
            model.addAttribute("filename", file.getOriginFilename()); //파일명 뷰 전달
        }

        //Bean Validation
        if (bindingResult.hasFieldErrors("title") || bindingResult.hasFieldErrors("book")) {
            log.info("검증 에러 errors={}", bindingResult);
            return "posts/editPost";
        }

        postsService.update(postId, requestDto); //게시글 갱신

        return "redirect:/posts/" + postId;

    }

    @MySecured(role = Role.GUEST)
    @DeleteMapping("/posts/{postId}/edit")
    public String delete(@PathVariable Long postId) {

        log.info("게시글 삭제");

        Long fileId = postsService.findById(postId).getFileId();

        //게시글 삭제 -> 파일도 함께 삭제
        if (fileId != null) {
            fileService.delete(fileId);
        }

        postsService.delete(postId);
        return "redirect:/posts";

    }

    @MySecured(role = Role.GUEST)
    @PostMapping("/posts/{postId}/edit")
    public String renewFile(@PathVariable Long postId, @RequestParam("file") MultipartFile files, Model model) {

        log.info("첨부파일 수정");

        model.addAttribute("bookList", bookService.books());

        try {
            String originFilename = files.getOriginalFilename(); //고객이 업로드한 파일명
            //업로드 파일이 없는 상태에서 등록 클릭
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

            //FileDto 바인딩
            FileDto fileDto = new FileDto();
            fileDto.setOriginFilename(originFilename);
            fileDto.setFilename(storeFileName);
            fileDto.setFilePath(filePath);

            Long fileId = fileService.saveFile(fileDto); //DB File 저장

            //현재 게시글 조회 + fileId 변경
            PostDto postDto = postsService.getPost(postId);
            postDto.setFileId(fileId);
            Posts posts = postsService.updateFile(postId, postDto);

            //업로드 파일이 존재할 경우
            if (postDto.getFileId() != null) {
                FileDto file = fileService.getFile(posts.getFileId()); //조회
                model.addAttribute("filename", file.getOriginFilename()); //파일명 뷰 전달
            }
            model.addAttribute("posts", posts);
        } catch (IOException e) {
            model.addAttribute("postId", postId);
            e.printStackTrace();
            return "error/emptyFileError"; //오류 페이지 이동
        }

        return "redirect:/posts/" + postId + "/edit";
    }

    @MySecured(role = Role.GUEST)
    @DeleteMapping("/posts/{postId}")
    public String deleteFile(@PathVariable Long postId, Model model) {

        log.info("첨부파일 삭제");

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
            model.addAttribute("post", post);
            model.addAttribute("bookList", bookService.books());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "redirect:/posts/" + postId;

    }

}
