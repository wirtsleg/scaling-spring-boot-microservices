package com.github.wirtzleg.scaling.service;

import com.github.wirtzleg.scaling.dto.Email;
import com.github.wirtzleg.scaling.repository.EmailRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.github.wirtzleg.scaling.dto.EmailStatus.NEW;
import static com.github.wirtzleg.scaling.dto.EmailStatus.SENT;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {
    private static final PageRequest BATCH_SIZE = PageRequest.of(0, 2);

    private final EmailRepository emailRepo;

    @Scheduled(cron = "15 * * * * *")
    public void sendEmails() {
        List<Email> emails;

        while (!(emails = emailRepo.findAllByStatus(NEW.name(), BATCH_SIZE)).isEmpty()) {
            for (Email email : emails) {
                // prepare email template
                // send email

                emailRepo.save(email.setStatus(SENT));
                log.info("Sent email with id={} to recipient={}", email.getId(), email.getRecipientId());
            }
        }
    }
}
