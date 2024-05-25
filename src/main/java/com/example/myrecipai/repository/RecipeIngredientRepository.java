package com.example.myrecipai.repository;

import com.example.myrecipai.model.Recipe;
import com.example.myrecipai.model.RecipeIngredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RecipeIngredientRepository extends JpaRepository<RecipeIngredient, Long> {
    @Query("select r from Recipe r join RecipeIngredient ing on r.id = ing.recipe.id where ing.ingredient.id = :ingredientId order by ing.id desc")
    List<Recipe> findAllRecipesByIngredientId(Long ingredientId);
}
