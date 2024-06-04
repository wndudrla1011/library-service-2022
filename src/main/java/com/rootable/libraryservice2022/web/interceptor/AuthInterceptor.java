package com.rootable.libraryservice2022.web.interceptor;

import com.rootable.libraryservice2022.domain.Role;
import com.rootable.libraryservice2022.web.MySecured;
import com.rootable.libraryservice2022.dto.SessionMember;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //handler 종류 확인 -> HandlerMethod 타입인지 체크
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        //HandlerMethod로 형 변환
        HandlerMethod handlerMethod = (HandlerMethod) handler;

        //@MySecured 받아 오기
        MySecured mySecured = handlerMethod.getMethodAnnotation(MySecured.class);

        //메소드에 @MySecured가 없는 경우 (인증이 필요 없는 요청)
        if (mySecured == null) {
            return true;
        }

        //@MySecured (인증이 필요한 요청 처리)

        HttpSession session = request.getSession();
        SessionMember member = (SessionMember) session.getAttribute("loginMember");

        //case ADMIN or STAFF
        String role = mySecured.role().toString();
        log.info("role={}", role);

        if (role != null) {
            if ("ADMIN".equals(role)) {
                //관리자가 아닌 사용자의 접근 -> 리다이렉션
                if (member.getRole() == Role.GUEST || member.getRole() == Role.USER) {
                    log.info("접근을 거부합니다.");
                    response.sendRedirect("/error-403");
                    return false;
                }
            } else if ("STAFF".equals(role)) { //ADMIN 단독 권한
                if (member.getRole() != Role.ADMIN) {
                    log.info("접근을 거부합니다.");
                    response.sendRedirect("/error-403");
                    return false;
                }
            } else if ("GUEST".equals(role)) { //GUEST 이상 권한
                if (member.getRole() == Role.USER) {
                    log.info("접근을 거부합니다.");
                    response.sendRedirect("/error-403");
                    return false;
                }
            }
        }

        //접근 허가
        log.info("접근을 허가합니다.");
        return true;

    }
}
