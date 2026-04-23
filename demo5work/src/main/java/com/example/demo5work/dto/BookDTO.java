package com.example.demo5work.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class BookDTO {
    private Long id;
    private String title;
    private String isbn;
    private LocalDate publishDate;
    private BigDecimal price;
}
