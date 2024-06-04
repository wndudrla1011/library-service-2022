package com.rootable.libraryservice2022.controller;

import com.rootable.libraryservice2022.domain.*;
import com.rootable.libraryservice2022.service.BookService;
import com.rootable.libraryservice2022.service.CommentService;
import com.rootable.libraryservice2022.service.FileService;
import com.rootable.libraryservice2022.service.PostsService;
import com.rootable.libraryservice2022.web.MySecured;
import com.rootable.libraryservice2022.web.argumentresolver.Login;
import com.rootable.libraryservice2022.dto.FileDto;
import com.rootable.libraryservice2022.dto.PostDto;
import com.rootable.libraryservice2022.dto.SessionMember;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class PostsController {

    private final PostsService postsService;
    private final BookService bookService;
    private final FileService fileService;
    private final CommentService commentService;

    public static String SAME_PERSON_KEY = "same";

    @GetMapping("/posts")
    public String posts(Model model, @Login SessionMember loginMember) {

        log.info("게시글 관리 페이지");

        List<Posts> posts = postsService.findPosts();

        //뷰 렌더링 값 전달
        model.addAttribute("posts", posts);
        model.addAttribute("member", loginMember);
        return "posts/posts";

    }

    @MySecured(role = Role.GUEST)
    @GetMapping("/posts/add")
    public String addForm(Model model) {

        log.info("게시글 등록 폼 이동");

        List<Book> books = bookService.books();

        //뷰 렌더링 값 전달
        model.addAttribute("posts", new Posts());
        model.addAttribute("bookList", books);
        return "posts/addPost";

    }

    @GetMapping("/posts/{postId}")
    public String post(@PathVariable Long postId, Model model, @Login SessionMember loginMember) {

        log.info("게시글 정보");

        Posts post = postsService.findById(postId);
        model.addAttribute("post", post);

        //업로드 파일이 존재할 경우
        if (post.getFileId() != null) {
            FileDto file = fileService.getFile(post.getFileId()); //조회
            model.addAttribute("filename", file.getOriginFilename()); //파일명 뷰 전달
        }

        //작성자 본인이거나 관리자일 경우
        if (post.getMember().getId().equals(loginMember.getId()) || loginMember.getRole() == Role.ADMIN) {
            model.addAttribute("same", SAME_PERSON_KEY); //키 발급
        }

        //댓글 리스트
        List<Comment> comments = post.getCommentList();

        if (comments != null && !comments.isEmpty()) {
            model.addAttribute("comments", comments);
            model.addAttribute("size", comments.size());
        }

        //댓글 렌더링
        model.addAttribute("comment", new Comment());

        return "/posts/postInfo";

    }

    @MySecured(role = Role.GUEST)
    @GetMapping("/posts/{postId}/edit")
    public String editForm(@PathVariable Long postId, Model model) {

        log.info("게시글 수정 폼 이동");

        PostDto post = postsService.getPost(postId);

        //업로드 파일이 존재할 경우
        if (post.getFileId() != null) {
            FileDto file = fileService.getFile(post.getFileId()); //조회
            model.addAttribute("filename", file.getOriginFilename()); //파일명 뷰 전달
        }

        model.addAttribute("posts", post);
        model.addAttribute("bookList", bookService.books());

        return "posts/editPost";

    }

    @GetMapping("/posts/mine")
    public String myList(Model model, @Login SessionMember loginMember) {

        log.info("나의 게시물 목록");

        List<Posts> myPosts = postsService.findMyPosts(loginMember.getId());
        model.addAttribute("myPosts", myPosts);

        return "posts/myPosts";

    }

    @GetMapping("/alert/{postId}")
    public String alert(@PathVariable Long postId, Model model) {

        log.info("처리 결과 alert");

        Posts post = postsService.findById(postId);
        String reason = post.getResult().getReason();

        model.addAttribute("msg", reason); //처리 결과 사유
        model.addAttribute("url", "/posts"); //Redirect 주소
        return "posts/alert";

    }

    @GetMapping("/posts/{postId}/comments/add")
    public String commentForm(@PathVariable Long postId, Model model) {

        log.info("댓글 입력 폼 이동");

        model.addAttribute("postId", postId);
        return "comments/commentForm";
    }

    @GetMapping("/posts/{postId}/comments/{commentId}")
    public String editComment(@PathVariable Long postId, @PathVariable Long commentId, Model model,
                              @Login SessionMember loginMember) {

        log.info("댓글 수정 폼 이동");

        Comment comment = commentService.getComment(commentId);

        model.addAttribute("comment", comment);
        model.addAttribute("member", loginMember);

        return "comments/editComment";

    }

}
