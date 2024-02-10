package com.example.myrecipai.dto;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserWithoutPasswordDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String token;
}
