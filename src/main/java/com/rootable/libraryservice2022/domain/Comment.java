package com.rootable.libraryservice2022.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@Getter
@NoArgsConstructor
@Table(name = "comments")
@Entity
@SequenceGenerator(
        name = "COMMENTS_SEQ_GENERATOR",
        sequenceName = "COMMENTS_SEQ",
        initialValue = 1, allocationSize = 1
)
public class Comment extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "COMMENTS_SEQ_GENERATOR")
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String comment; //댓글 내용

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "posts_id", nullable = false)
    private Posts posts;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member; //작성자

    @Builder
    public Comment(Long id, String comment, Posts posts, Member member) {
        this.id = id;
        this.comment = comment;
        this.posts = posts;
        this.member = member;
    }

    /*
     * 연관 관계 메서드
     * */
    public void bindPosts(Posts posts) {
        this.posts = posts;
        posts.getCommentList().add(this);
    }

    public void bindMember(Member member) {
        this.member = member;
        member.getCommentList().add(this);
    }

    public void update(String comment) {
        this.comment = comment;
    }

}
