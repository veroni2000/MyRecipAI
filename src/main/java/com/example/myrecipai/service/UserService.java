package com.example.myrecipai.service;

import com.example.myrecipai.config.JwtUtil;
import com.example.myrecipai.controller.AuthenticationRequest;
import com.example.myrecipai.dto.RegisterUserDTO;
import com.example.myrecipai.dto.UserWithoutPasswordDTO;
import com.example.myrecipai.exception.BadRequestException;
import com.example.myrecipai.exception.NotFoundException;
import com.example.myrecipai.exception.UnauthorizedException;
import com.example.myrecipai.model.User;
import com.example.myrecipai.repository.UserRepository;
import com.example.myrecipai.utils.InfoValidator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final InfoValidator validator;
    private final UserRepository repository;
    private final JwtUtil jwtUtil;


    public UserWithoutPasswordDTO registerUser(RegisterUserDTO request){
        if (repository.findByEmail(request.getEmail()).isPresent()) {
            throw new BadRequestException("User with this email already exists");
        }
        validator.emailValidate(request.getEmail());
        validator.passwordValidate(request.getPassword());
        validator.firstNameValidate(request.getFirstName());
        validator.lastNameValidate(request.getLastName());
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        String jwtToken = jwtUtil.generateToken(user);
        ModelMapper modelMapper = new ModelMapper();
        UserWithoutPasswordDTO userWithoutPassword = modelMapper.map(user,UserWithoutPasswordDTO.class);
        userWithoutPassword.setToken(jwtToken);
        repository.save(user);
        return userWithoutPassword;
    }

    public UserWithoutPasswordDTO login(AuthenticationRequest request) {
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new BadRequestException("Email is mandatory");
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new BadRequestException("Password is mandatory");
        }
        validator.emailValidate(request.getEmail());
        validator.passwordValidate(request.getPassword());
        if (repository.findByEmail(request.getEmail()).isPresent()) {
            User user = repository.findByEmail(request.getEmail()).get();
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                throw new UnauthorizedException("Wrong credentials");
            }
            String jwtToken = jwtUtil.generateToken(user);
            ModelMapper modelMapper = new ModelMapper();
            UserWithoutPasswordDTO userWithoutPassword = modelMapper.map(user,UserWithoutPasswordDTO.class);
            userWithoutPassword.setToken(jwtToken);
            return userWithoutPassword;
        } else {
            throw new NotFoundException("User not found");
        }
    }
}
