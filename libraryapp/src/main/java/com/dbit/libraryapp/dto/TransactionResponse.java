package com.dbit.libraryapp.dto;

import com.dbit.libraryapp.model.LibraryTransaction;
import com.dbit.libraryapp.model.TransactionStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionResponse(
        Long id, Long bookId, String bookTitle, Long memberId, String memberCode,
        String memberName, LocalDate issueDate, LocalDate dueDate, LocalDate returnDate,
        TransactionStatus status, BigDecimal fine, boolean overdue
) {
    public static TransactionResponse from(LibraryTransaction t, LocalDate today) {
        boolean overdue = t.getStatus() == TransactionStatus.ISSUED && t.getDueDate().isBefore(today);
        return new TransactionResponse(t.getId(), t.getBook().getId(), t.getBook().getTitle(),
                t.getMember().getId(), t.getMember().getMemberCode(), t.getMember().getName(),
                t.getIssueDate(), t.getDueDate(), t.getReturnDate(), t.getStatus(), t.getFine(), overdue);
    }
}
