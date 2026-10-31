package com.dbit.libraryapp.controller;

import com.dbit.libraryapp.dto.MemberRequest;
import com.dbit.libraryapp.dto.MemberResponse;
import com.dbit.libraryapp.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/members")
public class MemberController {
    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping
    public List<MemberResponse> all(@RequestParam(required = false) String search) {
        return memberService.findAll(search);
    }

    @GetMapping("/{id}")
    public MemberResponse one(@PathVariable Long id) {
        return memberService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MemberResponse create(@Valid @RequestBody MemberRequest request) {
        return memberService.create(request);
    }

    @PutMapping("/{id}")
    public MemberResponse update(@PathVariable Long id, @Valid @RequestBody MemberRequest request) {
        return memberService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public Map<String, String> delete(@PathVariable Long id) {
        memberService.delete(id);
        return Map.of("message", "Member deleted successfully.");
    }
}
