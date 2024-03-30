package com.example.myrecipai.controller;

import com.example.myrecipai.dto.CreateRecipeDTO;
import com.example.myrecipai.dto.RecipeDTO;
import com.example.myrecipai.repository.UserRepository;
import com.example.myrecipai.service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recipe")
public class RecipeController {
    @Autowired
    private RecipeService recipeService;
    @Autowired
    private UserRepository userRepository;

    @PostMapping("")
    public Long createRecipe(@RequestBody CreateRecipeDTO createRecipeDTO){
        return recipeService.createRecipe(createRecipeDTO);
    }

    @GetMapping("/{recipeId}")
    public RecipeDTO findRecipe(@PathVariable Long recipeId){
        return recipeService.findRecipeById(recipeId);
    }

    @GetMapping("/byUser")
    public List<RecipeDTO> findRecipesByUser(@RequestParam Long userId){
        return recipeService.findRecipesByUser(userId);
    }
}
