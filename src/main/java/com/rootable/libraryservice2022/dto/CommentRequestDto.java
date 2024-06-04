package com.rootable.libraryservice2022.dto;

import com.rootable.libraryservice2022.domain.Comment;
import com.rootable.libraryservice2022.domain.Member;
import com.rootable.libraryservice2022.domain.Posts;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentRequestDto {

    private Long id;

    @NotBlank
    private String comment;
    private Posts posts;
    private Member member;

    //DTO -> Entity
    public Comment toEntity() {
        Comment build = Comment.builder()
                .id(id)
                .comment(comment)
                .posts(posts)
                .member(member)
                .build();

        return build;
    }

}
