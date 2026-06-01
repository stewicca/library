package com.library.api.config;

import com.library.api.constant.UserRole;
import com.library.api.entity.UserAccount;
import com.library.api.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Seeds a single ADMIN account on startup if it does not already exist, so the system is never
 * locked out. Toggle with {@code library.admin.enabled}.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class AdminSeeder implements CommandLineRunner {

    private final AdminSeederProperties properties;
    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (!properties.enabled()) {
            return;
        }
        if (userAccountRepository.existsByUsername(properties.username())) {
            log.info("Admin account '{}' already present; skipping seed.", properties.username());
            return;
        }
        UserAccount admin = UserAccount.builder()
                .username(properties.username())
                .password(passwordEncoder.encode(properties.password()))
                .role(UserRole.ADMIN)
                .enabled(true)
                .build();
        userAccountRepository.save(admin);
        log.info("Seeded ADMIN account '{}'.", properties.username());
    }
}
