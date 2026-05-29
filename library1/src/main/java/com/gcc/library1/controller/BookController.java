package com.gcc.library1.controller;

import com.gcc.library1.dto.BookCreateRequest;
import com.gcc.library1.dto.BookResponse;
import com.gcc.library1.dto.BookUpdateRequest;
import com.gcc.library1.service.BookService;
import com.gcc.library1.service.BorrowRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;
    private final BorrowRecordService borrowService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/addBook")
    public ResponseEntity<BookResponse> addBook(@RequestBody @Valid BookCreateRequest request) {
        BookResponse response = bookService.addBook(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/getAllBooks")
    public List<BookResponse> getAllBooks() {
        return bookService.getAllBooks();
    }

    @GetMapping("/getBookById/{id}")
    public BookResponse getBookById(@PathVariable Long id) {
        return bookService.getBookById(id);
    }

    @GetMapping("/getBookByTitle")
    public ResponseEntity<?> getBookByTitle(@RequestParam String title) {
        if (title == null || title.trim().isEmpty()) {
            return new ResponseEntity<>("Title不能为空", HttpStatus.BAD_REQUEST);
        }
        List<BookResponse> books = bookService.getBooksByTitleLike("%" + title + "%");
        if (books.isEmpty()) {
            return new ResponseEntity<>("未找到书名为: " + title + " 的书籍", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    @GetMapping("/getBooksByIsbn")
    public List<BookResponse> getBooksByIsbn(@RequestParam String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new IllegalArgumentException("ISBN不能为空");
        }
        return bookService.getBooksByIsbn(isbn.trim());
    }

    @GetMapping("/getCopyCountByIsbn")
    public long getCopyCountByIsbn(@RequestParam String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new IllegalArgumentException("ISBN不能为空");
        }
        return bookService.getCopyCountByIsbn(isbn.trim());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/deleteBook/{id}")
    public ResponseEntity<?> deleteBook(@PathVariable Long id) {
        if (borrowService.isBookBorrowedByAnyone(id)) {
            return new ResponseEntity<>(id + "该书已经借出", HttpStatus.NOT_ACCEPTABLE);
        }
        bookService.deleteBook(id);
        return new ResponseEntity<>("bookid为" + id + "已删除", HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/updateBook")
    public BookResponse updateBook(@RequestBody @Valid BookUpdateRequest request) {
        return bookService.updateBook(request);
    }
}
