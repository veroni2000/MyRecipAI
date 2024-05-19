package com.example.myrecipai.controller;

import com.example.myrecipai.dto.LikeDTO;
import com.example.myrecipai.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/like")
public class LikeController {
    @Autowired
    private LikeService likeService;

    @PostMapping("")
    public void like(@RequestBody LikeDTO likeDTO) {
        likeService.addLike(likeDTO);
    }

    @DeleteMapping("")
    public void disLike(@RequestBody LikeDTO likeDTO) {
        try {
            likeService.disLike(likeDTO);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @GetMapping("/check")
    public Boolean checkLike(@RequestParam Long recipeId){
        return likeService.likedRecipe(recipeId);
    }
}
