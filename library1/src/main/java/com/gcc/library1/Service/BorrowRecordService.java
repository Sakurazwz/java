package com.gcc.library1.service;

import com.gcc.library1.model.BorrowRecord;
import com.gcc.library1.repository.BorrowRecordRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BorrowRecordService {
    private final BorrowRecordRepository borrowRecordRepository;

    public BorrowRecord addBorrow(Long bookId, Long userId, LocalDate borrowDate, LocalDate returnDate) {
        BorrowRecord borrowRecord = new BorrowRecord();
        borrowRecord.setBookId(bookId);
        borrowRecord.setUserId(userId);
        borrowRecord.setBorrowDate(borrowDate);
        borrowRecord.setReturnDate(returnDate);
        return borrowRecordRepository.save(borrowRecord);
    }

    public BorrowRecord updateBorrow(Long bookId, Long userId, LocalDate returnDate) {
        BorrowRecord borrowRecord = borrowRecordRepository.findByBookId(bookId)
                .orElseThrow(() -> new EntityNotFoundException("BorrowRecord not found with bookId:" + bookId));
        if (Objects.equals(borrowRecord.getUserId(), userId)) {
            borrowRecord.setReturnDate(returnDate);
            return borrowRecordRepository.save(borrowRecord);
        } else {
            throw new EntityNotFoundException("From original userId:" + borrowRecord.getUserId() + " and your userId:" + userId + " not same");
        }
    }

    public BorrowRecord getBorrowBookByBookId(Long bookId) {
        return borrowRecordRepository.findByBookId(bookId)
                .orElseThrow(() -> new EntityNotFoundException("BorrowRecord not found with bookId:" + bookId));
    }

    public List<BorrowRecord> getBorrowBooksByUserId(Long userId) {
        return borrowRecordRepository.findByUserId(userId);
    }

    public void deleteBorrow(Long bookId) {
        BorrowRecord borrowRecord = borrowRecordRepository.findByBookId(bookId)
                .orElseThrow(() -> new EntityNotFoundException("BorrowRecord not found with bookId:" + bookId));
        borrowRecordRepository.deleteById(borrowRecord.getId());
    }

    public boolean hasBorrowedBooks(Long userId) {
        return !borrowRecordRepository.findByUserIdAndReturnDateIsNull(userId).isEmpty();
    }

    public List<BorrowRecord> getOverdueBorrowRecordsByUserId(Long userId) {
        LocalDate today = LocalDate.now();
        return borrowRecordRepository.findByUserIdAndReturnDateBeforeAndReturnDateIsNotNull(userId, today);
    }

    public List<BorrowRecord> getAllBorrowRecords() {
        return borrowRecordRepository.findAll();
    }
}
