package com.example.demo5work.service;


import com.example.demo5work.entity.BorrowRecord;

import java.util.List;
import java.util.Optional;


public interface BorrowRecordService {

    List<BorrowRecord> getBorrowRecordsByBook(Long bookId);

    Optional<BorrowRecord> getCurrentBorrowRecord(Long bookId);

    List<BorrowRecord> findUnreturned();
}
