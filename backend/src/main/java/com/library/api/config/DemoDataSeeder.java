package com.library.api.config;

import com.library.api.constant.UserRole;
import com.library.api.entity.Book;
import com.library.api.entity.Magazine;
import com.library.api.entity.Member;
import com.library.api.entity.UserAccount;
import com.library.api.repository.LibraryItemRepository;
import com.library.api.repository.MemberRepository;
import com.library.api.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Seeds demo catalog items, members and a LIBRARIAN account once, when the catalog is empty.
 * Toggle with {@code library.demo.enabled}. Runs after {@link AdminSeeder}.
 *
 * @author stewicca
 * @version 1.0
 */
@Component
@Order(2)
@Slf4j
@RequiredArgsConstructor
public class DemoDataSeeder implements CommandLineRunner {

    private final DemoSeederProperties properties;
    private final LibraryItemRepository libraryItemRepository;
    private final MemberRepository memberRepository;
    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (!properties.enabled() || libraryItemRepository.count() > 0) {
            return;
        }

        libraryItemRepository.saveAll(List.of(
                Book.builder().title("Clean Code").author("Robert C. Martin")
                        .availableCopies(3).isbn("978-0132350884").build(),
                Book.builder().title("The Pragmatic Programmer").author("Hunt & Thomas")
                        .availableCopies(2).isbn("978-0201616224").build(),
                Book.builder().title("Effective Java").author("Joshua Bloch")
                        .availableCopies(0).isbn("978-0134685991").build(),
                Magazine.builder().title("Tech Monthly").author("Editorial Team")
                        .availableCopies(5).edition(42).build(),
                Magazine.builder().title("Science Today").author("Editorial Team")
                        .availableCopies(4).edition(7).build()));

        memberRepository.saveAll(List.of(
                Member.builder().memberNumber("M-001").name("Alice Reader").email("alice@example.com").build(),
                Member.builder().memberNumber("M-002").name("Bob Borrower").email("bob@example.com").build()));

        seedAccount(properties.librarianUsername(), properties.librarianPassword(), UserRole.LIBRARIAN);
        seedAccount(properties.memberUsername(), properties.memberPassword(), UserRole.MEMBER);

        log.info("Seeded demo catalog ({} items), members, LIBRARIAN '{}' and MEMBER '{}'.",
                libraryItemRepository.count(), properties.librarianUsername(), properties.memberUsername());
    }

    private void seedAccount(String username, String rawPassword, UserRole role) {
        if (!userAccountRepository.existsByUsername(username)) {
            userAccountRepository.save(UserAccount.builder()
                    .username(username)
                    .password(passwordEncoder.encode(rawPassword))
                    .role(role)
                    .enabled(true)
                    .build());
        }
    }
}
