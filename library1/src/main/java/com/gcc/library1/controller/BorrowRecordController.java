package com.gcc.library1.controller;

import com.gcc.library1.model.BorrowRecord;
import com.gcc.library1.service.BookService;
import com.gcc.library1.service.BorrowHistoryService;
import com.gcc.library1.service.BorrowRecordService;
import com.gcc.library1.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/borrow")
@RequiredArgsConstructor
public class BorrowRecordController {

    private final BorrowRecordService borrowService;
    private final BookService bookService;
    private final UserService userService;
    private final BorrowHistoryService borrowHistoryService;

    @PostMapping("/add")
    public ResponseEntity<?> registerBorrow(@RequestBody BorrowRecord inputBorrowRecord) {
        Long bookId = inputBorrowRecord.getBookId();
        Long userId = inputBorrowRecord.getUserId();

        if (bookId == null || userId == null) {
            return new ResponseEntity<>("bookId或者userId参数缺失", HttpStatus.BAD_REQUEST);
        }

        try {
            userService.getUserById(userId);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(userId + "，借书失败，用户不存在", HttpStatus.NOT_FOUND);
        }

        try {
            bookService.getBookById(bookId);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(bookId + "，已借失败，该书不存在", HttpStatus.NOT_FOUND);
        }

        try {
            borrowService.getBorrowBookByBookId(bookId);
            return new ResponseEntity<>(bookId + "，已借失败，该书已被借", HttpStatus.CONFLICT);
        } catch (EntityNotFoundException e) {
            LocalDate borrowDate = LocalDate.now();
            LocalDate returnDate = borrowDate.plusDays(90);

            borrowService.addBorrow(bookId, userId, borrowDate, returnDate);
            borrowHistoryService.addBorrowHistory(bookId, userId, "借书90天");
            return new ResponseEntity<>(userId + "," + bookId + "," + "已借成功", HttpStatus.OK);
        }
    }

    @PostMapping("/updateBorrow")
    public ResponseEntity<?> updateBorrow(@RequestBody BorrowRecord inputBorrowRecord) {
        Long bookId = inputBorrowRecord.getBookId();
        Long userId = inputBorrowRecord.getUserId();

        try {
            BorrowRecord borrowRecord = borrowService.getBorrowBookByBookId(bookId);

            if (borrowRecord == null) {
                return new ResponseEntity<>(bookId + "，更新失败，请检查书籍id是否正确", HttpStatus.NOT_FOUND);
            }

            LocalDate returnDate = borrowRecord.getReturnDate().plusDays(90);
            if (Objects.equals(borrowRecord.getUserId(), userId)) {
                borrowService.updateBorrow(bookId, borrowRecord.getUserId(), returnDate);
                borrowHistoryService.addBorrowHistory(bookId, userId, "续借90天");
                return new ResponseEntity<>(bookId + "归还日期已更改为" + returnDate, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(userId + "，更新失败，请检查用户id是否正确", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("系统异常：" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/back")
    public ResponseEntity<?> backBook(@RequestBody BorrowRecord inputBorrowRecord) {
        if (inputBorrowRecord == null) {
            return new ResponseEntity<>("请求参数不能为空", HttpStatus.BAD_REQUEST);
        }

        Long bookId = inputBorrowRecord.getBookId();
        if (bookId == null) {
            return new ResponseEntity<>("书籍ID不能为空", HttpStatus.BAD_REQUEST);
        }

        try {
            BorrowRecord borrowRecord = borrowService.getBorrowBookByBookId(bookId);
            Long userId = borrowRecord.getUserId();
            borrowService.deleteBorrow(bookId);
            borrowHistoryService.addBorrowHistory(bookId, userId, "还书");
            return new ResponseEntity<>(bookId + "已还成功", HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("还书失败，请稍后重试", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/user")
    public ResponseEntity<?> getBorrowRecordsByUserId(@RequestBody BorrowRecord inputBorrowRecord) {
        Long userId = inputBorrowRecord.getUserId();
        if (userId == null) {
            return new ResponseEntity<>("用户ID不能为空", HttpStatus.BAD_REQUEST);
        }

        try {
            userService.getUserById(userId);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>("用户不存在", HttpStatus.NOT_FOUND);
        }

        try {
            List<BorrowRecord> borrowRecords = borrowService.getBorrowBooksByUserId(userId);
            return new ResponseEntity<>(borrowRecords, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("查询失败，请稍后重试", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 根据用户ID查询其逾期未还的借阅记录
     */
    @PostMapping("/overdue")
    public ResponseEntity<?> getOverdueBorrowRecordsByUserId(@RequestBody BorrowRecord inputBorrowRecord) {
        Long userId = inputBorrowRecord.getUserId();
        if (userId == null) {
            return new ResponseEntity<>("用户ID不能为空", HttpStatus.BAD_REQUEST);
        }

        try {
            userService.getUserById(userId);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>("用户不存在", HttpStatus.NOT_FOUND);
        }

        try {
            List<BorrowRecord> overdueRecords = borrowService.getOverdueBorrowRecordsByUserId(userId);
            return new ResponseEntity<>(overdueRecords, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("查询失败，请稍后重试", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllBorrowRecords() {
        try {
            List<BorrowRecord> allBorrowRecords = borrowService.getAllBorrowRecords();
            return new ResponseEntity<>(allBorrowRecords, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("查询失败，请稍后重试", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
