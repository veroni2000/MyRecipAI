package com.example.myrecipai.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "recipe_ingredients")
public class RecipeIngredient {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "ingredient_id")
    private Ingredient ingredient;
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;
    private String weight;
    private String volume;
}
