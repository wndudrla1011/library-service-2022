package com.rootable.libraryservice2022.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.*;

@Getter
@Entity
@SequenceGenerator(
        name = "POSTS_SEQ_GENERATOR",
        sequenceName = "POSTS_SEQ",
        initialValue = 1, allocationSize = 1
)
@NoArgsConstructor
public class Posts extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "POSTS_SEQ_GENERATOR")
    @Column(name = "post_id")
    private Long id;

    @Column(length = 20, nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member; //게시물 작성자

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book; //신청 도서

    private Long fileId;

    @Enumerated(EnumType.STRING)
    private Result result;

    @OneToMany(mappedBy = "posts", cascade = CascadeType.ALL)
    @OrderBy("id asc") //댓글 정렬
    private List<Comment> commentList = new ArrayList<>();

    @Builder
    public Posts(Long id, String title, String content, Member member, Book book, Long fileId, Result result) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.member = member;
        this.book = book;
        this.fileId = fileId;
        this.result = result;
    }

    /*
     * 연관관계 메서드
     * */
    public void bindMember(Member member) {
        this.member = member;
        member.getPostsList().add(this);
    }

    public void bindBook(Book book) {
        this.book = book;
        book.getPostsList().add(this);
    }

    public void update(String title, String content, Book book, Result result) {
        this.title = title;
        this.content = content;
        this.book = book;
        this.result = result;
    }

    public void changeFile(Long fileId) {
        this.fileId = fileId;
    }

}
