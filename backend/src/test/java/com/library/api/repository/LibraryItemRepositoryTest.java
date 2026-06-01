package com.library.api.repository;

import com.library.api.entity.Book;
import com.library.api.entity.LibraryItem;
import com.library.api.entity.Magazine;
import com.library.api.support.TestcontainersConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies the catalog queries run correctly against a real PostgreSQL (Testcontainers).
 * Demonstrates database testing (Langkah 23) with {@link DataJpaTest}.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestcontainersConfiguration.class)
class LibraryItemRepositoryTest {

    @Autowired
    private LibraryItemRepository repository;

    @BeforeEach
    void seed() {
        repository.deleteAll();
        repository.save(Book.builder()
                .title("Clean Code").author("Martin").availableCopies(2).isbn("978-0132350884").build());
        repository.save(Magazine.builder()
                .title("Clean Times").author("Editor").availableCopies(0).edition(7).build());
    }

    @Test
    @DisplayName("findByTitleContainingIgnoreCase matches across the whole hierarchy, case-insensitively")
    void findByTitle() {
        List<LibraryItem> results = repository.findByTitleContainingIgnoreCase("clean");

        assertThat(results).hasSize(2);
        assertThat(results).extracting(LibraryItem::itemType)
                .containsExactlyInAnyOrder("BOOK", "MAGAZINE");
    }

    @Test
    @DisplayName("findAvailable returns only items with copies on the shelf")
    void findAvailable() {
        List<LibraryItem> available = repository.findAvailable();

        assertThat(available).hasSize(1);
        assertThat(available.getFirst().getTitle()).isEqualTo("Clean Code");
    }
}
