package com.dbit.libraryapp.controller;

import com.dbit.libraryapp.dto.DashboardResponse;
import com.dbit.libraryapp.dto.IssueRequest;
import com.dbit.libraryapp.dto.TransactionResponse;
import com.dbit.libraryapp.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public List<TransactionResponse> history(@RequestParam(required = false) String status,
                                             @RequestParam(defaultValue = "false") boolean overdue) {
        return transactionService.history(status, overdue);
    }

    @GetMapping("/dashboard")
    public DashboardResponse dashboard() {
        return transactionService.dashboard();
    }

    @GetMapping("/fine-rate")
    public Map<String, BigDecimal> fineRate() {
        return Map.of("perDay", transactionService.fineRate());
    }

    @PostMapping("/issue")
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse issue(@Valid @RequestBody IssueRequest request) {
        return transactionService.issue(request);
    }

    @PostMapping("/{id}/return")
    public TransactionResponse returnBook(@PathVariable Long id) {
        return transactionService.returnBook(id);
    }
}
