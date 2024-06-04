package com.rootable.libraryservice2022.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity
                //enable h2-console
                .csrf().disable()

                .headers()
                .frameOptions()
                .sameOrigin()

                //conf STATELESS
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                //conf Access Control
                .and()
                .authorizeHttpRequests()
                .requestMatchers(new AntPathRequestMatcher("/login")).permitAll() //로그인 요청 허용
                .requestMatchers(new AntPathRequestMatcher("/members/add")).permitAll() //회원가입 요청 허용
                .requestMatchers(PathRequest.toH2Console()).permitAll() //h2-console 허용
                .requestMatchers(new AntPathRequestMatcher("/favicon.ico")).permitAll() //favicon 요청 허용
                .anyRequest().authenticated(); //그 외 인증 필요

        return httpSecurity.build();

    }

}
