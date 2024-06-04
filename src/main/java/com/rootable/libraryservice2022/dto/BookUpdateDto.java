package com.rootable.libraryservice2022.dto;

import com.rootable.libraryservice2022.domain.Status;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@Data
@NoArgsConstructor
public class BookUpdateDto {

    @NotNull(message = "가격을 입력해주세요")
    @Range(min = 1000, max = 100000)
    private Integer price;

    //수정에서는 재고 범위 자유
    @NotNull(message = "재고를 입력해주세요")
    @PositiveOrZero
    private Integer stock;

    private Status status;

}
