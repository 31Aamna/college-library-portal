package com.dbit.libraryapp.dto;

import com.dbit.libraryapp.model.Book;

public record BookResponse(
        Long id, String title, String author, String isbn, String category,
        Integer totalCopies, Integer availableCopies
) {
    public static BookResponse from(Book book) {
        return new BookResponse(book.getId(), book.getTitle(), book.getAuthor(), book.getIsbn(),
                book.getCategory(), book.getTotalCopies(), book.getAvailableCopies());
    }
}
