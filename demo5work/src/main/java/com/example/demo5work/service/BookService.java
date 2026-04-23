package com.example.demo5work.service;

import com.example.demo5work.entity.BorrowRecord;

import java.util.List;
import java.util.Optional;

public interface BookService {

    BorrowRecord borrowBook(Long bookId, String borrower);

    BorrowRecord returnBook(Long recordId);

}
