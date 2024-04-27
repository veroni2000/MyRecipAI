package com.example.myrecipai.service;

import com.example.myrecipai.dto.IngredientDTO;
import com.example.myrecipai.model.Ingredient;
import com.example.myrecipai.repository.IngredientRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IngredientService {
    @Autowired
    private IngredientRepository ingredientRepository;

    public Long addIngredient(IngredientDTO ingredientDTO){
        ModelMapper modelMapper = new ModelMapper();
        Ingredient ingredient = modelMapper.map(ingredientDTO, Ingredient.class);
        ingredientRepository.save(ingredient);
        System.out.println(ingredient.getId());
        return ingredient.getId();
    }

    public List<Ingredient> findIngredients(String name){
        return ingredientRepository.searchIngredient(name);
    }

    public Ingredient findIngredientByName(String name){return ingredientRepository.findIngredientIdByIngredient(name);}
}
