package com.rootable.libraryservice2022.dto;

import com.rootable.libraryservice2022.domain.Book;
import com.rootable.libraryservice2022.domain.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookSaveDto {

    private Long id;

    @NotBlank(message = "책 제목을 입력해주세요")
    private String title;

    @NotBlank(message = "저자를 입력해주세요")
    private String writer;

    @NotNull(message = "가격을 입력해주세요")
    @Range(min = 1000, max = 100000)
    private Integer price;

    @NotNull(message = "재고를 입력해주세요")
    @PositiveOrZero
    @Max(999)
    private Integer stock;

    @NotNull(message = "상태를 입력해주세요")
    private Status status;

    public Book toEntity() {
        Book build = Book.builder()
                .id(id)
                .title(title)
                .writer(writer)
                .price(price)
                .stock(stock)
                .status(status)
                .build();

        return build;
    }

}
