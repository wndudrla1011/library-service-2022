package com.rootable.libraryservice2022.repository;

import com.rootable.libraryservice2022.domain.*;
import com.rootable.libraryservice2022.dto.BookSaveDto;
import com.rootable.libraryservice2022.dto.MemberDto;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PostsRepositoryTest {

    @Autowired
    PostsRepository postsRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    BookRepository bookRepository;

    @After
    public void cleanUp() {
        postsRepository.deleteAll();
    }
    
    @Test
    public void register_baseTimeEntity() {

        //given
        LocalDateTime now = LocalDateTime.of(2022, 9, 24, 0, 0, 0);

        MemberDto memberDto = MemberDto.builder()
                .name("kim")
                .loginId("test2")
                .password("1111!!qq")
                .email("test2@gmail.com")
                .role(Role.USER)
                .build();

        Member member = createMember(memberDto);
        memberRepository.save(member);

        BookSaveDto bookDto = BookSaveDto.builder()
                .title("자바 기초")
                .writer("TonyJoo")
                .price(10000)
                .stock(3)
                .status(Status.PERMISSION)
                .build();

        Book book = createBook(bookDto);
        bookRepository.save(book);

        Posts posts = Posts.builder()
                .title("aaa")
                .content("hello")
                .member(member)
                .book(book)
                .build();

        postsRepository.save(posts);

        //when
        Posts savedPost = postsRepository.findPosts().stream()
                .filter(post -> post.getTitle().equals("aaa"))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("게시물이 존재하지 않습니다."));

        System.out.println("post.getCreatedDate() = " + savedPost.getCreatedDate());
        System.out.println("post.getModifiedDate() = " + savedPost.getModifiedDate());

        //then
        assertThat(savedPost.getCreatedDate()).isAfter(now);
        assertThat(savedPost.getModifiedDate()).isAfter(now);

    }

    @Test
    public void findPosts() {

        //given
        MemberDto memberDto = MemberDto.builder()
                .name("kim")
                .loginId("test2")
                .password("1111!!qq")
                .email("test2@gmail.com")
                .role(Role.USER)
                .build();

        Member member = createMember(memberDto);
        memberRepository.save(member);

        BookSaveDto bookDto = BookSaveDto.builder()
                .title("자바 기초")
                .writer("TonyJoo")
                .price(10000)
                .stock(3)
                .status(Status.PERMISSION)
                .build();

        Book book = createBook(bookDto);
        bookRepository.save(book);

        Posts post1 = Posts.builder()
                .title("aaa")
                .content("hello")
                .member(member)
                .book(book)
                .build();

        Posts post2 = Posts.builder()
                .title("bbb")
                .content("hello")
                .member(member)
                .book(book)
                .build();

        postsRepository.save(post1);
        postsRepository.save(post2);

        //when
        List<Posts> posts = postsRepository.findPosts();
        Posts findPost1 = posts.stream()
                .filter(p -> p.getTitle().equals("aaa"))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 게시물이 존재하지 않습니다. title=" + post1.getTitle()));

        Posts findPost2 = posts.stream()
                .filter(p -> p.getTitle().equals("bbb"))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 게시물이 존재하지 않습니다. title=" + post2.getTitle()));

        for (Posts post : posts) {
            System.out.println("post.getTitle() = " + post.getTitle());
        }

        //then
        assertThat(posts.contains(findPost1)).isTrue();
        assertThat(posts.contains(findPost2)).isTrue();

    }

    public Member createMember(MemberDto dto) {
        return Member.builder()
                .name(dto.getName())
                .loginId(dto.getLoginId())
                .password(dto.getPassword())
                .email(dto.getEmail())
                .role(dto.getRole())
                .build();
    }

    public Book createBook(BookSaveDto dto) {
        return Book.builder()
                .title(dto.getTitle())
                .writer(dto.getWriter())
                .price(dto.getPrice())
                .stock(dto.getStock())
                .status(dto.getStatus())
                .build();
    }

}