package com.example.myrecipai.controller;

import com.example.myrecipai.dto.IngredientDTO;
import com.example.myrecipai.model.Ingredient;
import com.example.myrecipai.service.IngredientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ingredient")
public class IngredientController {
    @Autowired
    private IngredientService ingredientService;

    @PostMapping("")
    public void addIngredient(@RequestBody IngredientDTO ingredientDTO){
        ingredientService.addIngredient(ingredientDTO);
    }

    @GetMapping("")
    public List<Ingredient> findIngredients(@RequestParam String name){
        return ingredientService.findIngredients(name);
    }

    @GetMapping("/find")
    public Ingredient findIngredient(@RequestParam String name){
        return ingredientService.findIngredientByName(name);
    }
}
