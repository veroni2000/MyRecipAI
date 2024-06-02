package com.example.myrecipai.service;

import com.example.myrecipai.dto.CommentDTO;
import com.example.myrecipai.dto.RecipeUserIdDTO;
import com.example.myrecipai.exception.BadRequestException;
import com.example.myrecipai.model.Comment;
import com.example.myrecipai.model.Recipe;
import com.example.myrecipai.model.User;
import com.example.myrecipai.repository.CommentRepository;
import com.example.myrecipai.repository.RecipeRepository;
import com.example.myrecipai.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RecipeRepository recipeRepository;
    @Autowired
    private CommentRepository commentRepository;

    public void addComment(RecipeUserIdDTO recipeUserIdDTO) {
        Optional<Recipe> recipe = recipeRepository.findById(recipeUserIdDTO.getRecipeId());
        if (recipe.isEmpty()) {
            throw new BadRequestException("Recipe not found");
        }
        Optional<User> user = userRepository.findById(recipeUserIdDTO.getUserId());
        if (user.isEmpty()) {
            throw new BadRequestException("User not found");
        }
        Comment comment = new Comment();
        comment.setRecipe(recipe.get());
        comment.setUser(user.get());
        comment.setText(recipeUserIdDTO.getText());
        commentRepository.save(comment);
        Recipe r = recipe.get();
        r.setCommentsCount(r.getCommentsCount() + 1);
        recipeRepository.save(r);
    }

    public void deleteComment(Long commentId) {
        Optional<Comment> comment = commentRepository.findById(commentId);
        if (comment.isEmpty()) {
            throw new BadRequestException("Comment not found");
        }
        Optional<Recipe> recipe = recipeRepository.findById(comment.get().getRecipe().getId());
        if (recipe.isEmpty()) {
            throw new BadRequestException("Post not found");
        }
        Recipe r = recipe.get();
        r.setCommentsCount(r.getCommentsCount() - 1);
        recipeRepository.save(r);
        commentRepository.delete(comment.get());
    }

    public List<CommentDTO> getAllCommentsByRecipe(Long recipeId) {
        List<Comment> comments = commentRepository.findAllCommentsByRecipeId(recipeId);
        List<CommentDTO> commentDTOS = new ArrayList<>();
        ModelMapper modelMapper = new ModelMapper();
        for (Comment comment : comments) {
            commentDTOS.add(modelMapper.map(comment, CommentDTO.class));
        }
        return commentDTOS;
    }
}
