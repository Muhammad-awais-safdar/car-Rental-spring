package com.marketplace.messaging.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class MessagingDTO {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SendMessageRequest {
        @NotNull(message = "Receiver ID is required")
        private Long receiverId;

        private Long listingId;

        @NotBlank(message = "Message content is required")
        private String content;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MessageResponse {
        private Long id;
        private Long conversationId;
        private Long senderId;
        private String senderName;
        private String content;
        private Boolean isRead;
        private LocalDateTime createdAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConversationResponse {
        private Long id;
        private Long listingId;
        private String listingTitle;
        private Long otherUserId;
        private String otherUserName;
        private MessageResponse lastMessage;
        private long unreadCount;
        private LocalDateTime updatedAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConversationDetailResponse {
        private Long id;
        private Long listingId;
        private String listingTitle;
        private Long otherUserId;
        private String otherUserName;
        private List<MessageResponse> messages;
    }
}
