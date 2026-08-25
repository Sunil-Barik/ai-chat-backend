package com.aichat.app.repository;

import com.aichat.app.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    List<Conversation> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Conversation> findByUserIdAndIsArchivedOrderByCreatedAtDesc(Long userId, Boolean isArchived);

    @Query("SELECT DISTINCT c FROM Conversation c JOIN Message m ON m.conversation.id = c.id " +
           "WHERE c.user.id = :userId AND (LOWER(c.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(m.content) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Conversation> searchConversations(@Param("userId") Long userId, @Param("query") String query);
}
