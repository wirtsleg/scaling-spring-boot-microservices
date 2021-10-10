package com.github.wirtzleg.scaling.repository;

import java.util.List;
import com.github.wirtzleg.scaling.dto.Email;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public interface EmailRepository extends CrudRepository<Email, Long> {

    List<Email> findAllByStatus(String status, Pageable pageable);
}
