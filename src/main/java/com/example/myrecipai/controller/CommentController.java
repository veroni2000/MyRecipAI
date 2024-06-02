package com.example.myrecipai.controller;

import com.example.myrecipai.dto.CommentDTO;
import com.example.myrecipai.dto.RecipeUserIdDTO;
import com.example.myrecipai.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comment")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @PostMapping("")
    public void addComment(@RequestBody RecipeUserIdDTO recipeUserIdDTO) {
        commentService.addComment(recipeUserIdDTO);
    }

    @DeleteMapping("/")
    public void deleteComment(@RequestParam("id") Long id) {
        commentService.deleteComment(id);
    }
}
