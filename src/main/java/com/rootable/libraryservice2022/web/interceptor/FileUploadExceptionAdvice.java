package com.rootable.libraryservice2022.web.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@ControllerAdvice
public class FileUploadExceptionAdvice {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ModelAndView handleMaxSizeException(MaxUploadSizeExceededException exc,
                                               HttpServletRequest request, HttpServletResponse response) {

        log.info("파일 용량 초과 예외 처리");

        ModelAndView mav = new ModelAndView("error/fileExceededError");
        mav.getModel().put("message", "File too large!");
        return mav;

    }

}
