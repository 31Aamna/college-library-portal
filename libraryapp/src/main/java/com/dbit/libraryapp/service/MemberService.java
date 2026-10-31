package com.dbit.libraryapp.service;

import com.dbit.libraryapp.dto.MemberRequest;
import com.dbit.libraryapp.dto.MemberResponse;
import com.dbit.libraryapp.exception.BadRequestException;
import com.dbit.libraryapp.exception.ResourceNotFoundException;
import com.dbit.libraryapp.model.Member;
import com.dbit.libraryapp.model.TransactionStatus;
import com.dbit.libraryapp.repository.MemberRepository;
import com.dbit.libraryapp.repository.TransactionRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final TransactionRepository transactionRepository;

    public MemberService(MemberRepository memberRepository, TransactionRepository transactionRepository) {
        this.memberRepository = memberRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional(readOnly = true)
    public List<MemberResponse> findAll(String search) {
        List<Member> members = search == null || search.isBlank()
                ? memberRepository.findAllByOrderByNameAsc()
                : memberRepository.findByMemberCodeContainingIgnoreCaseOrNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                        search.trim(), search.trim(), search.trim());
        return members.stream().map(MemberResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public MemberResponse findById(Long id) {
        return MemberResponse.from(getMember(id));
    }

    @Transactional
    public MemberResponse create(MemberRequest request) {
        ensureUnique(request, null);
        Member member = new Member();
        apply(member, request);
        try {
            return MemberResponse.from(memberRepository.save(member));
        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestException("Member ID and email must be unique.");
        }
    }

    @Transactional
    public MemberResponse update(Long id, MemberRequest request) {
        Member member = getMember(id);
        ensureUnique(request, id);
        apply(member, request);
        return MemberResponse.from(memberRepository.save(member));
    }

    @Transactional
    public void delete(Long id) {
        Member member = getMember(id);
        if (transactionRepository.existsByMemberIdAndStatus(id, TransactionStatus.ISSUED)) {
            throw new BadRequestException("This member cannot be deleted while they have an active issue.");
        }
        transactionRepository.deleteByMemberId(id);
        memberRepository.delete(member);
    }

    private Member getMember(Long id) {
        return memberRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Member not found: " + id));
    }

    private void ensureUnique(MemberRequest request, Long currentId) {
        memberRepository.findByMemberCodeIgnoreCase(request.memberCode().trim()).ifPresent(existing -> {
            if (currentId == null || !existing.getId().equals(currentId)) throw new BadRequestException("Member ID must be unique.");
        });
        memberRepository.findAll().stream()
                .filter(existing -> existing.getEmail().equalsIgnoreCase(request.email().trim()))
                .filter(existing -> currentId == null || !existing.getId().equals(currentId))
                .findFirst()
                .ifPresent(existing -> { throw new BadRequestException("Email must be unique."); });
    }

    private void apply(Member member, MemberRequest request) {
        member.setMemberCode(request.memberCode().trim());
        member.setName(request.name().trim());
        member.setEmail(request.email().trim().toLowerCase());
        member.setPhone(request.phone().trim());
    }
}
