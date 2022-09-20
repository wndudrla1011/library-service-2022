package com.rootable.libraryservice2022.web.dto;

import com.rootable.libraryservice2022.domain.Status;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

@Data
public class BookSaveForm {

    @NotNull(message = "도서 제목을 입력해주세요")
    private String title;

    @NotNull(message = "저자를 입력해주세요")
    private String writer;

    @NotNull(message = "가격을 입력해주세요")
    @Range(min = 1000, max = 100000)
    private Integer price;

    @NotNull(message = "재고를 입력해주세요")
    @Max(999)
    private Integer stock;

    @NotNull
    private Status status;

}
