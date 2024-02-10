package com.example.myrecipai.controller;

import com.example.myrecipai.dto.UserWithoutPasswordDTO;
import com.example.myrecipai.model.User;
import com.example.myrecipai.repository.UserRepository;
import com.example.myrecipai.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    // Assuming you have a UserService to handle user-related operations

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserWithoutPasswordDTO> getUserById(@PathVariable Long userId) {
        // Fetch user details based on the userId using the UserService
        Optional<User> user = userRepository.findById(userId);
        ModelMapper modelMapper = new ModelMapper();
        // Check if the user exists and return the appropriate response
        if (user.isPresent()) {
            System.out.println(user);
            UserWithoutPasswordDTO dto = modelMapper.map(user, UserWithoutPasswordDTO.class);
            System.out.println(dto.toString());
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

