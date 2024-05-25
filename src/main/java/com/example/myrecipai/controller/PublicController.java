package com.example.myrecipai.controller;

import com.example.myrecipai.dto.RecipeDTO;
import com.example.myrecipai.model.Recipe;
import com.example.myrecipai.service.ChatGptService;
import com.example.myrecipai.service.LikeService;
import com.example.myrecipai.service.RecipeService;
import com.theokanning.openai.assistants.Assistant;
import com.theokanning.openai.service.OpenAiService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicController {
    @Autowired
    private ChatGptService chatGptService;
    @Autowired
    private RecipeService recipeService;
    @Autowired
    private LikeService likeService;
    @Value("${openai.api.key}")
    private String openaiApiKey;

    @Value("${openai.api.assistant.cups-to-grams}")
    private String assistCupsToGrams;

    @Value("${openai.api.assistant.grams-to-cups}")
    private String assistGramsToCups;

    @Value("${spring.image.directory}")
    private String imageDirectory;

    @GetMapping("/assistant")
    public Assistant assistant(@RequestParam String assistantId) {
        OpenAiService openAiService = new OpenAiService(openaiApiKey);
        return openAiService.retrieveAssistant(assistantId);
    }

    @GetMapping("/cupsToGrams")
    public String cupsToGrams(@RequestParam String msg) throws InterruptedException {
        return chatGptService.getMessage(msg, assistCupsToGrams);
    }

    @GetMapping("/gramsToCups")
    public String gramsToCups(@RequestParam String msg) throws InterruptedException {
        return chatGptService.getMessage(msg, assistGramsToCups);
    }

    @GetMapping("/image/{imageName:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String imageName) throws MalformedURLException {
        Path imagePath = Paths.get(imageDirectory).resolve(imageName);
        Resource resource = new UrlResource(imagePath.toUri());

        if (!resource.exists() || !resource.isReadable()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG) // Adjust content type based on image type
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @GetMapping("/image")
    public void downloadImage(@RequestParam(name = "file") String name, HttpServletResponse response) throws IOException {
        recipeService.downloadImage(name, response);
    }

    @GetMapping("/recipes/all")
    public Page<RecipeDTO> getAllRecipes(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "10") int size) {
        return recipeService.getAllRecipes(page, size);
    }

    @GetMapping("/recipe/{recipeId}")
    public RecipeDTO findRecipe(@PathVariable Long recipeId) {
        return recipeService.findRecipeById(recipeId);
    }

    @GetMapping("/likes")
    public Long getAllLikesByRecipeId(@RequestParam Long recipeId) {
        return likeService.getAllLikesByRecipeId(recipeId);
    }

    @GetMapping("/search")
    public Page<RecipeDTO> searchRecipes(@RequestParam String title,
                                         @RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "10") int size) {
        return recipeService.searchRecipes(title, page, size);
    }

    @GetMapping("/recipesByIngredient")
    public List<RecipeDTO> getRecipesByIngredient(@RequestParam Long ingredientId){
        return recipeService.getRecipesByIngredient(ingredientId);
    };
}
