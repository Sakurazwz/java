package com.gcc.library1.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookCreateRequest {
    private String isbn;

    @NotBlank
    private String title;

    @NotBlank
    private String author;

    private String description;

    private String cover;

    private String category;

    @PositiveOrZero
    private int count;
}
