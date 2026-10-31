package com.dbit.libraryapp.service;

import com.dbit.libraryapp.dto.BookRequest;
import com.dbit.libraryapp.exception.BadRequestException;
import com.dbit.libraryapp.model.Book;
import com.dbit.libraryapp.repository.BookRepository;
import com.dbit.libraryapp.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {
    @Mock BookRepository bookRepository;
    @Mock TransactionRepository transactionRepository;
    private BookService service;

    @BeforeEach
    void setUp() { service = new BookService(bookRepository, transactionRepository); }

    @Test
    void rejectsInvalidCopyCount() {
        assertThrows(BadRequestException.class, () -> service.create(new BookRequest("Title", "Author", "ISBN", "Category", 0)));
        verifyNoInteractions(bookRepository, transactionRepository);
    }

    @Test
    void rejectsDuplicateIsbn() {
        when(bookRepository.existsByIsbnIgnoreCase("ISBN")).thenReturn(true);
        assertThrows(BadRequestException.class, () -> service.create(new BookRequest("Title", "Author", "ISBN", "Category", 1)));
        verify(bookRepository).existsByIsbnIgnoreCase("ISBN");
        verify(bookRepository, never()).save(any());
    }

    @Test
    void removesHistoricalTransactionsOnlyAfterActiveCheckPasses() {
        Book book = new Book(); book.setId(7L); book.setTotalCopies(1); book.setAvailableCopies(1);
        when(bookRepository.findById(7L)).thenReturn(Optional.of(book));
        when(transactionRepository.existsByBookIdAndStatus(any(), any())).thenReturn(false);
        service.delete(7L);
        verify(transactionRepository).deleteByBookId(7L);
        verify(bookRepository).delete(book);
    }
}
