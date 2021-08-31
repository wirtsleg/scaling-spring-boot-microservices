package com.github.wirtzleg.scaling.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.LockConfiguration;
import net.javacrumbs.shedlock.core.LockProvider;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

import static com.github.wirtzleg.scaling.dto.Subscription.Status.ACTIVE;
import static com.github.wirtzleg.scaling.dto.Subscription.Status.PAST_DUE;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final UserService userService;
    private final StripeGateway stripeGateway;
    private final LockProvider lockProvider;

    @Scheduled(cron = "*/30 * * * * *")
    public void checkSubscriptions() {
        LockConfiguration lockCfg = new LockConfiguration(
                Instant.now(),
                "checkSubscriptions",
                Duration.ofSeconds(20),
                Duration.ofSeconds(10)
        );

        lockProvider.lock(lockCfg).ifPresent(lock -> {
            log.info("Checking subscriptions...");

            try {
                stripeGateway.getSubscriptions().forEach(sub -> {
                    userService.getById(sub.getUserId()).ifPresent(user -> {
                        if (sub.getStatus() == ACTIVE && user.isExpired())
                            userService.updateUser(user.setExpired(false));

                        if (sub.getStatus() == PAST_DUE && !user.isExpired())
                            userService.updateUser(user.setExpired(true));
                    });
                });
            } finally {
                lock.unlock();
            }
        });
    }
}
