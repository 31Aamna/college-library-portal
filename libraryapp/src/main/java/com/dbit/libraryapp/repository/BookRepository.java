package com.dbit.libraryapp.repository;

import com.dbit.libraryapp.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findByIsbnIgnoreCase(String isbn);
    boolean existsByIsbnIgnoreCase(String isbn);

    @Query("select b from Book b where lower(b.title) like lower(concat('%', :search, '%')) " +
           "or lower(b.author) like lower(concat('%', :search, '%')) " +
           "or lower(b.isbn) like lower(concat('%', :search, '%')) " +
           "or lower(b.category) like lower(concat('%', :search, '%')) order by b.title")
    List<Book> search(@Param("search") String search);

    List<Book> findAllByOrderByTitleAsc();
}
