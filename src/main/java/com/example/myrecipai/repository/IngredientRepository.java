package com.example.myrecipai.repository;

import com.example.myrecipai.model.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
    @Query("SELECT i from Ingredient  i where i.ingredient like concat('%', :ingredient, '%')")
    List<Ingredient> searchIngredient(String ingredient);
    Ingredient findIngredientIdByIngredient(String ingredient);
}
