package com.github.wirtzleg.scaling.repository;

import com.github.wirtzleg.scaling.dto.ChatMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends CrudRepository<ChatMessage, Long> {

    List<ChatMessage> findAllByChatIdOrderByCreatedAtDesc(String chatId, Pageable pageable);
}
