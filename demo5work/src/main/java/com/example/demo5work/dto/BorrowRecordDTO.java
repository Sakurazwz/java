package com.example.demo5work.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BorrowRecordDTO {
    private Long id;
    private String bookTitle;
    private String borrower;
    private LocalDateTime borrowDate;
    private LocalDateTime returnDate;
}