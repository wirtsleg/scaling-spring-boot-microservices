package com.github.wirtzleg.scaling.repository;

import com.github.wirtzleg.scaling.dto.Email;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.Instant;
import java.util.List;

public interface EmailRepository extends CrudRepository<Email, Long> {

    @Query("""
        UPDATE emails
        SET lock_until = :lockUntil
        WHERE id in (
            SELECT e.id FROM emails e
            WHERE e.status = 'NEW' and (e.lock_until is null or e.lock_until < now())
            LIMIT :limit
            FOR UPDATE SKIP LOCKED)
        RETURNING *
    """)
    List<Email> findAllByStatus(String status, Instant lockUntil, int limit);
}
