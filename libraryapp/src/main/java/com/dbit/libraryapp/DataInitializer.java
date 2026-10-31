package com.dbit.libraryapp;

import com.dbit.libraryapp.model.Book;
import com.dbit.libraryapp.model.Member;
import com.dbit.libraryapp.repository.BookRepository;
import com.dbit.libraryapp.repository.MemberRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {
    @Bean
    CommandLineRunner seedDemoData(BookRepository bookRepository, MemberRepository memberRepository) {
        return args -> {
            if (bookRepository.count() == 0) {
                bookRepository.save(book("Clean Code", "Robert C. Martin", "9780132350884", "Software", 4));
                bookRepository.save(book("The Pragmatic Programmer", "David Thomas", "9780135957059", "Software", 3));
                bookRepository.save(book("Designing Data-Intensive Applications", "Martin Kleppmann", "9781449373320", "Data", 2));
                bookRepository.save(book("Atomic Habits", "James Clear", "9780735211292", "Personal Development", 5));
                bookRepository.save(book("The Lean Startup", "Eric Ries", "9780307887894", "Management", 2));
            }
            if (memberRepository.count() == 0) {
                memberRepository.save(member("LIB-1001", "Ananya Sharma", "ananya.sharma@example.edu", "+91 98765 12001"));
                memberRepository.save(member("LIB-1002", "Rohan Mehta", "rohan.mehta@example.edu", "+91 98765 12002"));
                memberRepository.save(member("LIB-1003", "Sara Thomas", "sara.thomas@example.edu", "+91 98765 12003"));
            }
        };
    }

    private Book book(String title, String author, String isbn, String category, int copies) {
        Book book = new Book();
        book.setTitle(title); book.setAuthor(author); book.setIsbn(isbn); book.setCategory(category);
        book.setTotalCopies(copies); book.setAvailableCopies(copies);
        return book;
    }

    private Member member(String code, String name, String email, String phone) {
        Member member = new Member();
        member.setMemberCode(code); member.setName(name); member.setEmail(email); member.setPhone(phone);
        return member;
    }
}
