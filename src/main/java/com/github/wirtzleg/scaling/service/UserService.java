package com.github.wirtzleg.scaling.service;

import com.github.wirtzleg.scaling.dto.User;
import com.github.wirtzleg.scaling.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsManager {
    private final UserRepository userRepo;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public void createUser(UserDetails user) {
        User user0 = (User) user;

        user0.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepo.save((User) user);
    }

    @Override
    public void updateUser(UserDetails user) {
        userRepo.save((User) user);
    }

    @Override
    public void deleteUser(String username) {
        userRepo.deleteByUsername(username);
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);
    }

    @Override
    public boolean userExists(String username) {
        return userRepo.findByUsername(username).isPresent();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found by name: " + username));
    }

    public Optional<User> getById(Long id) {
        return userRepo.findById(id);
    }

    @Transactional
    @CacheEvict(value = "contacts", key = "user.id")
    public void addContact(User user, String username) {
        userRepo.findByUsername(username).ifPresent(user2 -> {
            userRepo.addContact(user.getId(), user2.getId());
        });
    }

    @Cacheable("contacts")
    public List<User> getContacts(Long userId) {
        log.info("Getting contacts not from cache...");
        return userRepo.findAllContacts(userId);
    }
}
