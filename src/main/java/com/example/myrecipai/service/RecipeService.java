package com.example.myrecipai.service;

import com.example.myrecipai.dto.CreateRecipeDTO;
import com.example.myrecipai.dto.RecipeDTO;
import com.example.myrecipai.dto.UserWithoutPasswordDTO;
import com.example.myrecipai.exception.BadRequestException;
import com.example.myrecipai.model.*;
import com.example.myrecipai.repository.IngredientRepository;
import com.example.myrecipai.repository.RecipeIngredientRepository;
import com.example.myrecipai.repository.RecipeRepository;
import com.example.myrecipai.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class RecipeService {
    @Value("${spring.image.directory}")
    private String imageDirectory;
    @Autowired
    private RecipeRepository recipeRepository;
    @Autowired
    private IngredientRepository ingredientRepository;
    @Autowired
    private RecipeIngredientRepository recipeIngredientRepository;
    @Autowired
    private UserRepository userRepository;

    public Long createRecipe(CreateRecipeDTO createRecipeDTO) {
        ModelMapper modelMapper = new ModelMapper();
        Optional<User> userOptional = userRepository.findById(createRecipeDTO.getCreatedBy());
        if (userOptional.isEmpty()) {
            throw new BadRequestException("User not found");
        }

        User user = userOptional.get();
        Recipe recipe = modelMapper.map(createRecipeDTO, Recipe.class);
        recipe.setUser(user);
        recipe.setDateCreated(new Date());

        // Iterate over the recipe ingredients and set the recipe for each one
        for (RecipeIngredient recipeIngredient : recipe.getRecipeIngredients()) {
            recipeIngredient.setRecipe(recipe);
        }
        if (createRecipeDTO.getRecipeImages() != null) {
            for (RecipeImage recipeImage : recipe.getRecipeImages()) {
                System.out.println(recipeImage);
                recipeImage.setRecipe(recipe);
            }
        }

        recipeRepository.save(recipe);
        return recipe.getId();
    }

    private void updateFields(Recipe recipe, RecipeDTO recipeDTO) {
        if (recipeDTO.getTitle() != null) {
            recipe.setTitle(recipeDTO.getTitle());
        }
        if (recipeDTO.getInstructions() != null) {
            recipe.setInstructions(recipeDTO.getInstructions());
        }
        if (recipeDTO.getRecipeIngredients() != null &&
                recipeDTO.getRecipeIngredients().size() == recipe.getRecipeIngredients().size()) {
            recipe.getRecipeIngredients().clear();
            recipeDTO.getRecipeIngredients()
                    .forEach(rec -> rec.setRecipe(recipe));
            recipe.getRecipeIngredients().addAll(recipeDTO.getRecipeIngredients());
        }
        if (recipeDTO.getImage() != null) {
            recipe.setImage(recipeDTO.getImage());
        }
        if (recipeDTO.getShowImage() != null) {
            recipe.setShowImage(recipeDTO.getShowImage());
        }
        if (recipeDTO.getRecipeTime() != null){
            recipe.setRecipeTime(recipeDTO.getRecipeTime());
        }
        if (recipeDTO.getRecipeCalories() != null){
            recipe.setRecipeCalories(recipeDTO.getRecipeCalories());
        }
        if (recipeDTO.getRecipePrice() != null){
            recipe.setRecipePrice(recipeDTO.getRecipePrice());
        }
        if (recipeDTO.getImage() != null){
            recipe.setImage(recipeDTO.getImage());
        }
        recipeRepository.save(recipe);
    }

    public RecipeDTO findRecipeById(Long recipeId) {
        ModelMapper modelMapper = new ModelMapper();
        Optional<Recipe> recipeOptional = recipeRepository.findById(recipeId);
        if (recipeOptional.isEmpty()) {
            throw new BadRequestException("Recipe not found");
        }

        RecipeDTO recipeDTO = modelMapper.map(recipeOptional.get(), RecipeDTO.class);
        recipeDTO.setCreatedBy(modelMapper.map(recipeOptional.get().getUser(), UserWithoutPasswordDTO.class));

        return recipeDTO;
    }

    public List<RecipeDTO> findRecipesByUser(Long userId) {
        ModelMapper modelMapper = new ModelMapper();
        List<Recipe> recipes = recipeRepository.findAllByUserIdOrderByDateCreatedDesc(userId);
        return getRecipeDTOS(modelMapper, recipes);
    }
    public Page<RecipeDTO> getAllRecipes(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Recipe> recipes = recipeRepository.getAllRecipesReversed(pageable);
        return recipes.map(this::convertToDto);
    }

    private RecipeDTO convertToDto(Recipe recipe) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(recipe, RecipeDTO.class);
    }

    public ResponseEntity<RecipeDTO> editRecipeById(Long recipeId, RecipeDTO recipeDTO) {
        ModelMapper modelMapper = new ModelMapper();
        Optional<Recipe> recipe = recipeRepository.findById(recipeId);
        if (recipe.isEmpty()) {
            throw new BadRequestException("Recipe not found");
        }

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (recipe.get().getUser().getId().equals(user.getId())) {
            updateFields(recipe.get(), recipeDTO);
        }
        return ResponseEntity.ok(modelMapper.map(recipe, RecipeDTO.class));
    }

    public String saveImage(MultipartFile file) {
        try {
            // Get the original filename of the uploaded file
            String originalFileName = file.getOriginalFilename();

            // Create the directory if it doesn't exist
            Path directory = Paths.get(imageDirectory);
            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
            }

            // Set the path where the file will be saved
            Path filePath = directory.resolve(originalFileName);

            // Save the file to the specified path
            Files.write(filePath, file.getBytes());

            // Return the name of the saved file (optional)
            return file.getOriginalFilename();
        } catch (Exception e) {
            // Handle any exception that might occur during file upload
            return "Failed to upload file: " + e.getMessage();
        }
    }

    public void downloadImage(String name, HttpServletResponse response) throws IOException {
        File file = new File(imageDirectory + name);
        Files.copy(file.toPath(), response.getOutputStream());
    }

    public Page<RecipeDTO> searchRecipes(String title, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Recipe> recipes = recipeRepository.searchRecipes(title, pageable);
        return recipes.map(this::convertToDto);
    }

    public List<RecipeDTO> getRecipesByIngredient(Long ingredientId) {
        ModelMapper modelMapper = new ModelMapper();
        List<Recipe> recipes = recipeIngredientRepository.findAllRecipesByIngredientId(ingredientId);
        return getRecipeDTOS(modelMapper, recipes);
    }

    @NotNull
    private List<RecipeDTO> getRecipeDTOS(ModelMapper modelMapper, List<Recipe> recipes) {
        List<RecipeDTO> recipeDTOList = new ArrayList<>();
        for (Recipe recipe : recipes) {
            RecipeDTO dto = modelMapper.map(recipe, RecipeDTO.class);
            dto.setCreatedBy(modelMapper.map(recipe.getUser(), UserWithoutPasswordDTO.class));
            recipeDTOList.add(dto);
        }
        return recipeDTOList;
    }
    public Page<RecipeDTO> getFollowedUsersRecipes(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Recipe> recipes = recipeRepository.getFollowedUsersRecipes(userId, pageable);
        return recipes.map(this::convertToDto);
    }
}
