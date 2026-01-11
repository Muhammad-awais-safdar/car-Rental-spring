package com.marketplace.messaging.repository;

import com.marketplace.messaging.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByConversationIdOrderByCreatedAtAsc(Long conversationId);

    @Query("SELECT COUNT(m) FROM Message m JOIN m.conversation c JOIN c.participants p " +
            "WHERE p.user.id = :userId AND m.sender.id != :userId AND m.isRead = false")
    long countUnreadByUserId(@Param("userId") Long userId);
}
