package com.gcc.library1.repository;

import com.gcc.library1.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByTitle(String title);

    List<Book> findByTitleLike(String title);

    List<Book> findByIsbn(String isbn);

    long countByIsbn(String isbn);
}
