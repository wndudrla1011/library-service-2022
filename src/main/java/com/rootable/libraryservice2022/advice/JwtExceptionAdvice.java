package com.rootable.libraryservice2022.advice;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SecurityException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@ControllerAdvice
public class JwtExceptionAdvice {

    @ExceptionHandler({SecurityException.class, MalformedJwtException.class, ExpiredJwtException.class,
                       UnsupportedJwtException.class, JwtException.class})
    public ModelAndView handleJwtException() {

        log.info("JWT 관련 예외 처리");

        return new ModelAndView("login/signIn");

    }

}
