package com.dbit.libraryapp.repository;

import com.dbit.libraryapp.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByMemberCodeIgnoreCase(String memberCode);
    boolean existsByMemberCodeIgnoreCase(String memberCode);
    boolean existsByEmailIgnoreCase(String email);
    List<Member> findAllByOrderByNameAsc();
    List<Member> findByMemberCodeContainingIgnoreCaseOrNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String memberCode, String name, String email);
}
