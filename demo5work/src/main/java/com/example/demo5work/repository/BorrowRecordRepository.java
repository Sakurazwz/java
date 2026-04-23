package com.example.demo5work.repository;

import com.example.demo5work.entity.BorrowRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {

    @Query("SELECT br FROM BorrowRecord br LEFT JOIN FETCH br.book WHERE br.id = :id")
    Optional<BorrowRecord> findByIdWithBook(@Param("id") Long id);

    @Query("SELECT br FROM BorrowRecord br WHERE br.borrower = :borrower")
    List<BorrowRecord> findByBorrower(@Param("borrower") String borrower);

    @Query("SELECT br FROM BorrowRecord br WHERE br.returnDate IS NULL")
    List<BorrowRecord> findUnreturned();

    @Query("SELECT br FROM BorrowRecord br WHERE br.borrowDate BETWEEN :start AND :end")
    List<BorrowRecord> findByBorrowDateBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
