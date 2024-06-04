package com.rootable.libraryservice2022.service;

import com.rootable.libraryservice2022.domain.Book;
import com.rootable.libraryservice2022.repository.BookRepository;
import com.rootable.libraryservice2022.dto.BookSaveDto;
import com.rootable.libraryservice2022.dto.BookUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    /*
     * 도서 등록
     * */
    @Transactional
    public Long create(BookSaveDto dto) {
        return bookRepository.save(dto.toEntity()).getId();
    }

    /*
     * 전체 도서 조회
     * */
    public List<Book> books() {
        return bookRepository.findAll();
    }

    /*
     * 도서 조회
     * */
    public Book findOne(Long id) {

        return bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 도서가 존재하지 않습니다. id=" + id));

    }

    /*
    * 도서 수정
    * */
    @Transactional
    public Long update(Long bookId, BookUpdateDto bookParam) {

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("해당 도서가 존재하지 않습니다. id=" + bookId));

        book.update(bookParam.getPrice(), bookParam.getStock(), bookParam.getStatus());

        return bookId;

    }

    /*
     * 도서 삭제
     * */
    @Transactional
    public void delete(Long bookId) {

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("해당 도서가 존재하지 않습니다. id=" + bookId));

        bookRepository.delete(book);

    }

    /*
     * 도서 조회 by 제목
     * */
    public Book findByTitle(String title) {

        Book book = bookRepository.findByTitle(title);

        if (book == null) {
            throw new IllegalArgumentException("해당 도서가 존재하지 않습니다. title=" + title);
        }

        return book;

    }

}
