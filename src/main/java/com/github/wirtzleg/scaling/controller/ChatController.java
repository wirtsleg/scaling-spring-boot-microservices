package com.github.wirtzleg.scaling.controller;

import com.github.wirtzleg.scaling.dto.ChatMessage;
import com.github.wirtzleg.scaling.dto.User;
import com.github.wirtzleg.scaling.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import static com.github.wirtzleg.scaling.utils.Utils.chatId;
import static com.github.wirtzleg.scaling.utils.Utils.isChatMember;

@CrossOrigin
@Controller
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @SubscribeMapping("/chat/{chatId}/messages")
    public List<ChatMessage> chatMessages(
            @AuthenticationPrincipal User user,
            @DestinationVariable("chatId") String chatId
    ) {
        if (!isChatMember(user.getId(), chatId))
            throw new AccessDeniedException("Not a chat member. UserId=" + user.getId() + ", chatId=" + chatId);

        return chatService.getLastMessages(chatId);
    }

    @MessageMapping("/chat/{contactId}/messages/add")
    public void sendMessage(
            @DestinationVariable("contactId") Long contactId,
            @AuthenticationPrincipal User user,
            String text
    ) {
        String chatId = chatId(user.getId(), contactId);
        ChatMessage msg = chatService.saveMessage(user.getId(), contactId, text);

        messagingTemplate.convertAndSend("/topic/chat/" + chatId + "/messages", List.of(msg));
    }
}
