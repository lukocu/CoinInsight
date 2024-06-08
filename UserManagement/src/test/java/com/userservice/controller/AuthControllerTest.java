package com.userservice.controller;

import com.userservice.model.ERole;
import com.userservice.model.Role;
import com.userservice.model.User;
import com.userservice.payload.request.LoginRequest;
import com.userservice.payload.request.SignupRequest;
import com.userservice.repository.RoleRepository;
import com.userservice.repository.UserRepository;
import com.userservice.security.config.JwtUtils;
import com.userservice.security.service.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthControllerTest {

    @Mock
    AuthenticationManager authenticationManager;

    @Mock
    UserRepository userRepository;

    @Mock
    RoleRepository roleRepository;

    @Mock
    PasswordEncoder encoder;

    @Mock
    JwtUtils jwtUtils;

    @InjectMocks
    AuthController authController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    public void authenticateUser() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("user");
        loginRequest.setPassword("password");

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        UserDetailsImpl userDetails = new UserDetailsImpl(1L, "user", "user@example.com", "password", Collections.emptyList());
        when(authentication.getPrincipal()).thenReturn(userDetails);

        when(jwtUtils.generateJwtToken(authentication)).thenReturn("mockJwtToken");

        mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"user\", \"password\":\"password\"}"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"token\":\"mockJwtToken\"}"));
    }

    @Test
    public void registerUser() throws Exception {
        SignupRequest signUpRequest = new SignupRequest();
        signUpRequest.setUsername("user");
        signUpRequest.setEmail("user@example.com");
        signUpRequest.setPassword("password");
        signUpRequest.setRole(Set.of("user"));

        when(userRepository.existsByUsername("user")).thenReturn(false);
        when(userRepository.existsByEmail("user@example.com")).thenReturn(false);
        when(roleRepository.findByName(ERole.ROLE_USER)).thenReturn(Optional.of(new Role(ERole.ROLE_USER)));

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"user\", \"email\":\"user@example.com\", \"password\":\"password\", \"role\":[\"user\"]}"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"message\":\"User registered successfully!\"}"));

        verify(userRepository, times(1)).save(any(User.class));
    }
}
