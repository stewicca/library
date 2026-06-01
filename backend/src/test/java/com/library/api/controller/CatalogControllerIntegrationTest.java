package com.library.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.api.dto.request.CreateBookRequest;
import com.library.api.entity.Book;
import com.library.api.entity.Magazine;
import com.library.api.repository.LibraryItemRepository;
import com.library.api.support.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
class CatalogControllerIntegrationTest {

    private static final String BASE = "/api/v1/books";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private LibraryItemRepository libraryItemRepository;

    @BeforeEach
    void seed() {
        libraryItemRepository.deleteAll();
        libraryItemRepository.save(Book.builder()
                .title("Clean Code").author("Martin").availableCopies(2).isbn("978-0132350884").build());
        libraryItemRepository.save(Magazine.builder()
                .title("Tech Monthly").author("Editor").availableCopies(1).edition(3).build());
    }

    @Test
    @DisplayName("TC05 — browsing the catalog without a token returns 401")
    void listRequiresAuth() throws Exception {
        mockMvc.perform(get(BASE))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "MEMBER")
    @DisplayName("a member can browse the catalog")
    void memberCanList() throws Exception {
        mockMvc.perform(get(BASE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    @WithMockUser(roles = "MEMBER")
    @DisplayName("a member can search the catalog by title")
    void memberCanSearch() throws Exception {
        mockMvc.perform(get(BASE).param("title", "clean"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].type").value("BOOK"));
    }

    @Test
    @WithMockUser(roles = "MEMBER")
    @DisplayName("a member is forbidden from adding to the catalog")
    void memberCannotAddBook() throws Exception {
        mockMvc.perform(post(BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreateBookRequest("New Book", "Author", 4, "111"))))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "LIBRARIAN")
    @DisplayName("a librarian can add a book and gets 201 Created")
    void librarianCanAddBook() throws Exception {
        mockMvc.perform(post(BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreateBookRequest("Refactoring", "Fowler", 4, "978-0201485677"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.type").value("BOOK"))
                .andExpect(jsonPath("$.data.description").value(org.hamcrest.Matchers.containsString("ISBN")));
    }
}
