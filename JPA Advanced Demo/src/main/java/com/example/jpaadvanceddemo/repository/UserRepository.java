package com.example.jpaadvanceddemo.repository;

import com.example.jpaadvanceddemo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 用户 Repository
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // 根据用户名查询（自动生成实现）
    Optional<User> findByUsername(String username);

    // 根据邮箱查询
    Optional<User> findByEmail(String email);

    // JPQL 查询：查询用户及其详情（使用 JOIN FETCH 避免N+1问题）
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.userProfile WHERE u.id = :id")
    Optional<User> findByIdWithProfile(@Param("id") Long id);

    // JPQL 查询：查询所有用户及其详情
    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.userProfile")
    List<User> findAllWithProfile();
}
