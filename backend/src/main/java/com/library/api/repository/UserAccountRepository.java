package com.library.api.repository;

import com.library.api.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Data-access for login accounts.
 *
 * @author stewicca
 * @version 1.0
 */
public interface UserAccountRepository extends JpaRepository<UserAccount, String> {
    Optional<UserAccount> findByUsername(String username);

    boolean existsByUsername(String username);
}
