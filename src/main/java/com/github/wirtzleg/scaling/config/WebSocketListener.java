package com.github.wirtzleg.scaling.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.wirtzleg.scaling.dto.User;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.event.EventListener;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.github.wirtzleg.scaling.utils.Utils.user;

@Component
@RequiredArgsConstructor
public class WebSocketListener implements BeanPostProcessor {
    private final List<Handler> handlers = new CopyOnWriteArrayList<>();
    private final PathMatcher matcher = new AntPathMatcher();
    private final ObjectMapper objectMapper;
    private final SimpMessagingTemplate messagingTemplate;
    private final ScheduledExecutorService executorService;

    private static final MethodIntrospector.MetadataLookup<SubscribeMapping> SELECTOR =
            method -> AnnotatedElementUtils.getMergedAnnotation(method, SubscribeMapping.class);

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> targetClass = AopProxyUtils.ultimateTargetClass(bean);

        if (AnnotationUtils.isCandidateClass(targetClass, SubscribeMapping.class)) {
            Map<Method, SubscribeMapping> methodAnnotations = MethodIntrospector.selectMethods(targetClass, SELECTOR);

            methodAnnotations.forEach((method, annotation) -> {
                registerListener(annotation, method, bean);
            });
        }
        return bean;
    }

    private void registerListener(SubscribeMapping annotation, Method method, Object bean) {
        List<Input> inputs = Arrays.stream(method.getParameters())
                .map(parameter -> {
                    DestinationVariable destVar = parameter.getAnnotation(DestinationVariable.class);
                    AuthenticationPrincipal principal = parameter.getAnnotation(AuthenticationPrincipal.class);

                    return new Input(
                            parameter.getType(),
                            destVar != null ? destVar.value() : null,
                            principal != null
                    );
                }).toList();

        handlers.add(new Handler(bean, method, annotation.value(), inputs));
    }

    @EventListener(SessionSubscribeEvent.class)
    public void onSubscribe(SessionSubscribeEvent evt) throws Exception {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(evt.getMessage());
        String dest = accessor.getDestination();

        if (StringUtils.isEmpty(dest))
            return;

        dest = StringUtils.removeStart(dest, "/topic");

        boolean userDest = dest.startsWith("/user");
        User user = user(accessor.getUser());

        if (userDest)
            dest = StringUtils.removeStart(dest, "/user");

        for (Handler handler : handlers) {
            for (String pattern : handler.patterns) {
                if (matcher.match(pattern, dest)) {
                    Map<String, String> variables = matcher.extractUriTemplateVariables(pattern, dest);
                    Object[] args = getArguments(handler, variables, user);

                    Object result = ReflectionUtils.invokeMethod(handler.method, handler.bean, args);

                    if (result != null)
                        sendWithDelay(dest, userDest, user, result);
                }
            }
        }
    }

    private void sendWithDelay(String dest, boolean userDest, User user, Object result) {
        executorService.schedule(() -> {
            if (userDest)
                messagingTemplate.convertAndSendToUser(user.getUsername(), dest, result);
            else
                messagingTemplate.convertAndSend("/topic" + dest, result);
        }, 50, TimeUnit.MILLISECONDS);
    }

    private Object[] getArguments(Handler handler, Map<String, String> variables, User user) throws IOException {
        Object[] args = new Object[handler.inputs.size()];

        for (int i = 0; i < handler.inputs.size(); i++) {
            Input input = handler.inputs.get(i);

            if (input.destinationVariable != null)
                args[i] = readValue(variables, input);
            else if (input.principal)
                args[i] = user;
        }
        return args;
    }

    private Object readValue(Map<String, String> variables, Input input) throws JsonProcessingException {
        if (String.class.equals(input.clazz))
            return variables.get(input.destinationVariable);

        return objectMapper.readValue(variables.get(input.destinationVariable), input.clazz);
    }

    @Data
    private static class Handler {
        private final Object bean;
        private final Method method;
        private final String[] patterns;
        private final List<Input> inputs;
    }

    @Data
    private static class Input {
        private final Class<?> clazz;
        private final String destinationVariable;
        private final boolean principal;
    }
}
