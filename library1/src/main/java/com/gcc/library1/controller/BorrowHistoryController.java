package com.gcc.library1.controller;

import com.gcc.library1.model.BorrowHistory;
import com.gcc.library1.service.BorrowHistoryService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/borrowhistory")
@RequiredArgsConstructor
public class BorrowHistoryController {

    private final BorrowHistoryService borrowHistoryService;

    @GetMapping("/getBorrowHistoryByBookId/{bookId}")
    public ResponseEntity<?> getBorrowHistoryByBookId(@PathVariable Long bookId) {
        try {
            if (bookId == null) {
                return ResponseEntity.badRequest().body("书籍ID不能为空");
            }
            List<BorrowHistory> historyList = borrowHistoryService.getBorrowHistoryByBookId(bookId);
            return ResponseEntity.ok(historyList);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("查询失败");
        }
    }

    @GetMapping("/getBorrowHistoryByUserId/{userId}")
    public ResponseEntity<?> getBorrowHistoryByUserId(@PathVariable Long userId) {
        try {
            if (userId == null) {
                return ResponseEntity.badRequest().body("用户ID不能为空");
            }
            List<BorrowHistory> historyList = borrowHistoryService.getBorrowHistoryByUserId(userId);
            return ResponseEntity.ok(historyList);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("查询失败");
        }
    }
}
