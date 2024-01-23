package com.rootable.libraryservice2022.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@SequenceGenerator(
        name = "BOOK_SEQ_GENERATOR",
        sequenceName = "BOOK_SEQ",
        initialValue = 5, allocationSize = 1
)
@NoArgsConstructor
public class Book extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BOOK_SEQ_GENERATOR")
    @Column(name = "book_id")
    private Long id;

    private String title;
    private String writer;
    private Integer price;
    private Integer stock;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private List<Posts> postsList = new ArrayList<>();

    @Builder
    public Book(Long id, String title, String writer, Integer price, Integer stock, Status status) {
        this.id = id;
        this.title = title;
        this.writer = writer;
        this.price = price;
        this.stock = stock;
        this.status = status;
    }

    public void update(Integer price, Integer stock, Status status) {
        this.price = price;
        this.stock = stock;
        this.status = status;
    }

}
