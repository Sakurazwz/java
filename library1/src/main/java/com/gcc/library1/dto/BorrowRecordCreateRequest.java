package com.gcc.library1.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BorrowRecordCreateRequest {
    @NotNull
    private Long bookId;

    private Long userId;
}
