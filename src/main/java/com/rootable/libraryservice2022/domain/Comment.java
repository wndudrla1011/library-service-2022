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
public class Comment extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String comment; //댓글 내용

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "posts_id", nullable = false)
    private Posts posts;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id", nullable = false, updatable = false)
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
    public void setPosts(Posts posts) {
        this.posts = posts;
        posts.getCommentList().add(this);
    }

    public void setMember(Member member) {
        this.member = member;
        member.getCommentList().add(this);
    }

    public void update(String comment) {
        this.comment = comment;
    }

}
