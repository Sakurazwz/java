package com.example.demo5work.repository;

import com.example.demo5work.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {

    @Query("SELECT a FROM Author a LEFT JOIN FETCH a.books WHERE a.id = :id")
    Optional<Author> findByIdWithBooks(@Param("id") Long id);

    @Query("SELECT a FROM Author a WHERE a.nationality = :nationality")
    List<Author> findByNationality(@Param("nationality") String nationality);

    @Query("SELECT DISTINCT a FROM Author a JOIN a.books b WHERE b.category.name = :categoryName")
    List<Author> findAuthorsByBookCategory(@Param("categoryName") String categoryName);
}
