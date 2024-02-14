package com.example.myrecipai.dto;

import com.example.myrecipai.model.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserEditDTO extends User {
    private String token;
}