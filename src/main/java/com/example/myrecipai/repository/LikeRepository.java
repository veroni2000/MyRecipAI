package com.example.myrecipai.repository;

import com.example.myrecipai.model.Like;
import com.example.myrecipai.model.Recipe;
import com.example.myrecipai.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    @Query("select l from Like l where l.recipe.id=:recipe and l.user.id=:user")
    Optional<Like> findByRecipeIdAndUserId(Long recipe, Long user);

    Like findByRecipeAndUser(Recipe recipe, User user);

    List<Like> findAllLikesByRecipeId(Long id);
}
