package com.rootable.libraryservice2022.controller;

import com.rootable.libraryservice2022.domain.Member;
import com.rootable.libraryservice2022.domain.Role;
import com.rootable.libraryservice2022.service.MemberService;
import com.rootable.libraryservice2022.web.MySecured;
import com.rootable.libraryservice2022.web.argumentresolver.Login;
import com.rootable.libraryservice2022.dto.MemberDto;
import com.rootable.libraryservice2022.dto.SessionMember;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    public static String COMMON_MEMBER_KEY = "common";

    @GetMapping("/members/add")
    public String joinForm(@ModelAttribute("member") Member member) {

        log.info("회원 가입 폼 이동");
        return "members/signup";

    }

    @PostMapping("/members/add")
    public String join(@Valid @ModelAttribute("member") MemberDto dto, BindingResult bindingResult) {

        log.info("회원 가입 검증");

        dto.setRole(Role.USER);

        duplicationCheckLoginId(dto, bindingResult); //중복 아이디 검증
        duplicationCheckEmail(dto, bindingResult); //중복 이메일 검증

        if (bindingResult.hasErrors()) {
            log.info("검증 에러 errors={}", bindingResult);
            return "members/signup";
        }

        log.info("정상 입력으로 회원 가입 진행");
        memberService.join(dto); //회원 가입
        return "redirect:/";

    }

    @MySecured(role = Role.STAFF)
    @GetMapping("/admin/members")
    public String members(Model model, @Login SessionMember loginMember) {

        log.info("회원 관리 페이지");

        List<Member> members = memberService.findMembers();
        model.addAttribute("members", members);

        model.addAttribute("loginMember", loginMember);

        return "members/members";

    }

    @MySecured(role = Role.STAFF)
    @GetMapping("/admin/members/{memberId}")
    public String member(@PathVariable Long memberId, Model model) {

        log.info("회원 정보");

        Member member = memberService.findOne(memberId);

        model.addAttribute("member", member);
        return "members/memberInfo";

    }

    @MySecured(role = Role.STAFF)
    @GetMapping("/admin/members/{memberId}/edit")
    public String updateForm(@PathVariable Long memberId, Model model) {

        log.info("회원 수정 화면 이동");

        Member member = memberService.findOne(memberId);

        model.addAttribute("member", member);
        model.addAttribute("roles", Role.values());
        return "members/editMember";

    }

    @MySecured(role = Role.STAFF)
    @PutMapping("/admin/members/{memberId}/edit")
    public String edit(@PathVariable Long memberId, @Valid @ModelAttribute("member") MemberDto dto,
                       BindingResult bindingResult, Model model){

        log.info("회원 정보 수정 검증");

        model.addAttribute("roles", Role.values());

        duplicationCheckUpdateLoginId(memberId, dto, bindingResult); //중복 아이디 검증

        if (dto.getRole() == null) {
            bindingResult.reject("roleNull");
        }

        if (bindingResult.hasErrors()) {
            log.info("검증 에러 errors={}", bindingResult);
            return "members/editMember";
        }

        log.info("회원 정보 수정 진행");

        memberService.update(memberId, dto); //회원 정보 갱신

        return "redirect:/admin/members";

    }

    @MySecured(role = Role.STAFF)
    @DeleteMapping("/admin/members")
    public String delete(@RequestParam("memberId") Long memberId) {

        log.info("회원 삭제");

        Member member = memberService.findOne(memberId);
        memberService.delete(member.getId());

        return "redirect:/admin/members";

    }

    @GetMapping("/members/{memberId}")
    public String myInfo(@PathVariable Long memberId, Model model) {

        log.info("내 정보 조회");

        Member member = memberService.findOne(memberId);

        model.addAttribute("member", member);
        model.addAttribute("common", COMMON_MEMBER_KEY); //작성자 키 발급
        return "members/memberInfo";

    }

    @GetMapping("/members/{memberId}/edit")
    public String myInfoForm(@PathVariable Long memberId, Model model) {

        log.info("내 정보 수정 폼 이동");

        Member member = memberService.findOne(memberId);

        model.addAttribute("member", member);
        model.addAttribute("common", COMMON_MEMBER_KEY); //작성자 키 발급

        return "members/editMember";

    }

    @PutMapping("/members/{memberId}/edit")
    public String editMyInfo(@PathVariable Long memberId, @Valid @ModelAttribute("member") MemberDto dto,
                       BindingResult bindingResult, Model model){

        log.info("내 정보 수정 검증");

        duplicationCheckUpdateLoginId(memberId, dto, bindingResult); //중복 아이디 검증

        //DTO Role 셋팅
        Member member = memberService.findOne(memberId);
        dto.setRole(member.getRole());

        if (bindingResult.hasErrors()) {
            log.info("검증 에러 errors={}", bindingResult);
            model.addAttribute("common", COMMON_MEMBER_KEY); //작성자 키 발급
            return "members/editMember";
        }

        log.info("내 정보 수정 진행");

        memberService.update(memberId, dto); //회원 정보 갱신

        model.addAttribute("common", COMMON_MEMBER_KEY); //작성자 키 발급
        return "redirect:/members/" + memberId;

    }

    //수정 -> ID 중복 확인
    private void duplicationCheckUpdateLoginId(Long memberId, MemberDto form, BindingResult bindingResult) {

        if (memberService.checkDuplicatedLoginId(memberId, form.getLoginId()) != null) {
            log.info(">>> 로그인 ID 중복");
            bindingResult.rejectValue("loginId", "overlap");
        }

    }

    //회원 ID 중복 확인
    private void duplicationCheckLoginId(MemberDto member, BindingResult bindingResult) {

        if (memberService.findByLoginId(member.getLoginId()) != null) {
            log.info(">>> 로그인 ID 중복");
            bindingResult.rejectValue("loginId", "overlap");
        }

    }

    //회원 이메일 중복 확인
    private void duplicationCheckEmail(MemberDto member, BindingResult bindingResult) {

        if (memberService.findByEmail(member.getEmail()) != null) {
            log.info(">>> 로그인 Email 중복");
            bindingResult.rejectValue("email", "duplication");
        }

    }

}
