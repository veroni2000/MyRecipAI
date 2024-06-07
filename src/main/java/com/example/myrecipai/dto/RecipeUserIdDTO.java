package com.example.myrecipai.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RecipeUserIdDTO {
    private String text;
    private Long recipeId;
    private Long userId;
}
