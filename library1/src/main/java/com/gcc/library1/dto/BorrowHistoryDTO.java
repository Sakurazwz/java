package com.gcc.library1.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BorrowHistoryDTO {
    private Long id;
    private Long bookId;
    private Long userId;
    private String behaviour;
    private LocalDateTime date;
}
