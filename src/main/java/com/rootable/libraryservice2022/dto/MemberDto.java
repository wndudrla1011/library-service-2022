package com.rootable.libraryservice2022.dto;

import com.rootable.libraryservice2022.domain.Member;
import com.rootable.libraryservice2022.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberDto {

    private Long id;

    @NotBlank(message = "이름은 필수 입력 값입니다.")
    private String name;

    @Pattern(regexp = "^[a-z0-9]{4,20}$",
            message = "아이디는 영문 대소문자와 숫자만 허용되며 4~20 자리여야 합니다.")
    private String loginId;

    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,16}$",
            message = "비밀번호 영문 대소문자, 숫자, 특수문자를 1개 이상 포함해야 하며 8~16자리여야 합니다.")
    private String password;

    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    private String email;

    private Role role;

    public Member toEntity() {
        Member build = Member.builder()
                .id(id)
                .name(name)
                .loginId(loginId)
                .password(password)
                .email(email)
                .role(role)
                .build();
        return build;
    }

}
