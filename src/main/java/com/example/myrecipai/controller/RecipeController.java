package com.example.myrecipai.controller;

import com.example.myrecipai.dto.CreateRecipeDTO;
import com.example.myrecipai.dto.RecipeDTO;
import com.example.myrecipai.service.OpenAiService;
import com.example.myrecipai.service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/recipe")
public class RecipeController {
    @Autowired
    private OpenAiService openAiService;
    @Autowired
    private RecipeService recipeService;

    @Value("${openai.api.assistant.recipai}")
    private String assistRecipe;
    @Value("${openai.api.assistant.time-cal-price}")
    private String assistTimeCalPrice;

    @PostMapping("")
    public Long createRecipe(@RequestBody CreateRecipeDTO createRecipeDTO) {
        return recipeService.createRecipe(createRecipeDTO);
    }

    @GetMapping("/{recipeId}")
    public RecipeDTO findRecipe(@PathVariable Long recipeId) {
        return recipeService.findRecipeById(recipeId);
    }

    @PostMapping("/edit/{recipeId}")
    public ResponseEntity<RecipeDTO> editRecipe(@PathVariable Long recipeId,
                                                @RequestBody RecipeDTO recipeDTO) {
        return recipeService.editRecipeById(recipeId, recipeDTO);
    }

//    @GetMapping("/all")
//    public List<RecipeDTO> getAllRecipes() {
//        return recipeService.getAllRecipes();
//    }

    @GetMapping("/byUser")
    public List<RecipeDTO> findRecipesByUser(@RequestParam Long userId) {
        return recipeService.findRecipesByUser(userId);
    }

    @GetMapping("/generate")
    public String generateRecipe(@RequestParam String prompt) throws InterruptedException {
        return openAiService.getMessage(prompt, assistRecipe);
    }

    @GetMapping("/image")
    public String image(@RequestParam String prompt) {
        return openAiService.getImage(prompt);
    }

    @GetMapping("/timeCalPrice")
    public String timeCalPrice(@RequestParam String prompt) throws InterruptedException {
        return openAiService.getMessage(prompt, assistTimeCalPrice);
    }

    @PostMapping("/saveImage")
    public String saveImage(@RequestParam("file") MultipartFile file) {
        return recipeService.saveImage(file);
    }

    @GetMapping("/saveUrlImage")
    public String saveImage(@RequestParam String imageUrl) throws IOException {
        return openAiService.saveGeneratedImage(imageUrl);
    }

    @GetMapping("/followedUsersRecipe")
    public Page<RecipeDTO> getFollowedUsersRecipes (@RequestParam Long userId,
                                                    @RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "10") int size){
        return recipeService.getFollowedUsersRecipes(userId, page, size);
    }
    @DeleteMapping("/deleteRecipe")
    public void deleteRecipe(@RequestParam("id") Long id){
        recipeService.deleteRecipeById(id);
    }
}
