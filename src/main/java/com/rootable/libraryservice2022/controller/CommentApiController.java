package com.rootable.libraryservice2022.controller;

import com.rootable.libraryservice2022.domain.Comment;
import com.rootable.libraryservice2022.service.CommentService;
import com.rootable.libraryservice2022.web.argumentresolver.Login;
import com.rootable.libraryservice2022.dto.CommentRequestDto;
import com.rootable.libraryservice2022.dto.SessionMember;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CommentApiController {

    private final CommentService commentService;

    @GetMapping("/posts/comments")
    public ModelAndView comments(@Login SessionMember loginMember) {

        log.info("나의 댓글 목록");

        ModelAndView mav = new ModelAndView("comments/myComments");

        List<Comment> myComments = commentService.findMyComments(loginMember.getId());

        mav.addObject("myComments", myComments);
        return mav;

    }

    @PostMapping("/posts/{postId}/comments/add")
    public ResponseEntity commentSave(@PathVariable Long postId, @RequestBody CommentRequestDto dto,
                                      @Login SessionMember loginMember) {

        log.info("댓글 생성");
        return ResponseEntity.ok(commentService.commentSave(loginMember.getId(), postId, dto));

    }

    @PutMapping("/posts/{postId}/comments/{commentId}")
    public ResponseEntity update(@PathVariable Long postId, @PathVariable Long commentId,
                                      @RequestBody CommentRequestDto dto) {

        log.info("댓글 수정");
        return ResponseEntity.ok(commentService.update(commentId, dto));

    }

    @DeleteMapping("/posts/{postId}/comments/{commentId}")
    public void delete(@PathVariable Long postId, @PathVariable Long commentId) {
        log.info("댓글 삭제");
        commentService.delete(commentId);
    }

}
