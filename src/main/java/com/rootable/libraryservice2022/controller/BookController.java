package com.rootable.libraryservice2022.controller;

import com.rootable.libraryservice2022.domain.Book;
import com.rootable.libraryservice2022.domain.Role;
import com.rootable.libraryservice2022.domain.Status;
import com.rootable.libraryservice2022.service.BookService;
import com.rootable.libraryservice2022.web.MySecured;
import com.rootable.libraryservice2022.web.dto.BookSaveDto;
import com.rootable.libraryservice2022.web.dto.BookUpdateDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @MySecured(role = Role.ADMIN)
    @GetMapping("/admin/books")
    public String books(Model model) {

        log.info("도서 관리 페이지");

        List<Book> books = bookService.books();
        model.addAttribute("books", books);

        return "books/books";

    }

    @MySecured(role = Role.ADMIN)
    @GetMapping("/admin/books/add")
    public String bookForm(Model model) {

        log.info("도서 등록 폼 이동");

        model.addAttribute("book", new Book());
        model.addAttribute("statusList", Status.values());
        return "books/addBook";

    }

    @MySecured(role = Role.ADMIN)
    @PostMapping("/admin/books/add")
    public String create(@Validated @ModelAttribute("book") BookSaveDto dto, BindingResult bindingResult, Model model) {

        log.info("도서 등록 검증");

        model.addAttribute("statusList", Status.values());

        if (dto.getStock() != null && dto.getStatus() != null) {
            if (dto.getStock() > 0 && dto.getStatus() == Status.DENIED) {
                bindingResult.reject("invalid");
            }
        }

        if (dto.getStatus() == null) {
            bindingResult.reject("statusNull");
        }

        if (bindingResult.hasErrors()) {
            log.info("검증 에러 errors={}", bindingResult);
            return "books/addBook";
        }

        log.info("정상 입력으로 도서 등록 진행");

        Book book = Book.builder()
                .title(dto.getTitle())
                .writer(dto.getWriter())
                .price(dto.getPrice())
                .stock(dto.getStock())
                .status(dto.getStatus())
                .build();

        Long savedId = bookService.create(book);

        return "redirect:/admin/books/" + savedId;

    }

    @MySecured(role = Role.ADMIN)
    @GetMapping("/admin/books/{bookId}")
    public String book(@PathVariable Long bookId, Model model) {

        log.info("도서 정보");

        Book findBook = bookService.findOne(bookId);

        model.addAttribute("book", findBook);
        return "books/bookInfo";

    }

    @MySecured(role = Role.ADMIN)
    @GetMapping("/admin/books/{bookId}/edit")
    public String editForm(@PathVariable Long bookId, Model model) {

        log.info("도서 수정 폼 이동");

        Book book = bookService.findOne(bookId);

        model.addAttribute("book", book);
        model.addAttribute("statusList", Status.values());
        return "books/editBook";

    }

    @MySecured(role = Role.ADMIN)
    @PostMapping("/admin/books/{bookId}/edit")
    public String edit(@PathVariable Long bookId, @Validated @ModelAttribute("book") BookUpdateDto dto,
                       BindingResult bindingResult, Model model) {

        log.info("도서 정보 수정 검증");

        model.addAttribute("statusList", Status.values());

        if (dto.getStock() != null && dto.getStatus() != null) {
            if (dto.getStock() > 0 && dto.getStatus() == Status.DENIED) {
                bindingResult.reject("invalid");
            }
        }

        if (dto.getStatus() == null) {
            bindingResult.reject("statusNull");
        }

        if (bindingResult.hasErrors()) {
            log.info("검증 에러 errors={}", bindingResult);
            return "books/editBook";
        }

        log.info("도서 정보 수정 진행");

        bookService.update(bookId, dto);
        return "redirect:/admin/books/" + bookId;

    }

    @MySecured(role = Role.ADMIN)
    @DeleteMapping("/admin/books/{bookId}")
    public String delete(@PathVariable Long bookId) {

        log.info("도서 삭제");

        bookService.delete(bookId);

        return "redirect:/admin/books";

    }

}
