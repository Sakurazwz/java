package com.example.demo5work.controller;

import com.example.demo5work.dto.BookDTO;
import com.example.demo5work.dto.BorrowRecordDTO;
import com.example.demo5work.dto.DTOMapper;
import com.example.demo5work.entity.Book;
import com.example.demo5work.entity.BorrowRecord;
import com.example.demo5work.repository.BookRepository;
import com.example.demo5work.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/books")
@Tag(name = "图书管理", description = "图书CRUD和借阅相关接口")
public class BookController {

    private final BookService bookService;
    private final BookRepository bookRepository;

    public BookController(BookService bookService, BookRepository bookRepository) {
        this.bookService = bookService;
        this.bookRepository = bookRepository;
    }

    @PostMapping
    @Operation(summary = "创建图书", description = "创建新图书")
    public ResponseEntity<BookDTO> createBook(@RequestBody Book book) {
        Book savedBook = bookRepository.save(book);
        return ResponseEntity.ok(DTOMapper.toBookDTO(savedBook));
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询图书", description = "查询单个图书详情")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        Optional<Book> book = bookRepository.findById(id);
        return book.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "查询所有图书", description = "查询图书列表")
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除图书", description = "根据ID删除图书")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{bookId}/borrow")
    @Operation(summary = "借书", description = "根据图书ID和借阅人借书")
    public ResponseEntity<Map<String, Object>> borrowBook(
            @PathVariable Long bookId,
            @RequestParam String borrower) {
        try {
            BorrowRecord record = bookService.borrowBook(bookId, borrower);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "借书成功");
            response.put("data", DTOMapper.toBorrowRecordDTO(record));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/{bookId}/return")
    @Operation(summary = "还书", description = "根据借阅记录ID还书")
    public ResponseEntity<Map<String, Object>> returnBook(
            @PathVariable Long bookId,
            @RequestParam Long recordId) {
        try {
            BorrowRecord record = bookService.returnBook(recordId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "还书成功");
            response.put("data", DTOMapper.toBorrowRecordDTO(record));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

//    @GetMapping("/{bookId}/borrow-records")
//    @Operation(summary = "查询借阅记录", description = "查询某本书的所有借阅记录")
//    public ResponseEntity<Map<String, Object>> getBorrowRecords(@PathVariable Long bookId) {
//        try {
//            List<BorrowRecord> records = bookService.getBorrowRecordsByBook(bookId);
//            Map<String, Object> response = new HashMap<>();
//            response.put("success", true);
//            response.put("data", DTOMapper.toBorrowRecordDTOList(records));
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            Map<String, Object> response = new HashMap<>();
//            response.put("success", false);
//            response.put("message", e.getMessage());
//            return ResponseEntity.badRequest().body(response);
//        }
//    }
}
