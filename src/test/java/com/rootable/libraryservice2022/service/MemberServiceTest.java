package com.rootable.libraryservice2022.service;

import com.rootable.libraryservice2022.domain.Member;
import com.rootable.libraryservice2022.domain.Role;
import com.rootable.libraryservice2022.dto.MemberDto;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @Test
    public void join() {

        //given
        MemberDto memberDto = MemberDto.builder()
                .name("kim")
                .loginId("test2")
                .password("1111@@gg")
                .email("ss@gmail.com")
                .role(Role.USER)
                .build();

        //when
        Long savedId = memberService.join(memberDto);
        MemberDto savedMemberDto = memberService.getMember(savedId);

        //then
        assertEquals(memberDto, savedMemberDto);

    }

    @Test
    public void update() {

        String loginId1 = "test2";
        String loginId2 = "test3";

        //given
        MemberDto member1 = MemberDto.builder()
                .id(1L)
                .name("kim")
                .loginId(loginId1)
                .password("1111!!ee")
                .email("ssss@gmail.com")
                .role(Role.USER)
                .build();

        MemberDto member2 = MemberDto.builder()
                .id(2L)
                .name("kim")
                .loginId(loginId2)
                .password("1111!!ee")
                .email("ddss@gmail.com")
                .role(Role.USER)
                .build();

        Long savedId = memberService.join(member1);

        //when
        Long updatedId = memberService.update(savedId, member2);
        MemberDto updatedMember = memberService.getMember(updatedId);

        //then
        assertThat(updatedMember.getLoginId()).isEqualTo(loginId2);

    }

    @Test
    @DisplayName("존재하지 않는 회원 조회")
    public void notFineOne() {

        //given
        MemberDto memberDto = MemberDto.builder()
                .name("kim")
                .loginId("test2")
                .password("1111!!ee")
                .email("oooo@gmail.com")
                .role(Role.USER)
                .build();

        memberService.join(memberDto);

        //when
        Member unsavedId = memberService.findByLoginId("joo");
        Member unsavedEmail = memberService.findByEmail("qqqq@gmail.com");

        //then
        assertThat(unsavedId).isNull();
        assertThat(unsavedEmail).isNull();

    }

}