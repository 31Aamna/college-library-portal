package com.dbit.libraryapp.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BookRequest(
        @NotBlank @Size(max = 160) String title,
        @NotBlank @Size(max = 120) String author,
        @NotBlank @Size(max = 30) String isbn,
        @NotBlank @Size(max = 80) String category,
        @Min(1) Integer totalCopies
) {}
