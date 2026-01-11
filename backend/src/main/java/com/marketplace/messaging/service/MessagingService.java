package com.marketplace.messaging.service;

import com.marketplace.auth.entity.User;
import com.marketplace.auth.repository.UserRepository;
import com.marketplace.common.exception.BadRequestException;
import com.marketplace.common.exception.ResourceNotFoundException;
import com.marketplace.listing.entity.Listing;
import com.marketplace.listing.repository.ListingRepository;
import com.marketplace.messaging.dto.MessagingDTO;
import com.marketplace.messaging.entity.Conversation;
import com.marketplace.messaging.entity.ConversationParticipant;
import com.marketplace.messaging.entity.Message;
import com.marketplace.messaging.repository.ConversationRepository;
import com.marketplace.messaging.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class MessagingService {

        @Autowired
        private ConversationRepository conversationRepository;

        @Autowired
        private MessageRepository messageRepository;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private ListingRepository listingRepository;

        public MessagingDTO.MessageResponse sendMessage(Long senderId, MessagingDTO.SendMessageRequest request) {
                User sender = userRepository.findById(senderId)
                                .orElseThrow(() -> new ResourceNotFoundException("Sender not found"));

                User receiver = userRepository.findById(request.getReceiverId())
                                .orElseThrow(() -> new ResourceNotFoundException("Receiver not found"));

                Listing listing = null;
                if (request.getListingId() != null) {
                        listing = listingRepository.findById(request.getListingId())
                                        .orElseThrow(() -> new ResourceNotFoundException("Listing not found"));
                }

                // Find or create conversation
                Conversation conversation = findOrCreateConversation(sender, receiver, listing);

                // Create message
                Message message = Message.builder()
                                .conversation(conversation)
                                .sender(sender)
                                .content(request.getContent())
                                .isRead(false)
                                .build();

                message = messageRepository.save(message);
                return convertMessageToResponse(message);
        }

        public List<MessagingDTO.ConversationResponse> getUserConversations(Long userId) {
                List<Conversation> conversations = conversationRepository.findByUserId(userId);

                return conversations.stream()
                                .map(conv -> convertConversationToResponse(conv, userId))
                                .collect(Collectors.toList());
        }

        public MessagingDTO.ConversationDetailResponse getConversationMessages(Long conversationId, Long userId) {
                Conversation conversation = conversationRepository.findById(conversationId)
                                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found"));

                // Verify user is participant
                boolean isParticipant = conversation.getParticipants().stream()
                                .anyMatch(p -> p.getUser().getId().equals(userId));

                if (!isParticipant) {
                        throw new BadRequestException("You are not a participant in this conversation");
                }

                // Mark messages as read
                List<Message> messages = messageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId);
                messages.stream()
                                .filter(m -> !m.getSender().getId().equals(userId) && !m.getIsRead())
                                .forEach(m -> {
                                        m.setIsRead(true);
                                        messageRepository.save(m);
                                });

                // Get other participant
                User otherUser = conversation.getParticipants().stream()
                                .map(ConversationParticipant::getUser)
                                .filter(u -> !u.getId().equals(userId))
                                .findFirst()
                                .orElse(null);

                return MessagingDTO.ConversationDetailResponse.builder()
                                .id(conversation.getId())
                                .listingId(conversation.getVehicle() != null ? conversation.getVehicle().getId() : null)
                                .listingTitle(conversation.getVehicle() != null ? conversation.getVehicle().getTitle()
                                                : null)
                                .otherUserId(otherUser != null ? otherUser.getId() : null)
                                .otherUserName(otherUser != null
                                                ? otherUser.getFirstName() + " " + otherUser.getLastName()
                                                : null)
                                .messages(messages.stream().map(this::convertMessageToResponse)
                                                .collect(Collectors.toList()))
                                .build();
        }

        public long getUnreadMessageCount(Long userId) {
                return messageRepository.countUnreadByUserId(userId);
        }

        private Conversation findOrCreateConversation(User user1, User user2, Listing listing) {
                if (listing != null) {
                        Optional<Conversation> existing = conversationRepository.findByParticipantsAndListing(
                                        user1.getId(), user2.getId(), listing.getId());
                        if (existing.isPresent()) {
                                return existing.get();
                        }
                }

                // Create new conversation
                Conversation conversation = Conversation.builder()
                                .vehicle(listing)
                                .participants(new ArrayList<>())
                                .messages(new ArrayList<>())
                                .build();

                conversation = conversationRepository.save(conversation);

                // Add participants
                ConversationParticipant participant1 = ConversationParticipant.builder()
                                .conversation(conversation)
                                .user(user1)
                                .build();

                ConversationParticipant participant2 = ConversationParticipant.builder()
                                .conversation(conversation)
                                .user(user2)
                                .build();

                conversation.getParticipants().add(participant1);
                conversation.getParticipants().add(participant2);

                return conversationRepository.save(conversation);
        }

        private MessagingDTO.MessageResponse convertMessageToResponse(Message message) {
                return MessagingDTO.MessageResponse.builder()
                                .id(message.getId())
                                .conversationId(message.getConversation().getId())
                                .senderId(message.getSender().getId())
                                .senderName(message.getSender().getFirstName() + " "
                                                + message.getSender().getLastName())
                                .content(message.getContent())
                                .isRead(message.getIsRead())
                                .createdAt(message.getCreatedAt())
                                .build();
        }

        private MessagingDTO.ConversationResponse convertConversationToResponse(Conversation conversation,
                        Long currentUserId) {
                // Get other participant
                User otherUser = conversation.getParticipants().stream()
                                .map(ConversationParticipant::getUser)
                                .filter(u -> !u.getId().equals(currentUserId))
                                .findFirst()
                                .orElse(null);

                // Get last message
                List<Message> messages = conversation.getMessages();
                Message lastMessage = messages.isEmpty() ? null : messages.get(messages.size() - 1);

                // Count unread messages
                long unreadCount = messages.stream()
                                .filter(m -> !m.getSender().getId().equals(currentUserId) && !m.getIsRead())
                                .count();

                return MessagingDTO.ConversationResponse.builder()
                                .id(conversation.getId())
                                .listingId(conversation.getVehicle() != null ? conversation.getVehicle().getId() : null)
                                .listingTitle(conversation.getVehicle() != null ? conversation.getVehicle().getTitle()
                                                : null)
                                .otherUserId(otherUser != null ? otherUser.getId() : null)
                                .otherUserName(otherUser != null
                                                ? otherUser.getFirstName() + " " + otherUser.getLastName()
                                                : null)
                                .lastMessage(lastMessage != null ? convertMessageToResponse(lastMessage) : null)
                                .unreadCount(unreadCount)
                                .updatedAt(conversation.getUpdatedAt())
                                .build();
        }
}
