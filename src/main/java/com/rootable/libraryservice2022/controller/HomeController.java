package com.rootable.libraryservice2022.controller;

import com.rootable.libraryservice2022.domain.Role;
import com.rootable.libraryservice2022.web.argumentresolver.Login;
import com.rootable.libraryservice2022.dto.SessionMember;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class HomeController {

    public static String ADMIN_KEY = "admin";

    @GetMapping("/")
    public String homeLogin(@Login SessionMember loginMember, Model model) {

        log.info("홈 화면 이동");

        //세션에 회원 데이터가 없으면 intro 화면으로
        if (loginMember == null) {
            return "index";
        }

        if (loginMember.getRole() == Role.ADMIN || loginMember.getRole() == Role.STAFF) {
            model.addAttribute("admin", ADMIN_KEY);
        }

        //회원 데이터가 있으면 로그인 화면으로
        model.addAttribute("member", loginMember);
        return "loginHome";

    }

}
