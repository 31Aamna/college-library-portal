package com.dbit.libraryapp.service;

import com.dbit.libraryapp.dto.IssueRequest;
import com.dbit.libraryapp.dto.TransactionResponse;
import com.dbit.libraryapp.exception.BadRequestException;
import com.dbit.libraryapp.exception.ResourceNotFoundException;
import com.dbit.libraryapp.model.Book;
import com.dbit.libraryapp.model.LibraryTransaction;
import com.dbit.libraryapp.model.Member;
import com.dbit.libraryapp.model.TransactionStatus;
import com.dbit.libraryapp.repository.BookRepository;
import com.dbit.libraryapp.repository.MemberRepository;
import com.dbit.libraryapp.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {
    @Mock TransactionRepository transactionRepository;
    @Mock BookRepository bookRepository;
    @Mock MemberRepository memberRepository;
    private TransactionService service;

    @BeforeEach
    void setUp() { service = new TransactionService(transactionRepository, bookRepository, memberRepository, new BigDecimal("2.50")); }

    @Test
    void issueDecrementsAvailabilityAndCreatesIssuedTransaction() {
        Book book = book(11L, 2, 2); Member member = member(5L);
        when(bookRepository.findById(11L)).thenReturn(Optional.of(book));
        when(memberRepository.findById(5L)).thenReturn(Optional.of(member));
        when(transactionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        TransactionResponse result = service.issue(new IssueRequest(11L, 5L, LocalDate.now().plusDays(7)));

        assertEquals(1, book.getAvailableCopies());
        assertEquals(TransactionStatus.ISSUED, result.status());
        verify(bookRepository).save(book);
        verify(transactionRepository).save(any(LibraryTransaction.class));
    }

    @Test
    void returnCalculatesFineFromOverdueDays() {
        Book book = book(11L, 2, 1); Member member = member(5L);
        LibraryTransaction transaction = new LibraryTransaction();
        transaction.setId(8L); transaction.setBook(book); transaction.setMember(member);
        transaction.setIssueDate(LocalDate.now().minusDays(10));
        transaction.setDueDate(LocalDate.now().minusDays(3));
        transaction.setStatus(TransactionStatus.ISSUED); transaction.setFine(BigDecimal.ZERO);
        when(transactionRepository.findById(8L)).thenReturn(Optional.of(transaction));
        when(transactionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        TransactionResponse result = service.returnBook(8L);

        assertEquals(TransactionStatus.RETURNED, result.status());
        assertEquals(new BigDecimal("7.50"), result.fine());
        assertEquals(2, book.getAvailableCopies());
        verify(bookRepository).save(book);
    }

    @Test
    void rejectsUnavailableBookWithoutCreatingTransaction() {
        Book book = book(11L, 1, 0);
        when(bookRepository.findById(11L)).thenReturn(Optional.of(book));
        when(memberRepository.findById(5L)).thenReturn(Optional.of(member(5L)));

        assertThrows(BadRequestException.class, () -> service.issue(new IssueRequest(11L, 5L, LocalDate.now().plusDays(7))));
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void rejectsUnknownMemberBeforeChangingCopies() {
        Book book = book(11L, 1, 1);
        when(bookRepository.findById(11L)).thenReturn(Optional.of(book));
        when(memberRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.issue(new IssueRequest(11L, 999L, LocalDate.now().plusDays(7))));
        assertEquals(1, book.getAvailableCopies());
        verify(transactionRepository, never()).save(any());
    }

    private Book book(Long id, int total, int available) {
        Book book = new Book(); book.setId(id); book.setTitle("Test Book"); book.setTotalCopies(total); book.setAvailableCopies(available); return book;
    }
    private Member member(Long id) {
        Member member = new Member(); member.setId(id); member.setMemberCode("LIB-TEST"); member.setName("Test Reader"); return member;
    }
}
