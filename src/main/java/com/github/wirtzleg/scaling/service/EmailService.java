package com.github.wirtzleg.scaling.service;

import com.github.wirtzleg.scaling.dto.Email;
import com.github.wirtzleg.scaling.repository.EmailRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

import static com.github.wirtzleg.scaling.dto.EmailStatus.NEW;
import static com.github.wirtzleg.scaling.dto.EmailStatus.SENT;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {
    private static final int BATCH_SIZE = 2;
    private static final int LOCK_FOR_MS = 120_000;

    private final EmailRepository emailRepo;

    @Scheduled(cron = "15 * * * * *")
    public void sendEmails() {
        List<Email> emails;

        while (!(emails = getNewEmails()).isEmpty()) {
            for (Email email : emails) {
                // prepare email template
                // send email

                emailRepo.save(email
                        .setStatus(SENT)
                        .setLockUntil(null)
                );
                log.info("Sent email with id={} to recipient={}", email.getId(), email.getRecipientId());
            }
        }
    }

    private List<Email> getNewEmails() {
        return emailRepo.findAllByStatus(NEW.name(), Instant.now().plusMillis(LOCK_FOR_MS), BATCH_SIZE);
    }
}
