package com.example.myrecipai.repository;

import com.example.myrecipai.model.Ingredient;
import com.example.myrecipai.model.Recipe;
import com.example.myrecipai.model.RecipeIngredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecipeRepository  extends JpaRepository<Recipe, Long> {
    List<Recipe> findAllByUserIdOrderByDateCreatedDesc(Long user);
    Optional<Recipe> findById(Long id);
    @Query("select r from Recipe r order by r.id desc")
    Page<Recipe> getAllRecipesReversed(Pageable pageable);
    @Query("SELECT r from Recipe r where r.title like concat('%', :title, '%') order by r.id desc")
    Page<Recipe> searchRecipes(String title, Pageable pageable);
    @Query("select r from Recipe r join Follow f on r.user.id = f.followed.id and f.following.id = :userId order by r.id desc")
    Page<Recipe> getFollowedUsersRecipes(Long userId, Pageable pageable);
    @Query("select r from Recipe r where r.recipeTime is null or r.recipeTime = '' " +
            "or r.recipeCalories is null or r.recipeCalories = '' or r.recipePrice is null or r.recipePrice = ''")
    List<Recipe> getEmptyTimeCalPrice();
}
