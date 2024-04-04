package com.example.myrecipai.controller;

import com.example.myrecipai.dto.CreateRecipeDTO;
import com.example.myrecipai.dto.RecipeDTO;
import com.example.myrecipai.dto.RecipeEditDTO;
import com.example.myrecipai.model.Recipe;
import com.example.myrecipai.model.User;
import com.example.myrecipai.repository.UserRepository;
import com.example.myrecipai.service.ChatGptService;
import com.example.myrecipai.service.RecipeService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/recipe")
public class RecipeController {
    @Autowired
    private ChatGptService chatGptService;
    @Autowired
    private RecipeService recipeService;

    @Value("${openai.api.assistant.recipai}")
    private String assistRecipe;
    @Value("${openai.api.assistant.time-cal-price}")
    private String assistTimeCalPrice;

    @PostMapping("")
    public Long createRecipe(@RequestBody CreateRecipeDTO createRecipeDTO){
        return recipeService.createRecipe(createRecipeDTO);
    }
    @GetMapping("/{recipeId}")
    public RecipeDTO findRecipe(@PathVariable Long recipeId){
        return recipeService.findRecipeById(recipeId);
    }

    @PostMapping("/edit/{recipeId}")
    public ResponseEntity<RecipeDTO> editRecipe(@PathVariable Long recipeId,
                                                @RequestBody RecipeEditDTO recipeEditDTO){
        RecipeDTO recipeDTO = recipeService.findRecipeById(recipeId);
        ModelMapper modelMapper = new ModelMapper();
        Recipe recipe = modelMapper.map(recipeDTO, Recipe.class);

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(recipe.getUser().getId().equals(user.getId())){
            recipeService.editRecipe(recipe, recipeEditDTO);
        }
        return ResponseEntity.ok(modelMapper.map(recipe, RecipeDTO.class));
    }
    @GetMapping("/byUser")
    public List<RecipeDTO> findRecipesByUser(@RequestParam Long userId){
        return recipeService.findRecipesByUser(userId);
    }
    @PostMapping("/generate")
    public String generateRecipe(@RequestParam String msg) throws InterruptedException {
        return chatGptService.getMessage(msg, assistRecipe);
    }
    @GetMapping("/image")
    public String image(@RequestParam String prompt){
        return chatGptService.getImage(prompt);
    }
    @GetMapping("/timeCalPrice")
    public String timeCalPrice(@RequestParam String msg) throws InterruptedException {
        return chatGptService.getMessage(msg, assistTimeCalPrice);
    }
}
