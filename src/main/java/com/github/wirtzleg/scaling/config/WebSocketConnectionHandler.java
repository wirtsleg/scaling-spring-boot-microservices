package com.github.wirtzleg.scaling.config;

import com.github.wirtzleg.scaling.dto.User;
import com.github.wirtzleg.scaling.dto.UserResponse;
import com.github.wirtzleg.scaling.service.SessionService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;

import java.util.List;

import static com.github.wirtzleg.scaling.utils.Utils.user;

public class WebSocketConnectionHandler extends WebSocketHandlerDecorator {

    private final SimpMessagingTemplate messagingTemplate;
    private final SessionService sessionService;

    public WebSocketConnectionHandler(
            WebSocketHandler delegate,
            SimpMessagingTemplate messagingTemplate,
            SessionService sessionService
    ) {
        super(delegate);

        this.messagingTemplate = messagingTemplate;
        this.sessionService = sessionService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);

        User user = user(session.getPrincipal());

        if (user != null) {
            Object payload = List.of(new UserResponse(user, true));

            sessionService.getContacts(user.getId()).stream()
                    .filter(UserResponse::isOnline)
                    .forEach(contact -> {
                        messagingTemplate.convertAndSendToUser(contact.getId().toString(), "/topic/contacts", payload);
                    });
        }
    }
}
