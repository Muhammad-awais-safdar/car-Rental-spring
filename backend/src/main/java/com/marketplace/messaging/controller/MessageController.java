package com.marketplace.messaging.controller;

import com.marketplace.common.constants.Constants;
import com.marketplace.common.payload.ApiResponse;
import com.marketplace.messaging.dto.MessagingDTO;
import com.marketplace.messaging.service.MessagingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "*")
public class MessageController {

    @Autowired
    private MessagingService messagingService;

    @PostMapping
    public ResponseEntity<ApiResponse<MessagingDTO.MessageResponse>> sendMessage(
            @Valid @RequestBody MessagingDTO.SendMessageRequest request,
            Authentication authentication) {

        Long senderId = Long.parseLong(authentication.getPrincipal().toString());
        MessagingDTO.MessageResponse response = messagingService.sendMessage(senderId, request);

        return new ResponseEntity<>(
                ApiResponse.success(Constants.STATUS_CREATED, "Message sent successfully", response),
                HttpStatus.CREATED);
    }

    @GetMapping("/conversations")
    public ResponseEntity<ApiResponse<List<MessagingDTO.ConversationResponse>>> getUserConversations(
            Authentication authentication) {

        Long userId = Long.parseLong(authentication.getPrincipal().toString());
        List<MessagingDTO.ConversationResponse> conversations = messagingService.getUserConversations(userId);

        return ResponseEntity.ok(
                ApiResponse.success(Constants.STATUS_SUCCESS, "Conversations retrieved successfully", conversations));
    }

    @GetMapping("/conversations/{id}")
    public ResponseEntity<ApiResponse<MessagingDTO.ConversationDetailResponse>> getConversationMessages(
            @PathVariable Long id,
            Authentication authentication) {

        Long userId = Long.parseLong(authentication.getPrincipal().toString());
        MessagingDTO.ConversationDetailResponse conversation = messagingService.getConversationMessages(id, userId);

        return ResponseEntity.ok(
                ApiResponse.success(Constants.STATUS_SUCCESS, "Conversation messages retrieved successfully",
                        conversation));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getPrincipal().toString());
        long count = messagingService.getUnreadMessageCount(userId);

        return ResponseEntity.ok(
                ApiResponse.success(Constants.STATUS_SUCCESS, "Unread count retrieved successfully", count));
    }
}
