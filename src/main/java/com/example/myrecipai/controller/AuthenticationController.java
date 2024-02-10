package com.example.myrecipai.controller;

import com.example.myrecipai.dto.RegisterUserDTO;
import com.example.myrecipai.dto.UserWithoutPasswordDTO;
import com.example.myrecipai.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class AuthenticationController {
    private final UserService service;
    @PostMapping("/register")
    public ResponseEntity<UserWithoutPasswordDTO> register(@RequestBody RegisterUserDTO request){
        return ResponseEntity.ok(service.registerUser(request));
    }
    @PostMapping("/login")
    public ResponseEntity<UserWithoutPasswordDTO> login(@RequestBody AuthenticationRequest request) {
        UserWithoutPasswordDTO userDTO = service.login(request);
        return ResponseEntity.ok(userDTO);
    }
}
