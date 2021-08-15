package com.github.wirtzleg.scaling.controller;

import com.github.wirtzleg.scaling.dto.ContactRequest;
import com.github.wirtzleg.scaling.dto.User;
import com.github.wirtzleg.scaling.dto.UserResponse;
import com.github.wirtzleg.scaling.service.SessionService;
import com.github.wirtzleg.scaling.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@Controller
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final SessionService sessionService;

    @GetMapping("/api/v1/user")
    public UserResponse getUser(@AuthenticationPrincipal User user) {
        return new UserResponse(user, true);
    }

    @PutMapping("/api/v1/contacts")
    public void addContact(@AuthenticationPrincipal User user, ContactRequest req) {
        userService.addContact(user, req.getUsername());
    }

    @SubscribeMapping("/topic/contacts")
    public List<UserResponse> contacts(@AuthenticationPrincipal User user) {
        return sessionService.getContacts(user.getId());
    }
}
