package com.rootable.libraryservice2022.web.argumentresolver;

import com.rootable.libraryservice2022.domain.Member;
import com.rootable.libraryservice2022.domain.Role;
import com.rootable.libraryservice2022.jwt.TokenProvider;
import com.rootable.libraryservice2022.web.interceptor.JwtLoginCheckInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/*
* 인증된 회원 정보를 편리하게 가져오기 위한 Argument resolver
* */
@Slf4j
public class JwtLoginResolver implements HandlerMethodArgumentResolver {

    @Autowired
    private TokenProvider tokenProvider;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {

        log.info("supportsParameter 실행");

        return parameter.hasParameterAnnotation(Login.class);

    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        log.info("resolveArgument 실행");

        final Map<String, Object> resolved = new HashMap<>();

        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();

        Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(JwtLoginCheckInterceptor.COOKIE_NAME))
                .map(Cookie::getValue).findFirst()
                .ifPresent(token -> {
                    Map<String, Object> jwt = tokenProvider.verify(token);

                    if (parameter.getParameterType().isAssignableFrom(String.class)) {
                        resolved.put("resolved", jwt.get(parameter.getParameterName()));
                    } else if (parameter.getParameterType().isAssignableFrom(Member.class)) {
                        Member member = Member.builder()
                                .name((String) jwt.get("name"))
                                .loginId((String) jwt.get("loginId"))
                                .password((String) jwt.get("password"))
                                .email((String) jwt.get("email"))
                                .role(Role.USER)
                                .build();

                        resolved.put("resolved", member);
                    }
                });

        return resolved.get("resolved");

    }

}
