package com.gcc.library1.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookUpdateRequest {
    @NotNull
    private Long id;

    private String isbn;

    @NotBlank
    private String title;

    @NotBlank
    private String author;

    private String description;

    private String cover;

    private String category;

    private int count;
}
