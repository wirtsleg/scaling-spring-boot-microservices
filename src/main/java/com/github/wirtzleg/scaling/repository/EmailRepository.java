package com.github.wirtzleg.scaling.repository;

import com.github.wirtzleg.scaling.dto.ChatMessage;
import com.github.wirtzleg.scaling.dto.Email;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface EmailRepository extends CrudRepository<Email, Long> {

    List<Email> findAllByStatus(String status, Pageable pageable);
}
