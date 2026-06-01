package com.library.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.api.dto.request.CreateLoanRequest;
import com.library.api.entity.Book;
import com.library.api.entity.Member;
import com.library.api.repository.LibraryItemRepository;
import com.library.api.repository.LoanRepository;
import com.library.api.repository.MemberRepository;
import com.library.api.support.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
class LoanControllerIntegrationTest {

    private static final String BASE = "/api/v1/loans";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private LibraryItemRepository libraryItemRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private LoanRepository loanRepository;

    private String memberId;
    private String bookId;
    private String emptyBookId;

    @BeforeEach
    void seed() {
        loanRepository.deleteAll();
        libraryItemRepository.deleteAll();
        memberRepository.deleteAll();

        memberId = memberRepository.save(Member.builder()
                .memberNumber("M-001").name("Alice").email("alice@example.com").build()).getId();
        bookId = libraryItemRepository.save(Book.builder()
                .title("Clean Code").author("Martin").availableCopies(3).isbn("978-0132350884").build()).getId();
        emptyBookId = libraryItemRepository.save(Book.builder()
                .title("Out Of Print").author("X").availableCopies(0).isbn("000").build()).getId();
    }

    @Test
    @WithMockUser(roles = "LIBRARIAN")
    @DisplayName("TC02 — a librarian records a loan; due date is +7 days and stock drops")
    void recordLoanHappyPath() throws Exception {
        mockMvc.perform(post(BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateLoanRequest(memberId, List.of(bookId)))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.memberNumber").value("M-001"))
                .andExpect(jsonPath("$.data.items.length()").value(1))
                .andExpect(jsonPath("$.data.dueDate").value(LocalDate.now().plusDays(7).toString()));

        assertThat(libraryItemRepository.findById(bookId).orElseThrow().getAvailableCopies()).isEqualTo(2);
    }

    @Test
    @WithMockUser(roles = "LIBRARIAN")
    @DisplayName("TC03 — borrowing an out-of-stock item returns 409 Conflict")
    void recordLoanOutOfStock() throws Exception {
        mockMvc.perform(post(BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateLoanRequest(memberId, List.of(emptyBookId)))))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(roles = "LIBRARIAN")
    @DisplayName("TC04 — borrowing for an unknown member returns 404 Not Found")
    void recordLoanUnknownMember() throws Exception {
        mockMvc.perform(post(BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateLoanRequest("ghost", List.of(bookId)))))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "MEMBER")
    @DisplayName("a member is forbidden from recording loans")
    void memberCannotRecordLoan() throws Exception {
        mockMvc.perform(post(BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateLoanRequest(memberId, List.of(bookId)))))
                .andExpect(status().isForbidden());
    }
}
