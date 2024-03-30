package com.example.myrecipai.repository;

import com.example.myrecipai.model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecipeRepository  extends JpaRepository<Recipe, Long> {
    List<Recipe> findAllByUserId(Long user);
    Optional<Recipe> findById(Long id);
}
