package com.example.jpaadvanceddemo.service;

import com.example.jpaadvanceddemo.dto.UserCreateRequestDTO;
import com.example.jpaadvanceddemo.dto.UserWithProfileDTO;
import com.example.jpaadvanceddemo.entity.User;

import java.util.List;

/**
 * 用户 Service 接口
 */
public interface UserService {

    /**
     * 创建用户
     */
    User createUser(User user);

    /**
     * 根据ID查询用户
     */
    User getUserById(Long id);

    /**
     * 根据ID查询用户及其详情
     */
    User getUserByIdWithProfile(Long id);

    /**
     * 查询所有用户
     */
    List<User> getAllUsers();

    /**
     * 更新用户
     */
    User updateUser(User user);

    /**
     * 删除用户
     */
    void deleteUser(Long id);

    /**
     * 使用 DTO 创建用户
     */
    User createUserWithDTO(UserCreateRequestDTO dto);

    /**
     * 根据ID查询用户及其详情（返回DTO）
     */
    UserWithProfileDTO getUserByIdWithProfileDTO(Long id);
}
