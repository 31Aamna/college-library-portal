package com.dbit.libraryapp.service;

import com.dbit.libraryapp.dto.BookRequest;
import com.dbit.libraryapp.dto.BookResponse;
import com.dbit.libraryapp.exception.BadRequestException;
import com.dbit.libraryapp.exception.ResourceNotFoundException;
import com.dbit.libraryapp.model.Book;
import com.dbit.libraryapp.model.TransactionStatus;
import com.dbit.libraryapp.repository.BookRepository;
import com.dbit.libraryapp.repository.TransactionRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookService {
    private final BookRepository bookRepository;
    private final TransactionRepository transactionRepository;

    public BookService(BookRepository bookRepository, TransactionRepository transactionRepository) {
        this.bookRepository = bookRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional(readOnly = true)
    public List<BookResponse> findAll(String search, String category) {
        List<Book> books = search == null || search.isBlank()
                ? bookRepository.findAllByOrderByTitleAsc()
                : bookRepository.search(search.trim());
        if (category != null && !category.isBlank()) {
            books = books.stream().filter(book -> book.getCategory().equalsIgnoreCase(category.trim())).toList();
        }
        return books.stream().map(BookResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public BookResponse findById(Long id) {
        return BookResponse.from(getBook(id));
    }

    @Transactional
    public BookResponse create(BookRequest request) {
        validateCopies(request.totalCopies());
        if (bookRepository.existsByIsbnIgnoreCase(request.isbn().trim())) {
            throw new BadRequestException("A book with ISBN " + request.isbn().trim() + " already exists.");
        }
        Book book = new Book();
        apply(book, request, request.totalCopies());
        try {
            return BookResponse.from(bookRepository.save(book));
        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestException("ISBN must be unique.");
        }
    }

    @Transactional
    public BookResponse update(Long id, BookRequest request) {
        validateCopies(request.totalCopies());
        Book book = getBook(id);
        bookRepository.findByIsbnIgnoreCase(request.isbn().trim()).ifPresent(existing -> {
            if (!existing.getId().equals(id)) throw new BadRequestException("A book with this ISBN already exists.");
        });
        int issuedCopies = book.getTotalCopies() - book.getAvailableCopies();
        if (request.totalCopies() < issuedCopies) {
            throw new BadRequestException("Total copies cannot be less than the " + issuedCopies + " copies currently issued.");
        }
        apply(book, request, request.totalCopies() - issuedCopies);
        return BookResponse.from(bookRepository.save(book));
    }

    @Transactional
    public void delete(Long id) {
        Book book = getBook(id);
        if (transactionRepository.existsByBookIdAndStatus(id, TransactionStatus.ISSUED)) {
            throw new BadRequestException("This book cannot be deleted while it has an active issue.");
        }
        transactionRepository.deleteByBookId(id);
        bookRepository.delete(book);
    }

    private Book getBook(Long id) {
        return bookRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Book not found: " + id));
    }

    private void apply(Book book, BookRequest request, int availableCopies) {
        book.setTitle(request.title().trim());
        book.setAuthor(request.author().trim());
        book.setIsbn(request.isbn().trim());
        book.setCategory(request.category().trim());
        book.setTotalCopies(request.totalCopies());
        book.setAvailableCopies(availableCopies);
    }

    private void validateCopies(Integer copies) {
        if (copies == null || copies < 1) throw new BadRequestException("Total copies must be at least 1.");
    }
}
