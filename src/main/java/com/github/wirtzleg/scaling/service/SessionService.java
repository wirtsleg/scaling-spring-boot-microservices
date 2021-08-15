package com.github.wirtzleg.scaling.service;

import com.github.wirtzleg.scaling.dto.User;
import com.github.wirtzleg.scaling.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SessionService {
    private final UserService userService;
    private final FindByIndexNameSessionRepository<? extends Session> sessionRepository;

    public List<UserResponse> getContacts(Long userId) {
        Instant now = Instant.now();
        List<User> contacts = userService.getContacts(userId);

        return contacts.stream()
                .map(user -> {
                    Instant lastAccessedTime = getLastAccessedTime(
                            sessionRepository.findByPrincipalName(user.getUsername()).values()
                    );

                    boolean online = Duration.between(lastAccessedTime, now).getSeconds() < 60;

                    return new UserResponse(user, online);
                }).toList();
    }

    private Instant getLastAccessedTime(Collection<? extends Session> sessions) {
        Instant lastAccessedTime = Instant.MIN;

        if (CollectionUtils.isEmpty(sessions))
            return lastAccessedTime;

        for (Session ses : sessions) {
            if (lastAccessedTime.isBefore(ses.getLastAccessedTime()))
                lastAccessedTime = ses.getLastAccessedTime();
        }

        return lastAccessedTime;
    }
}
