package com.rootable.libraryservice2022.web.interceptor;

import com.rootable.libraryservice2022.domain.Member;
import com.rootable.libraryservice2022.domain.Role;
import com.rootable.libraryservice2022.jwt.TokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Map;

@Slf4j
public class JwtLoginCheckInterceptor implements HandlerInterceptor {

    public static final String COOKIE_NAME = "login_token";

    @Autowired
    private TokenProvider tokenProvider;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String token = Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(COOKIE_NAME))
                .findFirst().map(Cookie::getValue)
                .orElse("dummy");

        log.info("token : " + token);

        Map<String, Object> jwt = tokenProvider.verify(token);

        Member member = Member.builder()
                .name((String) jwt.get("name"))
                .loginId((String) jwt.get("loginId"))
                .password((String) jwt.get("password"))
                .email((String) jwt.get("email"))
                .role(Role.USER)
                .build();

        request.setAttribute("login", member);

        log.info("JWT 인증이 성공했습니다.");
        return true;

    }
}
