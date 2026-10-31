package com.dbit.libraryapp.service;

import com.dbit.libraryapp.dto.DashboardResponse;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    private final BigDecimal fineRate;

    public TransactionService(TransactionRepository transactionRepository, BookRepository bookRepository,
                              MemberRepository memberRepository,
                              @Value("${library.fine-rate:1.50}") BigDecimal fineRate) {
        this.transactionRepository = transactionRepository;
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
        this.fineRate = fineRate.setScale(2, RoundingMode.HALF_UP);
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> history(String status, boolean overdueOnly) {
        if (overdueOnly) {
            return transactionRepository.findOverdue(LocalDate.now()).stream()
                    .map(t -> TransactionResponse.from(t, LocalDate.now())).toList();
        }
        TransactionStatus parsed = null;
        if (status != null && !status.isBlank()) {
            try { parsed = TransactionStatus.valueOf(status.toUpperCase()); }
            catch (IllegalArgumentException ex) { throw new BadRequestException("Status must be ISSUED or RETURNED."); }
        }
        LocalDate today = LocalDate.now();
        return transactionRepository.findHistory(parsed).stream().map(t -> TransactionResponse.from(t, today)).toList();
    }

    @Transactional
    public TransactionResponse issue(IssueRequest request) {
        Book book = bookRepository.findById(request.bookId())
                .orElseThrow(() -> new ResourceNotFoundException("Book not found: " + request.bookId()));
        Member member = memberRepository.findById(request.memberId())
                .orElseThrow(() -> new ResourceNotFoundException("Member not found: " + request.memberId()));
        if (book.getAvailableCopies() < 1) throw new BadRequestException("No available copies remain for this book.");
        if (request.dueDate().isBefore(LocalDate.now())) throw new BadRequestException("Due date cannot be in the past.");

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);
        LibraryTransaction transaction = new LibraryTransaction();
        transaction.setBook(book);
        transaction.setMember(member);
        transaction.setIssueDate(LocalDate.now());
        transaction.setDueDate(request.dueDate());
        transaction.setStatus(TransactionStatus.ISSUED);
        transaction.setFine(BigDecimal.ZERO.setScale(2));
        return TransactionResponse.from(transactionRepository.save(transaction), LocalDate.now());
    }

    @Transactional
    public TransactionResponse returnBook(Long id) {
        LibraryTransaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found: " + id));
        if (transaction.getStatus() == TransactionStatus.RETURNED) {
            throw new BadRequestException("This transaction has already been returned.");
        }
        LocalDate today = LocalDate.now();
        long overdueDays = Math.max(0, java.time.temporal.ChronoUnit.DAYS.between(transaction.getDueDate(), today));
        transaction.setReturnDate(today);
        transaction.setStatus(TransactionStatus.RETURNED);
        transaction.setFine(fineRate.multiply(BigDecimal.valueOf(overdueDays)).setScale(2, RoundingMode.HALF_UP));
        Book book = transaction.getBook();
        book.setAvailableCopies(Math.min(book.getTotalCopies(), book.getAvailableCopies() + 1));
        bookRepository.save(book);
        return TransactionResponse.from(transactionRepository.save(transaction), today);
    }

    @Transactional(readOnly = true)
    public DashboardResponse dashboard() {
        long totalBooks = bookRepository.count();
        long availableCopies = bookRepository.findAll().stream().mapToLong(Book::getAvailableCopies).sum();
        long registeredMembers = memberRepository.count();
        long issued = transactionRepository.countByStatus(TransactionStatus.ISSUED);
        long overdue = transactionRepository.countByStatusAndDueDateBefore(TransactionStatus.ISSUED, LocalDate.now());
        return new DashboardResponse(totalBooks, availableCopies, registeredMembers, issued, overdue);
    }

    public BigDecimal fineRate() {
        return fineRate;
    }
}
