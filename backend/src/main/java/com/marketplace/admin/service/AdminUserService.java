package com.marketplace.admin.service;

import com.marketplace.admin.dto.AdminUserDTO;
import com.marketplace.auth.entity.Role;
import com.marketplace.auth.entity.User;
import com.marketplace.auth.repository.RoleRepository;
import com.marketplace.auth.repository.UserRepository;
import com.marketplace.common.exception.BadRequestException;
import com.marketplace.common.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class AdminUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    public Page<AdminUserDTO.UserListResponse> getAllUsers(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);
        return users.map(this::convertToListResponse);
    }

    public AdminUserDTO.UserDetailResponse getUserDetails(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return convertToDetailResponse(user);
    }

    public void blockUser(Long userId, Long adminId, AdminUserDTO.BlockUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getIsBlocked()) {
            throw new BadRequestException("User is already blocked");
        }

        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));

        user.setIsBlocked(true);
        user.setBlockedAt(LocalDateTime.now());
        user.setBlockedBy(admin);
        user.setBlockedReason(request.getReason());

        userRepository.save(user);
    }

    public void unblockUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!user.getIsBlocked()) {
            throw new BadRequestException("User is not blocked");
        }

        user.setIsBlocked(false);
        user.setBlockedAt(null);
        user.setBlockedBy(null);
        user.setBlockedReason(null);

        userRepository.save(user);
    }

    public void updateUserRoles(Long userId, AdminUserDTO.UpdateRoleRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Set<Role> roles = request.getRoles().stream()
                .map(roleName -> roleRepository.findByName(roleName)
                        .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleName)))
                .collect(Collectors.toSet());

        user.setRoles(roles);
        userRepository.save(user);
    }

    public AdminUserDTO.UserStatistics getUserStatistics() {
        long totalUsers = userRepository.count();
        long blockedUsers = userRepository.countByIsBlocked(true);
        long activeUsers = totalUsers - blockedUsers;

        LocalDateTime monthStart = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        long newUsersThisMonth = userRepository.countByCreatedAtAfter(monthStart);

        return AdminUserDTO.UserStatistics.builder()
                .totalUsers(totalUsers)
                .activeUsers(activeUsers)
                .blockedUsers(blockedUsers)
                .newUsersThisMonth(newUsersThisMonth)
                .build();
    }

    private AdminUserDTO.UserListResponse convertToListResponse(User user) {
        return AdminUserDTO.UserListResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhone())
                .roles(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()))
                .isBlocked(user.getIsBlocked())
                .createdAt(user.getCreatedAt())
                .blockedAt(user.getBlockedAt())
                .build();
    }

    private AdminUserDTO.UserDetailResponse convertToDetailResponse(User user) {
        return AdminUserDTO.UserDetailResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhone())
                .roles(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()))
                .isBlocked(user.getIsBlocked())
                .blockedReason(user.getBlockedReason())
                .blockedBy(user.getBlockedBy() != null ? user.getBlockedBy().getId() : null)
                .blockedByName(user.getBlockedBy() != null
                        ? user.getBlockedBy().getFirstName() + " " + user.getBlockedBy().getLastName()
                        : null)
                .blockedAt(user.getBlockedAt())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
