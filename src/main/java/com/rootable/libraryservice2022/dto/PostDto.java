package com.rootable.libraryservice2022.dto;

import com.rootable.libraryservice2022.domain.Book;
import com.rootable.libraryservice2022.domain.Member;
import com.rootable.libraryservice2022.domain.Posts;
import com.rootable.libraryservice2022.domain.Result;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@NoArgsConstructor
public class PostDto {

    private Long id;

    @NotBlank(message = "제목은 필수 입력 값입니다")
    @Size(max = 20, message = "제목은 20자를 넘을 수 없습니다")
    private String title;

    private String content;

    @NotNull
    private Member member;

    @NotNull(message = "도서를 선택해주세요")
    private Book book;

    private Long fileId;

    private Result result;

    public Posts toEntity() {
        Posts build = Posts.builder()
                .id(id)
                .title(title)
                .content(content)
                .member(member)
                .book(book)
                .fileId(fileId)
                .result(result)
                .build();
        return build;
    }

    @Builder
    public PostDto(Long id, String title, String content, Member member, Book book, Long fileId) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.member = member;
        this.book = book;
        this.fileId = fileId;
    }

}
