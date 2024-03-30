package com.example.myrecipai.dto;

import com.example.myrecipai.model.RecipeIngredient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CreateRecipeDTO {
    private Long id;
    private String title;
    private String instructions;
    private Long createdBy;
    private List<RecipeIngredient> recipeIngredients;
    private Boolean aiGenerated;
}
