package com.example.myrecipai.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ResetDTO {
    private Integer code;
    private String email;
    private String password;
    private String repeatPassword;
}