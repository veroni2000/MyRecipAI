package com.example.myrecipai.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CommentDTO {
    private Long id;
    private String text;
    private RecipeDTO recipe;
    private UserWithoutPasswordDTO user;
    private List<LikeDTO> likes = new ArrayList<>();
}
