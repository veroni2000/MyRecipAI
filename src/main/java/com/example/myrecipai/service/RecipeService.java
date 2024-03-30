package com.example.myrecipai.service;

import com.example.myrecipai.dto.CreateRecipeDTO;
import com.example.myrecipai.dto.RecipeDTO;
import com.example.myrecipai.dto.UserWithoutPasswordDTO;
import com.example.myrecipai.exception.BadRequestException;
import com.example.myrecipai.model.RecipeIngredient;
import com.example.myrecipai.model.*;
import com.example.myrecipai.repository.IngredientRepository;
import com.example.myrecipai.repository.RecipeRepository;
import com.example.myrecipai.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class RecipeService {
    @Autowired
    private RecipeRepository recipeRepository;
    @Autowired
    private IngredientRepository ingredientRepository;
    @Autowired
    private UserRepository userRepository;

    public Long createRecipe(CreateRecipeDTO createRecipeDTO) {
        ModelMapper modelMapper = new ModelMapper();
        Optional<User> userOptional = userRepository.findById(createRecipeDTO.getCreatedBy());
        if (userOptional.isEmpty()) {
            throw new BadRequestException("User not found");
        }

        User user = userOptional.get();
        Recipe recipe = modelMapper.map(createRecipeDTO, Recipe.class);
        recipe.setUser(user);
        recipe.setDateCreated(new Date());

        // Iterate over the recipe ingredients and set the recipe for each one
        for (RecipeIngredient recipeIngredient : recipe.getRecipeIngredients()) {
            recipeIngredient.setRecipe(recipe);
        }

        recipeRepository.save(recipe);
        return recipe.getId();
    }

    public RecipeDTO findRecipeById(Long recipeId){
        ModelMapper modelMapper = new ModelMapper();
        Optional<Recipe> recipeOptional = recipeRepository.findById(recipeId);
        if (recipeOptional.isEmpty()) {
            throw new BadRequestException("Recipe not found");
        }

        RecipeDTO recipeDTO = modelMapper.map(recipeOptional.get(), RecipeDTO.class);
        recipeDTO.setCreatedBy(modelMapper.map(recipeOptional.get().getUser(), UserWithoutPasswordDTO.class));

        return recipeDTO;
    }

    public List<RecipeDTO> findRecipesByUser(Long userId){
        ModelMapper modelMapper = new ModelMapper();
        List<Recipe> recipes = recipeRepository.findAllByUserId(userId);
        List<RecipeDTO> recipeDTOList = new ArrayList<>();
        for (Recipe recipe : recipes) {
            RecipeDTO dto = modelMapper.map(recipe, RecipeDTO.class);
            dto.setCreatedBy(modelMapper.map(recipe.getUser(), UserWithoutPasswordDTO.class));
            recipeDTOList.add(dto);
        }
        return recipeDTOList;
    }
}
