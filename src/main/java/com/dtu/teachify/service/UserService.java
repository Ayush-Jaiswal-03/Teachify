package com.dtu.teachify.service;

import com.dtu.teachify.dto.UserDTO;
import com.dtu.teachify.entity.User;
import com.dtu.teachify.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDTO createUser(UserDTO userDTO){

        // creating a new User from the DTO that came to us as input
        User newUser = User.builder()
                .email(userDTO.getEmail())
                .password(passwordEncoder.encode(userDTO.getPassword()))
                .userName(userDTO.getUserName())
                .createdAt(Instant.now())
                .build();

        newUser = userRepository.save(newUser);

        // converting the newUser object to type-[UserDTO] to be returned
        return UserDTO.builder()
                .id(newUser.getId())
                .email(newUser.getEmail())
                .userName(newUser.getUsername())
                .createdAt(newUser.getCreatedAt())
                .build();

    }

    public UserDTO updateProfile(UserDTO userDTO) {
        Optional<User> existingUser = userRepository.findByEmail(userDTO.getEmail());

        if(existingUser.isPresent()){

            if(userDTO.getEmail() != null && !userDTO.getEmail().isEmpty()){
                existingUser.get().setEmail(userDTO.getEmail());
            }

            if(userDTO.getUserName() != null && !userDTO.getUserName().isEmpty()){
                existingUser.get().setUserName(userDTO.getUserName());
            }

            userRepository.save(existingUser.get());

            return  UserDTO.builder()
                    .id(existingUser.get().getId())
                    .email(existingUser.get().getEmail())
                    .userName(existingUser.get().getUsername())
                    .createdAt(existingUser.get().getCreatedAt())
                    .build();
        }
        return null;
    }

    public void deleteProfile(String email){

        Optional<User> existingUser = userRepository.findByEmail(email);

        existingUser.ifPresent(userRepository::delete);
//        alternate code
//        if(existingUser.isPresent()){
//            userRepository.delete(existingUser.get());
//        }
    }

    public Optional<User> getCurrentProfile(){
        if(SecurityContextHolder.getContext().getAuthentication() == null){
            throw new UsernameNotFoundException("User not authenticated..");
        }
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email);
    }



}


