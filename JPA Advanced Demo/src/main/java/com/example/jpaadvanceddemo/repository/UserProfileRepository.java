package com.example.jpaadvanceddemo.repository;

import com.example.jpaadvanceddemo.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 用户详情 Repository
 */
@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
}
