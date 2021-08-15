package com.github.wirtzleg.scaling.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import static com.github.wirtzleg.scaling.dto.Subscription.Status.ACTIVE;
import static com.github.wirtzleg.scaling.dto.Subscription.Status.PAST_DUE;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final UserService userService;
    private final StripeGateway stripeGateway;

    @Scheduled(cron = "*/30 * * * * *")
    public void checkSubscriptions() {
        log.info("Checking subscriptions...");

        stripeGateway.getSubscriptions().forEach(sub -> {
            userService.getById(sub.getUserId()).ifPresent(user -> {
                if (sub.getStatus() == ACTIVE && user.isExpired())
                    userService.updateUser(user.setExpired(false));

                if (sub.getStatus() == PAST_DUE && !user.isExpired())
                    userService.updateUser(user.setExpired(true));
            });
        });
    }
}
