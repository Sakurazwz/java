package com.gcc.library1.controller;

import com.gcc.library1.model.BorrowHistory;
import com.gcc.library1.service.BorrowHistoryService;
import com.gcc.library1.util.SecurityUtils;
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

    /**
     * 查询借阅历史（按 bookId）：
     * - 管理员：可查看任意书籍的借阅历史
     * - 普通用户：禁止（可通过 userId 查自己的）
     */
    @GetMapping("/getBorrowHistoryByBookId/{bookId}")
    public ResponseEntity<?> getBorrowHistoryByBookId(@PathVariable Long bookId) {
        if (!SecurityUtils.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("仅管理员可查看书籍借阅历史");
        }
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

    /**
     * 查询借阅历史（按 userId）：
     * - 管理员：可查看任意用户的借阅历史
     * - 普通用户：只能查看自己的
     */
    @GetMapping("/getBorrowHistoryByUserId/{userId}")
    public ResponseEntity<?> getBorrowHistoryByUserId(@PathVariable Long userId) {
        // 普通用户只能查看自己的历史
        if (!SecurityUtils.isAdmin() && !SecurityUtils.getCurrentUserId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("只能查看自己的借阅历史");
        }
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
