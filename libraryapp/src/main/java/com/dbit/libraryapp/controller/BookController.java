package com.dbit.libraryapp.controller;

import com.dbit.libraryapp.dto.BookRequest;
import com.dbit.libraryapp.dto.BookResponse;
import com.dbit.libraryapp.service.BookService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/books")
public class BookController {
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public List<BookResponse> all(@RequestParam(required = false) String search,
                                  @RequestParam(required = false) String category) {
        return bookService.findAll(search, category);
    }

    @GetMapping("/{id}")
    public BookResponse one(@PathVariable Long id) {
        return bookService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookResponse create(@Valid @RequestBody BookRequest request) {
        return bookService.create(request);
    }

    @PutMapping("/{id}")
    public BookResponse update(@PathVariable Long id, @Valid @RequestBody BookRequest request) {
        return bookService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public Map<String, String> delete(@PathVariable Long id) {
        bookService.delete(id);
        return Map.of("message", "Book deleted successfully.");
    }
}
