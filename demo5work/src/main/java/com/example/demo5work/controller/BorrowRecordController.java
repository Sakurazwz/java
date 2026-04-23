package com.example.demo5work.controller;

import com.example.demo5work.dto.DTOMapper;
import com.example.demo5work.dto.BorrowRecordDTO;
import com.example.demo5work.entity.BorrowRecord;
import com.example.demo5work.service.BorrowRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/borrow-records")
@Tag(name = "借阅记录管理", description = "借阅记录查询")
public class BorrowRecordController {

    private final BorrowRecordService borrowRecordService;

    public BorrowRecordController(BorrowRecordService borrowRecordService) {
        this.borrowRecordService = borrowRecordService;
    }

    @GetMapping("/book/{bookId}")
    @Operation(summary = "查询某本书的所有借阅记录")
    public ResponseEntity<Map<String, Object>> getBorrowRecords(@PathVariable Long bookId) {
        try {
            List<BorrowRecord> records = borrowRecordService.getBorrowRecordsByBook(bookId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", DTOMapper.toBorrowRecordDTOList(records));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/unreturned")
    @Operation(summary = "查询所有未归还的借阅记录")
    public ResponseEntity<Map<String, Object>> getUnreturnedRecords() {
        List<BorrowRecord> records = borrowRecordService.findUnreturned();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", DTOMapper.toBorrowRecordDTOList(records));
        return ResponseEntity.ok(response);
    }
}