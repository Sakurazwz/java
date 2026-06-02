package com.gcc.library1.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "books")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String isbn;

    private String title;

    private String author;

    @Column(length = 2000)
    private String description;

    // 图书封面图片（Base64 编码存储）
    @Column(columnDefinition = "TEXT")
    private String cover;

    // 图书分类
    private String category;

    // 库存数量
    @Column(nullable = false)
    private int count = 0;

    // 被借数量
    @Column(nullable = false)
    private int borrowCount = 0;

}