package com.rootable.libraryservice2022.service;

import com.rootable.libraryservice2022.domain.Book;
import com.rootable.libraryservice2022.domain.Status;
import com.rootable.libraryservice2022.repository.BookRepository;
import com.rootable.libraryservice2022.dto.BookSaveDto;
import com.rootable.libraryservice2022.dto.BookUpdateDto;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class BookServiceTest {

    @Autowired
    BookRepository bookRepository;

    @Autowired
    BookService bookService;

    @Autowired
    EntityManager entityManager;

    @Test
    public void create() {

        //given
        BookSaveDto dto = BookSaveDto.builder()
                .title("book1")
                .writer("a")
                .price(10000)
                .stock(2)
                .status(Status.PERMISSION)
                .build();

        //when
        Long savedId = bookService.create(dto);

        //then
        List<Book> books = bookRepository.findAll();
        Book findBook = books.stream()
                .filter(b -> b.getId().equals(savedId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 도서가 존재하지 않습니다. id+" + savedId));

        assertThat(findBook).isNotNull();

    }

    @Test
    public void findOne() {

        //given
        BookSaveDto dto = BookSaveDto.builder()
                .title("book1")
                .writer("a")
                .price(10000)
                .stock(2)
                .status(Status.PERMISSION)
                .build();

        Long savedId = bookService.create(dto);

        //when
        Book savedBook = bookService.findOne(savedId);

        //then
        assertThat(entityManager.contains(savedBook)).isTrue();

    }


    @Test(expected = IllegalArgumentException.class)
    @DisplayName("존재하지 않는 책 조회")
    public void notFindOne() {

        //given
        BookSaveDto dto = BookSaveDto.builder()
                .title("book1")
                .writer("a")
                .price(10000)
                .stock(2)
                .status(Status.PERMISSION)
                .build();

        Long savedId = bookService.create(dto);

        //when
        bookService.findOne(savedId);
        bookService.findByTitle("book2");

        //then
        fail("존재하지 않는 책 조회로 예외가 발생해야 한다.");

    }

    @Test
    public void update() {

        //given
        BookSaveDto saveDto = BookSaveDto.builder()
                .id(1L)
                .title("book1")
                .writer("a")
                .price(10000)
                .stock(2)
                .status(Status.PERMISSION)
                .build();

        BookUpdateDto updateDto = createUpdateForm();
        Long savedId = bookService.create(saveDto);

        //when
        bookService.update(savedId, updateDto);
        Book book = bookService.findOne(savedId);

        //then
        assertThat(book.getPrice()).isEqualTo(updateDto.getPrice());
        assertThat(book.getStock()).isEqualTo(updateDto.getStock());
        assertThat(book.getStatus()).isEqualTo(updateDto.getStatus());

    }

    private BookUpdateDto createUpdateForm() {
        BookUpdateDto book2 = new BookUpdateDto();
        book2.setPrice(20000);
        book2.setStock(4);
        book2.setStatus(Status.PERMISSION);
        return book2;
    }

    @Test
    public void delete() {

        //given
        BookSaveDto dto = BookSaveDto.builder()
                .id(1L)
                .title("book1")
                .writer("a")
                .price(10000)
                .stock(2)
                .status(Status.PERMISSION)
                .build();

        Long savedId = bookService.create(dto);

        //when
        Book book = bookService.findOne(savedId);
        bookService.delete(savedId);

        //then
        assertThat(entityManager.contains(book)).isFalse();

    }

    @Test
    @DisplayName("도서 제목으로 조회")
    public void findByTitle() {

        //given
        BookSaveDto dto = BookSaveDto.builder()
                .id(1L)
                .title("book1")
                .writer("a")
                .price(10000)
                .stock(2)
                .status(Status.PERMISSION)
                .build();

        //when
        bookService.create(dto);
        Book savedTitle = bookService.findByTitle(dto.getTitle());

        //then
        assertThat(savedTitle.getTitle()).isEqualTo(dto.getTitle());

    }

}