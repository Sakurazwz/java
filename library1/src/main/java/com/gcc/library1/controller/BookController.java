package com.gcc.library1.controller;

import com.gcc.library1.model.Book;
import com.gcc.library1.service.BookService;
import com.gcc.library1.service.BorrowRecordService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Slf4j
public class BookController {
    private final BookService bookService;
    private final BorrowRecordService borrowService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/addBook")
    public ResponseEntity<Book> addBook(@RequestBody Book inputbook) {
        try {
            if (inputbook == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            Book book = bookService.addBook(inputbook);
            if (book == null) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return new ResponseEntity<>(book, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getAllBooks")
    public List<Book> getAllBooks() {
        return bookService.getAllBooks();
    }

    @GetMapping("/getBookById/{id}")
    public ResponseEntity<?> getBookById(@PathVariable Long id) {
        try {
            return new ResponseEntity<>(bookService.getBookById(id), HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getBookByTitle")
    public ResponseEntity<?> getBookByTitle(@RequestParam String title) {
        if (title == null || title.trim().isEmpty()) {
            return new ResponseEntity<>("Title不能为空", HttpStatus.BAD_REQUEST);
        }

        try {
            List<Book> books = bookService.getBooksByTitleLike("%" + title + "%");
            if (books.isEmpty()) {
                return new ResponseEntity<>("未找到书名为: " + title + " 的书籍", HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(books, HttpStatus.OK);
        } catch (Exception e) {
            log.error("查询书籍时发生错误: " + title, e);
            return new ResponseEntity<>("Internal server error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getBooksByIsbn")
    public ResponseEntity<?> getBooksByIsbn(@RequestParam String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            return new ResponseEntity<>("ISBN不能为空", HttpStatus.BAD_REQUEST);
        }

        try {
            List<Book> books = bookService.getBooksByIsbn(isbn.trim());
            return new ResponseEntity<>(books, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("查询ISBN时发生错误: " + isbn, e);
            return new ResponseEntity<>("Internal server error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getCopyCountByIsbn")
    public ResponseEntity<?> getCopyCountByIsbn(@RequestParam String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            return new ResponseEntity<>("ISBN不能为空", HttpStatus.BAD_REQUEST);
        }

        try {
            long count = bookService.getCopyCountByIsbn(isbn.trim());
            return new ResponseEntity<>(count, HttpStatus.OK);
        } catch (Exception e) {
            log.error("查询副本数量时发生错误: " + isbn, e);
            return new ResponseEntity<>("Internal server error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/deleteBook")
    public ResponseEntity<?> deleteBook(@RequestBody Book inputBook) {
        Long id = inputBook.getId();
        if (id == null) {
            return new ResponseEntity<>("bookId不能为空", HttpStatus.BAD_REQUEST);
        }

        try {
            borrowService.getBorrowBookByBookId(id);
            return new ResponseEntity<>(id + "该书已经借出", HttpStatus.NOT_ACCEPTABLE);
        } catch (EntityNotFoundException e) {
            try {
                bookService.deleteBook(id);
                return new ResponseEntity<>("bookid为" + id + "已删除", HttpStatus.OK);
            } catch (EntityNotFoundException e1) {
                return new ResponseEntity<>(e1.getMessage(), HttpStatus.NOT_FOUND);
            }
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/updateBook")
    public ResponseEntity<?> updateBook(@RequestBody Book inputBook) {
        Book book;
        try {
            book = bookService.updateBook(inputBook.getId(), inputBook);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(book, HttpStatus.OK);
    }
}
