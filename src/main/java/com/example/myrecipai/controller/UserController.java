package com.example.myrecipai.controller;

import com.example.myrecipai.config.JwtUtil;
import com.example.myrecipai.dto.UserEditDTO;
import com.example.myrecipai.dto.UserWithoutPasswordDTO;
import com.example.myrecipai.model.User;
import com.example.myrecipai.repository.UserRepository;
import com.example.myrecipai.service.RecipeService;
import com.example.myrecipai.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private final UserService userService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private RecipeService recipeService;

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
    @PostMapping("/edit")
    public ResponseEntity<UserWithoutPasswordDTO> edit(@RequestBody UserEditDTO userEditDTO) {
        ModelMapper mapper = new ModelMapper();
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        userService.edit(user, userEditDTO);
        userRepository.save(user);
        String jwtToken = jwtUtil.generateToken(user);
        UserWithoutPasswordDTO userWithoutPassword = mapper.map(user, UserWithoutPasswordDTO.class);
        userWithoutPassword.setToken(jwtToken);
        return ResponseEntity.ok(userWithoutPassword);
    }

    @PostMapping("/saveImage")
    public String saveImage(@RequestParam("file") MultipartFile file) {
        return recipeService.saveImage(file);
    }
}

