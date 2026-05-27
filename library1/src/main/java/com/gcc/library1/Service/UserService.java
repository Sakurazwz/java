package com.gcc.library1.service;

import com.gcc.library1.model.User;
import com.gcc.library1.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User addUser(User user) {
        // 注册时加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User updateUser(Long id, User user) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found " + id));
        existingUser.setName(user.getName());
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(existingUser);
    }

    public void deleteUser(Long id) {
        userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found " + id));
        userRepository.deleteById(id);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found " + id));
    }

    public User getUserByName(String name) {
        return userRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("User not found " + name));
    }

    /**
     * 验证用户名和密码（使用 BCrypt 解密比较），防止时序攻击枚举用户
     */
    public User authenticate(String name, String password) {
        User user = userRepository.findByName(name).orElse(null);
        // 无论用户是否存在，都执行一次 BCrypt 匹配以防止时序攻击
        if (user == null) {
            passwordEncoder.matches(password, "$2a$10$dummy.dummy.dummy.dummy.dummy.dummy.dummy.dummy.dummy.dummy");
            throw new EntityNotFoundException("用户名或密码错误");
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new EntityNotFoundException("用户名或密码错误");
        }
        return user;
    }
}
