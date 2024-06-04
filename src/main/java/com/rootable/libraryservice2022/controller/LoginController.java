package com.rootable.libraryservice2022.controller;

import com.rootable.libraryservice2022.domain.Member;
import com.rootable.libraryservice2022.service.LoginService;
import com.rootable.libraryservice2022.dto.LoginDto;
import com.rootable.libraryservice2022.dto.SessionMember;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;
    private final HttpSession httpSession;

    @GetMapping("/login")
    public String loginForm(@ModelAttribute("loginForm") LoginDto dto) {
        log.info("로그인 폼 이동");
        return "login/signIn";
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute("loginForm") LoginDto dto, BindingResult bindingResult,
                        @RequestParam(defaultValue = "/") String redirectURL) {

        log.info("로그인 검증");

        if (bindingResult.hasErrors()) {
            log.info("검증 에러 errors={}", bindingResult);
            return "login/signIn";
        }

        //로그인 유효성 검사
        Member loginMember = loginService.validationLogin(dto.getLoginId(), dto.getPassword());
        log.info("Login Member = {}", loginMember);

        if (loginMember == null) {
            bindingResult.reject("loginFail");
            return "login/signIn";
        }

        //로그인 성공 처리
        log.info("정상 입력으로 로그인 성공");

        httpSession.setAttribute("loginMember", new SessionMember(loginMember)); //세션에 회원 정보 저장


        return "redirect:" + redirectURL; //로그인 후 최초 위치로 돌아가도록

    }

    @PostMapping("/logout")
    public String logout() {

        log.info("로그아웃");

        //세션 삭제
        if (httpSession != null) {
            httpSession.invalidate();
        }

        return "redirect:/";

    }

}
