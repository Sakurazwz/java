package com.example.demo5work.controller;

import com.example.demo5work.entity.Author;
import com.example.demo5work.repository.AuthorRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/authors")
@Tag(name = "作者管理", description = "作者CRUD操作")
public class AuthorController {

    private final AuthorRepository authorRepository;

    public AuthorController(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @GetMapping
    @Operation(summary = "查询所有作者")
    public List<Author> getAllAuthors() {
        return authorRepository.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询作者")
    public ResponseEntity<Author> getAuthorById(@PathVariable Long id) {
        return authorRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "创建作者")
    public Author createAuthor(@RequestBody Author author) {
        return authorRepository.save(author);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除作者")
    public ResponseEntity<Void> deleteAuthor(@PathVariable Long id) {
        authorRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
