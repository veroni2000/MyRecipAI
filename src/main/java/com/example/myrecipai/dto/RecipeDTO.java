package com.example.myrecipai.dto;

import com.example.myrecipai.model.RecipeIngredient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class RecipeDTO {
    private Long id;
    private String title;
    private String instructions;
    private UserWithoutPasswordDTO createdBy;
    private List<RecipeIngredient> recipeIngredients;
    private Boolean aiGenerated;
    private String image;
    private Boolean showImage;
    private String defaultMetric;
}
