package com.dbit.libraryapp.dto;

public record DashboardResponse(
        long totalBooks,
        long availableCopies,
        long registeredMembers,
        long issuedBooks,
        long overdueBooks
) {}
