package com.dbit.libraryapp.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record IssueRequest(
        @NotNull Long bookId,
        @NotNull Long memberId,
        @NotNull @FutureOrPresent LocalDate dueDate
) {}
