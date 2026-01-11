package com.marketplace.auth.service;

import com.marketplace.auth.dto.AuthDTO;
import com.marketplace.auth.entity.Role;
import com.marketplace.auth.entity.User;
import com.marketplace.auth.repository.RoleRepository;
import com.marketplace.auth.repository.UserRepository;
import com.marketplace.auth.security.JwtTokenProvider;
import com.marketplace.common.constants.Constants;
import com.marketplace.common.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    public AuthDTO.LoginResponse register(AuthDTO.RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException(Constants.MSG_EMAIL_EXISTS);
        }

        Role role = roleRepository.findByName(request.getRole())
                .orElseGet(() -> {
                    Role newRole = Role.builder()
                            .name(request.getRole())
                            .description(request.getRole() + " role")
                            .build();
                    return roleRepository.save(newRole);
                });

        Set<Role> roles = new HashSet<>();
        roles.add(role);

        User user = User.builder()
                .email(request.getEmail())
                .phone(request.getPhone())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .roles(roles)
                .isVerified(false)
                .isActive(true)
                .isBlocked(false)
                .build();

        user = userRepository.save(user);

        Set<String> roleNames = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        String token = jwtTokenProvider.generateToken(user.getId(), user.getEmail(), roleNames);

        return AuthDTO.LoginResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .user(mapToUserInfo(user))
                .build();
    }

    public AuthDTO.LoginResponse login(AuthDTO.LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmailOrPhone(),
                        request.getPassword()));

        User user = userRepository.findByEmailOrPhone(request.getEmailOrPhone(), request.getEmailOrPhone())
                .orElseThrow(() -> new BadRequestException(Constants.MSG_INVALID_CREDENTIALS));

        if (user.getIsBlocked()) {
            throw new BadRequestException("Account is blocked");
        }

        if (!user.getIsActive()) {
            throw new BadRequestException("Account is inactive");
        }

        Set<String> roleNames = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        String token = jwtTokenProvider.generateToken(user.getId(), user.getEmail(), roleNames);

        return AuthDTO.LoginResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .user(mapToUserInfo(user))
                .build();
    }

    private AuthDTO.UserInfo mapToUserInfo(User user) {
        return AuthDTO.UserInfo.builder()
                .id(user.getId())
                .email(user.getEmail())
                .phone(user.getPhone())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .roles(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()))
                .isVerified(user.getIsVerified())
                .isActive(user.getIsActive())
                .build();
    }
}
