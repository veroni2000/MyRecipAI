package com.example.myrecipai.dto;

import com.example.myrecipai.model.Recipe;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RecipeEditDTO extends Recipe {
    private String token;
}
