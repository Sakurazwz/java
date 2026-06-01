package com.gcc.library1.controller;

import com.gcc.library1.dto.UserRegisterRequest;
import com.gcc.library1.dto.UserResponse;
import com.gcc.library1.service.BorrowRecordService;
import com.gcc.library1.service.UserService;
import com.gcc.library1.util.SecurityUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final BorrowRecordService borrowService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody @Valid UserRegisterRequest request) {
        try {
            userService.getUserByName(request.getName());
            return new ResponseEntity<>("用户已存在", HttpStatus.CONFLICT);
        } catch (EntityNotFoundException e) {
            UserResponse registeredUser = userService.addUser(request);
            return new ResponseEntity<>(registeredUser, HttpStatus.OK);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public List<UserResponse> getAllUsers(@RequestParam(required = false) String name) {
        if (name != null && !name.trim().isEmpty()) {
            return userService.searchUsersByName(name.trim());
        }
        return userService.getAllUsers();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        if (borrowService.hasBorrowedBooks(id)) {
            return new ResponseEntity<>("该用户已借书，无法删除", HttpStatus.CONFLICT);
        }
        userService.deleteUser(id);
        return new ResponseEntity<>("用户删除成功", HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/updateRole/{id}")
    public ResponseEntity<?> updateRole(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String newRole = body.get("role");
        if (newRole == null || (!newRole.equals("ADMIN") && !newRole.equals("USER"))) {
            return new ResponseEntity<>("角色值无效，必须为 ADMIN 或 USER", HttpStatus.BAD_REQUEST);
        }
        // 不能修改自己的角色
        if (SecurityUtils.getCurrentUserId().equals(id)) {
            return new ResponseEntity<>("不能修改自己的角色", HttpStatus.BAD_REQUEST);
        }
        UserResponse updated = userService.updateRole(id, newRole);
        return ResponseEntity.ok(updated);
    }
}
