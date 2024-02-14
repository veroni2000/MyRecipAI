package com.example.myrecipai.service;

import com.example.myrecipai.config.JwtUtil;
import com.example.myrecipai.controller.AuthenticationRequest;
import com.example.myrecipai.dto.*;
import com.example.myrecipai.exception.BadRequestException;
import com.example.myrecipai.exception.NotFoundException;
import com.example.myrecipai.exception.UnauthorizedException;
import com.example.myrecipai.model.User;
import com.example.myrecipai.repository.UserRepository;
import com.example.myrecipai.utils.EmailUtil;
import com.example.myrecipai.utils.InfoValidator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final InfoValidator validator;
    private final UserRepository repository;
    private final JwtUtil jwtUtil;
    private final EmailUtil emailUtil;


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
        user.setRegistrationCode(Integer.valueOf(generateCode()));
        user.setVerified(false);
        emailUtil.sendMailWithCode(userWithoutPassword.getEmail(), String.valueOf(user.getRegistrationCode()));
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
        if (!repository.findByEmail(request.getEmail()).get().isVerified()){
            throw new BadRequestException("User is not verified");
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

    public UserWithoutPasswordDTO verify(VerifyDTO verifyDTO) {
        ModelMapper modelMapper = new ModelMapper();
        Optional<User> u = repository.findByEmail(verifyDTO.getEmail());
        if (u.isEmpty()){
            throw new NotFoundException("User not found");
        }

        User user = u.get();
        if (!verifyDTO.getEmail().equals(user.getEmail())
                || !verifyDTO.getCode().equals(user.getRegistrationCode())){
            throw new BadRequestException("Code does not match the user");
        }
        user.setVerified(true);
        repository.save(user);

        return modelMapper.map(user,UserWithoutPasswordDTO.class);
    }

    public void edit(User user, UserEditDTO userEditDTO) {
        System.out.println(userEditDTO.toString());
        if (userEditDTO.getEmail() != null) {
            if (repository.findByEmail(userEditDTO.getEmail()).isEmpty() || userEditDTO.getEmail().equals(user.getEmail())) {
                validator.emailValidate(userEditDTO.getEmail());
                user.setEmail(userEditDTO.getEmail());
            } else {
                throw new BadRequestException("Email is already used");
            }
        }

        if (userEditDTO.getFirstName() != null) {
            validator.firstNameValidate(userEditDTO.getFirstName());
            user.setFirstName(userEditDTO.getFirstName());
        }
        if (userEditDTO.getLastName() != null) {
            validator.lastNameValidate(userEditDTO.getLastName());
            user.setLastName(userEditDTO.getLastName());
        }

        if (userEditDTO.getPassword() != null) {
            validator.passwordValidate(userEditDTO.getPassword());
            user.setPassword(passwordEncoder.encode(userEditDTO.getPassword()));
        }
    }
    public EmailDTO sendResetEmail(EmailDTO emailDTO) {
        Optional<User> u = repository.findByEmail(emailDTO.getEmail());
        if (u.isEmpty()){
            throw new NotFoundException("User not found");
        }
        User user = u.get();
        user.setResetCode(Integer.valueOf(generateCode()));
        repository.save(user);
        emailUtil.sendMailWithCode(emailDTO.getEmail(), String.valueOf(user.getResetCode()));
        return emailDTO;
    }
    public void resetPassword(ResetDTO resetDTO) {
        Optional<User> u = repository.findByEmail(resetDTO.getEmail());
        if (u.isEmpty()){
            throw new NotFoundException("User not found");
        }
        User user = u.get();
        if (user.getResetCode()==null){
            throw new NotFoundException("Code not found");
        }
        if (!resetDTO.getCode().equals(user.getResetCode())){
            throw new BadRequestException("Code does not match");
        }
        validator.passwordValidate(resetDTO.getPassword());
        if (!resetDTO.getPassword().equals(resetDTO.getRepeatPassword())){
            throw new BadRequestException("Passwords do not match");
        }
        user.setPassword(passwordEncoder.encode(resetDTO.getPassword()));
        user.setResetCode(null);
        repository.save(user);
    }
    private static String generateCode() {
        Random rand = new Random();
        Long digit = (long) rand.nextInt(1000000);
        return String.format("%06d", digit);
    }
}
