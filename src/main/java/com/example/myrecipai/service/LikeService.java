package com.example.myrecipai.service;

import com.example.myrecipai.dto.LikeDTO;
import com.example.myrecipai.exception.BadRequestException;
import com.example.myrecipai.model.Follow;
import com.example.myrecipai.model.Like;
import com.example.myrecipai.model.Recipe;
import com.example.myrecipai.model.User;
import com.example.myrecipai.repository.LikeRepository;
import com.example.myrecipai.repository.RecipeRepository;
import com.example.myrecipai.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RecipeRepository recipeRepository;
    @Autowired
    private LikeRepository likeRepository;
    public void addLike(LikeDTO likeDTO){
        Optional<User> user = userRepository.findById(likeDTO.getUserId());
        if (user.isEmpty()){
            throw new BadRequestException("User not found");
        }
        System.out.println(likeDTO.getRecipeId());
        Optional<Recipe> recipe = recipeRepository.findById(likeDTO.getRecipeId());
        if (recipe.isEmpty()){
            throw  new BadRequestException("Recipe not found");
        }
        if (likeRepository.findByRecipeIdAndUserId(recipe.get().getId(), user.get().getId()).isPresent()){
            throw new BadRequestException("You have already liked this recipe");
        }else if (recipe.get().getUser().equals(user)){
            throw new BadRequestException("You cannot like your own recipe");
        }
        Like like = new Like();
        like.setRecipe(recipe.get());
        like.setUser(user.get());
        // todo validation
        likeRepository.save(like);
    }
    public void disLike(LikeDTO likeDTO){
        Optional<User> user = userRepository.findById(likeDTO.getUserId());
        if (user.isEmpty()){
            throw new BadRequestException("User not found");
        }
        System.out.println(likeDTO);
        Optional<Recipe> recipe = recipeRepository.findById(likeDTO.getRecipeId());
        if (recipe.isEmpty()){
            throw  new BadRequestException("Recipe not found");
        }
        Like like = likeRepository.findByRecipeAndUser(recipe.get(), user.get());

        likeRepository.delete(like);
    }

    public boolean likedRecipe (Long recipeId){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Like> like = likeRepository.findByRecipeIdAndUserId(recipeId, user.getId());
        return like.isPresent();
    }

    public Long getAllLikesByRecipeId(Long recipeId) {
        return likeRepository.countByRecipeId(recipeId);
    }
}