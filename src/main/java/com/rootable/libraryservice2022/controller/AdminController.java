package com.rootable.libraryservice2022.controller;

import com.rootable.libraryservice2022.domain.Role;
import com.rootable.libraryservice2022.web.MySecured;
import com.rootable.libraryservice2022.web.argumentresolver.Login;
import com.rootable.libraryservice2022.dto.SessionMember;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AdminController {

    @MySecured(role = Role.ADMIN)
    @GetMapping("/admin")
    public String admin(Model model, @Login SessionMember loginMember) {

        log.info("관리자 페이지 접근");

        model.addAttribute("member", loginMember);
        return "loginHomeAdmin";

    }

}
