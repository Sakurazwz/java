package com.gcc.library1.service;

import com.gcc.library1.dto.BorrowRecordResponse;
import com.gcc.library1.dto.mapper.EntityMapper;
import com.gcc.library1.model.BorrowRecord;
import com.gcc.library1.repository.BorrowRecordRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BorrowRecordService {
    private final BorrowRecordRepository borrowRecordRepository;
    private final EntityMapper mapper;

    @Transactional
    public BorrowRecord addBorrow(Long bookId, Long userId, LocalDate borrowDate, LocalDate returnDate) {
        BorrowRecord borrowRecord = new BorrowRecord();
        borrowRecord.setBookId(bookId);
        borrowRecord.setUserId(userId);
        borrowRecord.setBorrowDate(borrowDate);
        borrowRecord.setReturnDate(returnDate);
        return borrowRecordRepository.save(borrowRecord);
    }

    public BorrowRecord updateBorrow(Long bookId, Long userId, LocalDate returnDate) {
        BorrowRecord borrowRecord = findRecordByBookIdAndUserId(bookId, userId);
        borrowRecord.setReturnDate(returnDate);
        return borrowRecordRepository.save(borrowRecord);
    }

    public BorrowRecord getBorrowedRecord(Long bookId, Long userId) {
        return findRecordByBookIdAndUserId(bookId, userId);
    }

    public List<BorrowRecordResponse> getBorrowBooksByUserId(Long userId) {
        return mapper.toBorrowRecordResponseList(borrowRecordRepository.findByUserId(userId));
    }

    @Transactional
    public void deleteBorrow(Long bookId, Long userId) {
        BorrowRecord borrowRecord = findRecordByBookIdAndUserId(bookId, userId);
        borrowRecordRepository.delete(borrowRecord);
    }

    public boolean hasBorrowedThisBook(Long bookId, Long userId) {
        List<BorrowRecord> records = borrowRecordRepository.findByBookId(bookId);
        return records.stream().anyMatch(r -> Objects.equals(r.getUserId(), userId));
    }

    public boolean isBookBorrowedByAnyone(Long bookId) {
        return !borrowRecordRepository.findByBookId(bookId).isEmpty();
    }

    public boolean hasBorrowedBooks(Long userId) {
        return !borrowRecordRepository.findByUserId(userId).isEmpty();
    }

    public List<BorrowRecordResponse> getOverdueBorrowRecordsByUserId(Long userId) {
        LocalDate today = LocalDate.now();
        return mapper.toBorrowRecordResponseList(borrowRecordRepository.findByUserIdAndReturnDateBefore(userId, today));
    }

    public List<BorrowRecordResponse> getAllBorrowRecords() {
        return mapper.toBorrowRecordResponseList(borrowRecordRepository.findAll());
    }

    private BorrowRecord findRecordByBookIdAndUserId(Long bookId, Long userId) {
        List<BorrowRecord> records = borrowRecordRepository.findByBookId(bookId);
        if (records.isEmpty()) {
            throw new EntityNotFoundException("BorrowRecord not found with bookId:" + bookId);
        }
        return records.stream()
                .filter(r -> Objects.equals(r.getUserId(), userId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(
                        "BorrowRecord not found with bookId:" + bookId + " and userId:" + userId));
    }
}
