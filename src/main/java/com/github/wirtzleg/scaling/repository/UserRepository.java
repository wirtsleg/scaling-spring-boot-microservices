package com.github.wirtzleg.scaling.repository;

import com.github.wirtzleg.scaling.dto.User;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    void deleteByUsername(String username);

    Optional<User> findByUsername(String username);

    @Modifying
    @Transactional
    @Query("INSERT INTO contacts (user1_id, user2_id) VALUES ((:user1Id, :user2Id), (:user2Id, :user1Id)) ON CONFLICT DO NOTHING")
    void addContact(@Param("user1Id") Long user1Id, @Param("user2Id") Long user2Id);

    @Query("SELECT u.* FROM users u LEFT JOIN contacts c ON u.id = c.user2_id WHERE c.user1_id = :userId")
    List<User> findAllContacts(@Param("userId") Long userId);
}
