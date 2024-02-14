package com.example.myrecipai.controller;

import com.example.myrecipai.dto.*;
import com.example.myrecipai.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @PostMapping("/verify")
    public ResponseEntity<UserWithoutPasswordDTO> verify(@RequestBody VerifyDTO verifyDTO){
        return ResponseEntity.ok(service.verify(verifyDTO));
    }
    @PostMapping("/reset/email")
    public ResponseEntity<EmailDTO> reset(@RequestBody EmailDTO emailDTO){
        return ResponseEntity.ok(service.sendResetEmail(emailDTO));
    }
    @PostMapping("/reset")
    public ResponseEntity<String> reset(@RequestBody ResetDTO resetDTO){
        service.resetPassword(resetDTO);
        return ResponseEntity.ok("Successfully changed password");
    }
}
