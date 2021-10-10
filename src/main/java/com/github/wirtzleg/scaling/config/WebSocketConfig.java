package com.github.wirtzleg.scaling.config;

import java.util.EnumSet;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.wirtzleg.scaling.config.interceptor.EscapeSlashesInterceptor;
import com.github.wirtzleg.scaling.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.session.web.socket.server.SessionRepositoryMessageInterceptor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

import static org.springframework.messaging.simp.SimpMessageType.CONNECT;
import static org.springframework.messaging.simp.SimpMessageType.MESSAGE;
import static org.springframework.messaging.simp.SimpMessageType.SUBSCRIBE;
import static org.springframework.messaging.simp.SimpMessageType.UNSUBSCRIBE;

@Configuration
@RequiredArgsConstructor
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {
    public static final long[] HEARTBEAT = {10_000, 10_000};
    public static final int MAX_TEXT_MESSAGE_SIZE = 10 * 1024 * 1024;
    public static final int MAX_WORKERS_COUNT = Math.max(2, Runtime.getRuntime().availableProcessors());
    public static final int TASK_QUEUE_SIZE = 10_000;
    public static final String[] BROKER_PREFIXES = new String[]{"/topic"};
    public static final String[] APP_PREFIXES = new String[]{"/topic/", "/user", "/app"};

    @Lazy
    private final SimpMessagingTemplate messagingTemplate;
    @Lazy
    private final SessionService sessionService;
    @Lazy
    private final SessionRepository<? extends Session> sessionRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
                .addEndpoint("/chat")
                .addInterceptors(sessionRepositoryInterceptor());

        registry.setErrorHandler(new StompSubProtocolErrorHandler());
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setPreservePublishOrder(true)
                .setApplicationDestinationPrefixes(APP_PREFIXES)
                .enableSimpleBroker(BROKER_PREFIXES)
                .setHeartbeatValue(HEARTBEAT)
                .setTaskScheduler(getHeartbeatScheduler());

        registry.configureBrokerChannel()
                .interceptors(new EscapeSlashesInterceptor())
                .taskExecutor().corePoolSize(1).maxPoolSize(MAX_WORKERS_COUNT).queueCapacity(TASK_QUEUE_SIZE);
    }

    @Override
    protected void customizeClientInboundChannel(ChannelRegistration registration) {
        registration
                .interceptors(sessionRepositoryInterceptor(), new EscapeSlashesInterceptor())
                .taskExecutor().corePoolSize(1).maxPoolSize(MAX_WORKERS_COUNT).queueCapacity(TASK_QUEUE_SIZE);
    }

    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        registration
                .taskExecutor().corePoolSize(1).maxPoolSize(MAX_WORKERS_COUNT).queueCapacity(TASK_QUEUE_SIZE);
    }

    @Override
    public boolean configureMessageConverters(List<MessageConverter> msgConverters) {
        MappingJackson2MessageConverter jsonConverter = new MappingJackson2MessageConverter();
        jsonConverter.setObjectMapper(objectMapper);

        msgConverters.add(new StringMessageConverter());
        msgConverters.add(jsonConverter);

        return false;
    }

    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        messages.simpSubscribeDestMatchers("/**").authenticated();
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
        registry.setMessageSizeLimit(MAX_TEXT_MESSAGE_SIZE)
                .addDecoratorFactory(hnd -> new WebSocketConnectionHandler(hnd, messagingTemplate, sessionService));
    }

    @Override
    protected boolean sameOriginDisabled() {
        return true;
    }

    @Bean
    public SessionRepositoryMessageInterceptor<? extends Session> sessionRepositoryInterceptor() {
        SessionRepositoryMessageInterceptor<? extends Session> interceptor = new SessionRepositoryMessageInterceptor<>(sessionRepository);
        interceptor.setMatchingMessageTypes(EnumSet.of(CONNECT, MESSAGE, SUBSCRIBE, UNSUBSCRIBE, SimpMessageType.HEARTBEAT));

        return interceptor;
    }

    @Bean
    public TaskScheduler getHeartbeatScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(1);
        scheduler.initialize();

        return scheduler;
    }
}
