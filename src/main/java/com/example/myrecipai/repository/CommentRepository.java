package com.example.myrecipai.repository;

import com.example.myrecipai.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllCommentsByRecipeId(Long recipeId);
}
