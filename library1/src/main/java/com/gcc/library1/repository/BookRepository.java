package com.gcc.library1.repository;

import com.gcc.library1.model.Book;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByTitleLike(String title);

    List<Book> findByIsbn(String isbn);

    long countByIsbn(String isbn);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Book> findById(Long id);
}
