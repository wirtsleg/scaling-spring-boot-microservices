package com.github.wirtzleg.scaling.service;

import com.github.wirtzleg.scaling.dto.ChatMessage;
import com.github.wirtzleg.scaling.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

import static com.github.wirtzleg.scaling.utils.Utils.chatId;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatMessageRepository chatMessageRepo;

    @Cacheable(value = "chatMessages")
    public List<ChatMessage> getLastMessages(String chatId) {
        return chatMessageRepo.findAllByChatIdOrderByCreatedAtDesc(chatId, PageRequest.of(0, 10)).stream()
                .sorted(Comparator.comparing(ChatMessage::getCreatedAt))
                .toList();
    }

    @CacheEvict(value = "chatMessages", key = "#result.chatId")
    public ChatMessage saveMessage(Long authorId, Long contactId, String text) {
        String chatId = chatId(authorId, contactId);

        ChatMessage msg = new ChatMessage()
                .setChatId(chatId)
                .setAuthorId(authorId)
                .setText(text);

        return chatMessageRepo.save(msg);
    }

    public List<ChatMessage> getUnprocessedMessages() {
        return null;
    }
}
