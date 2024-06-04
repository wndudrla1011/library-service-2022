package com.rootable.libraryservice2022.dto;

import com.rootable.libraryservice2022.domain.Member;
import com.rootable.libraryservice2022.domain.Role;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

//세션에 사용자 정보를 저장하기 위한 DTO + 직렬화 기능
@Data
@NoArgsConstructor
public class SessionMember implements Serializable {

    private Long id;
    private String name;
    private String loginId;
    private String password;
    private String email;
    private Role role;

    public SessionMember(Member member) {
        this.id = member.getId();
        this.name = member.getName();
        this.loginId = member.getLoginId();
        this.password = member.getPassword();
        this.email = member.getEmail();
        this.role = member.getRole();
    }

}
