package com.example.demo5work.repository;

import com.example.demo5work.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    @Query("SELECT b FROM Book b LEFT JOIN FETCH b.category WHERE b.id = :id")
    Optional<Book> findByIdWithCategory(@Param("id") Long id);

    @Query("SELECT b FROM Book b LEFT JOIN FETCH b.authors WHERE b.id = :id")
    Optional<Book> findByIdWithAuthors(@Param("id") Long id);

    @Query("SELECT b FROM Book b LEFT JOIN FETCH b.borrowRecords WHERE b.id = :id")
    Optional<Book> findByIdWithBorrowRecords(@Param("id") Long id);

    @Query("SELECT b FROM Book b WHERE b.category.name = :categoryName")
    List<Book> findByCategoryName(@Param("categoryName") String categoryName);

    @Query("SELECT b FROM Book b WHERE b.price BETWEEN :minPrice AND :maxPrice")
    List<Book> findByPriceBetween(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);

    @Query("SELECT b FROM Book b JOIN b.authors a WHERE a.id = :authorId")
    List<Book> findByAuthorId(@Param("authorId") Long authorId);
}
