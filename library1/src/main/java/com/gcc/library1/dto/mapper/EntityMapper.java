package com.gcc.library1.dto.mapper;

import com.gcc.library1.dto.*;
import com.gcc.library1.model.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EntityMapper {

    // ── Book ──

    public BookResponse toBookResponse(Book book) {
        BookResponse dto = new BookResponse();
        dto.setId(book.getId());
        dto.setIsbn(book.getIsbn());
        dto.setTitle(book.getTitle());
        dto.setAuthor(book.getAuthor());
        dto.setDescription(book.getDescription());
        dto.setCover(book.getCover());
        dto.setCount(book.getCount());
        dto.setBorrowCount(book.getBorrowCount());
        return dto;
    }

    public List<BookResponse> toBookResponseList(List<Book> books) {
        return books.stream().map(this::toBookResponse).toList();
    }

    public Book toEntity(BookCreateRequest request) {
        Book book = new Book();
        book.setIsbn(request.getIsbn());
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setDescription(request.getDescription());
        book.setCover(request.getCover());
        book.setCount(request.getCount());
        return book;
    }

    public void updateEntity(Book book, BookUpdateRequest request) {
        book.setIsbn(request.getIsbn());
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setDescription(request.getDescription());
        book.setCover(request.getCover());
        book.setCount(request.getCount());
    }

    // ── User ──

    public UserResponse toUserResponse(User user) {
        UserResponse dto = new UserResponse();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setRole(user.getRole());
        return dto;
    }

    public List<UserResponse> toUserResponseList(List<User> users) {
        return users.stream().map(this::toUserResponse).toList();
    }

    // ── BorrowRecord ──

    public BorrowRecordResponse toBorrowRecordResponse(BorrowRecord record) {
        BorrowRecordResponse dto = new BorrowRecordResponse();
        dto.setId(record.getId());
        dto.setBookId(record.getBookId());
        dto.setUserId(record.getUserId());
        dto.setBorrowDate(record.getBorrowDate());
        dto.setReturnDate(record.getReturnDate());
        return dto;
    }

    public List<BorrowRecordResponse> toBorrowRecordResponseList(List<BorrowRecord> records) {
        return records.stream().map(this::toBorrowRecordResponse).toList();
    }

    // ── BorrowHistory ──

    public BorrowHistoryDTO toBorrowHistoryDTO(BorrowHistory history) {
        BorrowHistoryDTO dto = new BorrowHistoryDTO();
        dto.setId(history.getId());
        dto.setBookId(history.getBookId());
        dto.setUserId(history.getUserId());
        dto.setBehaviour(history.getBehaviour());
        dto.setDate(history.getDate());
        return dto;
    }

    public List<BorrowHistoryDTO> toBorrowHistoryDTOList(List<BorrowHistory> histories) {
        return histories.stream().map(this::toBorrowHistoryDTO).toList();
    }
}
