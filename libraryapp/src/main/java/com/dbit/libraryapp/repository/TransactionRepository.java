package com.dbit.libraryapp.repository;

import com.dbit.libraryapp.model.LibraryTransaction;
import com.dbit.libraryapp.model.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<LibraryTransaction, Long> {
    boolean existsByBookIdAndStatus(Long bookId, TransactionStatus status);
    boolean existsByMemberIdAndStatus(Long memberId, TransactionStatus status);
    void deleteByBookId(Long bookId);
    void deleteByMemberId(Long memberId);
    long countByStatus(TransactionStatus status);
    long countByStatusAndDueDateBefore(TransactionStatus status, LocalDate date);

    @Query("select t from LibraryTransaction t join fetch t.book join fetch t.member " +
           "where (:status is null or t.status = :status) order by t.issueDate desc, t.id desc")
    List<LibraryTransaction> findHistory(@Param("status") TransactionStatus status);

    @Query("select t from LibraryTransaction t join fetch t.book join fetch t.member " +
           "where t.status = com.dbit.libraryapp.model.TransactionStatus.ISSUED " +
           "and t.dueDate < :today order by t.dueDate asc")
    List<LibraryTransaction> findOverdue(@Param("today") LocalDate today);

    List<LibraryTransaction> findTop8ByOrderByIssueDateDescIdDesc();
}
