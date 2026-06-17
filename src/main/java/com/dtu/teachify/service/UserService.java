package com.dtu.teachify.service;

import com.dtu.teachify.dto.UserDto;
import com.dtu.teachify.entity.User;
import com.dtu.teachify.exception.ApiException;
import com.dtu.teachify.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository; // required args constructor use kar rakha hai, no need for autowired
    private final PasswordEncoder passwordEncoder;

    public User createUser(UserDto userDTO){

        if(userRepository.existsByEmail(userDTO.getEmail())){
            throw new ApiException(HttpStatus.CONFLICT, "User Already Exists ...");
        }

        // creating a new User from the DTO that came to us as input
        User newUser = User.builder()
                .email(userDTO.getEmail())
                .password(passwordEncoder.encode(userDTO.getPassword()))
                .username(userDTO.getUsername())
                .createdAt(LocalDateTime.now())
                .build();

        newUser = userRepository.save(newUser);

        // converting the newUser object to type-[UserDTO] to be returned
        return User.builder()
                .email(newUser.getEmail())
                .username(newUser.getUsername())
                .createdAt(newUser.getCreatedAt())
                .build();

    }

    public UserDto updateProfile(UserDto userDTO) {
        User existingUser = userRepository.findByEmail(userDTO.getEmail());

        if(existingUser != null){

            if(userDTO.getEmail() != null && !userDTO.getEmail().isEmpty()){
                existingUser.setEmail(userDTO.getEmail());
            }

            if(userDTO.getUsername() != null && !userDTO.getUsername().isEmpty()){
                existingUser.setUsername(userDTO.getUsername());
            }

            userRepository.save(existingUser);

            return  UserDto.builder()
                    .id(existingUser.getId())
                    .email(existingUser.getEmail())
                    .username(existingUser.getUsername())
                    .createdAt(existingUser.getCreatedAt())
                    .build();
        }
        return null;
    }

    public void deleteProfile(String email){

        User existingUser = userRepository.findByEmail(email);

        if(existingUser != null){
            userRepository.delete(existingUser);
        }
//        existingUser.ifPresent(userRepository::delete);
//        alternate code
//        if(existingUser.isPresent()){
//            userRepository.delete(existingUser.get());
//        }
    }

    public User authenticateAndGetUser(){

        String email = getEmailFromToken();

        User user = userRepository.findByEmail(email);
        if(user == null) throw new UsernameNotFoundException("User not found ...");

        return user;
    }

    public String getEmailFromToken(){
        if(SecurityContextHolder.getContext().getAuthentication() == null){
            throw new UsernameNotFoundException("User not authenticated..");
        }

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        return email;
    }




}


