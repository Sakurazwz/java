package com.example.demo5work.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class BorrowRecordDetailDTO {
    private Long id;
    private String bookTitle;
    private String bookIsbn;
    private String borrower;
    private LocalDateTime borrowDate;
    private LocalDateTime returnDate;
    private List<AuthorDTO> authors;
    private String categoryName;
}
