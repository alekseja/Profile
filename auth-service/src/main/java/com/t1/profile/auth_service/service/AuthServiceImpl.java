package com.t1.profile.auth_service.service;

import com.t1.profile.auth_service.dto.ApiDto;
import com.t1.profile.auth_service.dto.JwtAuthenticationDto;
import com.t1.profile.auth_service.dto.LoginDto;
import com.t1.profile.auth_service.dto.RegistrationDto;
import com.t1.profile.auth_service.model.Role;
import com.t1.profile.auth_service.model.User;
import com.t1.profile.auth_service.repository.RoleRepo;
import com.t1.profile.auth_service.repository.UserRepo;
import com.t1.profile.auth_service.security.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

import static com.t1.profile.auth_service.MessageType.*;
import static com.t1.profile.auth_service.RoleType.USER;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Override
    public ApiDto registerUser(RegistrationDto registrationDto) {
        if (userRepo.findByEmail(registrationDto.getEmail()) != null) {
            return new ApiDto(false, EMAIL_ALREADY_USE);
        }

        User user = new User();
        user.setEmail(registrationDto.getEmail());
        user.setFirstName(registrationDto.getFirstName());
        user.setLastName(registrationDto.getLastName());
        user.setPasswordHash(passwordEncoder.encode(registrationDto.getPassword()));

        Role userRole = roleRepo.findByName(USER);
        if (userRole == null) {
            userRole = new Role();
            userRole.setName(USER);
            roleRepo.save(userRole);
        }
        user.setRoles(Collections.singleton(userRole));

        userRepo.save(user);

        return new ApiDto(true, USER_REGISTERED_SUCCESSFULLY);
    }

    @Override
    public JwtAuthenticationDto authenticateUser(LoginDto loginDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDto.getEmail(),
                            loginDto.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = tokenProvider.generateToken(authentication);
            return new JwtAuthenticationDto(jwt);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(WRONG_EMAIL_OR_PASSWORD);
        }
    }


}
