package com.github.wirtzleg.scaling.service;

import com.github.wirtzleg.scaling.dto.Subscription;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StripeGateway {

    public Iterable<Subscription> getSubscriptions() {
        return List.of();
    }
}
