package com.github.wirtzleg.scaling.config.interceptor;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompBrokerRelayMessageHandler;
import org.springframework.messaging.support.ExecutorChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;

import static com.github.wirtzleg.scaling.config.WebSocketConfig.BROKER_PREFIXES;
import static org.springframework.messaging.simp.SimpMessageHeaderAccessor.DESTINATION_HEADER;

public class EscapeSlashesInterceptor implements ExecutorChannelInterceptor {

    @Override
    public Message<?> beforeHandle(Message<?> message, MessageChannel channel, MessageHandler handler) {
        if (handler instanceof StompBrokerRelayMessageHandler) {
            SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.wrap(message);
            String dest = accessor.getDestination();

            if (!hasBrokerPrefix(dest))
                return message;

            String escapedDest = escapeDestination(dest);

            if (dest.equals(escapedDest))
                return message;

            accessor.setHeader(DESTINATION_HEADER, escapedDest);

            return MessageBuilder.createMessage(message.getPayload(), accessor.getMessageHeaders());
        }

        return message;
    }

    private boolean hasBrokerPrefix(String dest) {
        if (dest == null)
            return false;

        for (String prefix : BROKER_PREFIXES) {
            if (dest.startsWith(prefix))
                return true;
        }

        return false;
    }

    private String escapeDestination(String dest) {
        StringBuilder sb = new StringBuilder(dest.length());
        int slashes = 0;

        for (int i = 0; i < dest.length(); i++) {
            char ch = dest.charAt(i);

            if (ch == '/') {
                slashes++;

                sb.append(slashes > 2 ? "." : ch);
            } else
                sb.append(ch);
        }

        return sb.toString();
    }
}
