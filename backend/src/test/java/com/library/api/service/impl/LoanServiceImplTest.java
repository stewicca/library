package com.library.api.service.impl;

import com.library.api.dto.request.CreateLoanRequest;
import com.library.api.dto.response.LoanResponse;
import com.library.api.entity.Book;
import com.library.api.entity.LibraryItem;
import com.library.api.entity.Loan;
import com.library.api.entity.Member;
import com.library.api.exception.BusinessRuleException;
import com.library.api.exception.ResourceNotFoundException;
import com.library.api.repository.LibraryItemRepository;
import com.library.api.repository.LoanRepository;
import com.library.api.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanServiceImplTest {

    @Mock
    private LoanRepository loanRepository;
    @Mock
    private LibraryItemRepository libraryItemRepository;
    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private LoanServiceImpl loanService;

    private Member member;
    private Book bookInStock;

    @BeforeEach
    void setUp() {
        member = Member.builder().id("member-1").memberNumber("M-001").name("Alice").build();
        bookInStock = Book.builder()
                .id("book-1").title("Clean Code").author("Robert C. Martin")
                .availableCopies(3).isbn("978-0132350884").build();
    }

    @Test
    @DisplayName("TC01 — due date is exactly seven days after the loan date")
    void calculateDueDateAddsSevenDays() {
        assertThat(loanService.calculateDueDate(LocalDate.of(2025, 6, 1)))
                .isEqualTo(LocalDate.of(2025, 6, 8));
    }

    @Test
    @DisplayName("TC02 — recording a loan decrements stock and persists the loan")
    void recordLoanDecrementsStock() {
        when(memberRepository.findById("member-1")).thenReturn(Optional.of(member));
        when(libraryItemRepository.findById("book-1")).thenReturn(Optional.of(bookInStock));
        when(loanRepository.save(any(Loan.class))).thenAnswer(inv -> inv.getArgument(0));

        LoanResponse response = loanService.record(new CreateLoanRequest("member-1", List.of("book-1")));

        assertThat(bookInStock.getAvailableCopies()).isEqualTo(2);
        assertThat(response.memberNumber()).isEqualTo("M-001");
        assertThat(response.items()).hasSize(1);
        assertThat(response.dueDate()).isEqualTo(loanService.calculateDueDate(response.loanDate()));
        verify(loanRepository).save(any(Loan.class));
    }

    @Test
    @DisplayName("TC03 — borrowing an out-of-stock item is rejected as a business-rule violation")
    void recordLoanOutOfStock() {
        Book empty = Book.builder().id("book-2").title("Rare").author("X").availableCopies(0).isbn("x").build();
        when(memberRepository.findById("member-1")).thenReturn(Optional.of(member));
        when(libraryItemRepository.findById("book-2")).thenReturn(Optional.of(empty));

        assertThatThrownBy(() -> loanService.record(new CreateLoanRequest("member-1", List.of("book-2"))))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("No copies available");
        verify(loanRepository, never()).save(any());
    }

    @Test
    @DisplayName("TC04 — recording a loan for an unknown member returns not-found")
    void recordLoanUnknownMember() {
        when(memberRepository.findById("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> loanService.record(new CreateLoanRequest("ghost", List.of("book-1"))))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Member");
        verify(loanRepository, never()).save(any());
    }

    @Test
    @DisplayName("history maps stored loans to responses")
    void historyMapsLoans() {
        Loan loan = Loan.builder()
                .id("loan-1").member(member).items(List.<LibraryItem>of(bookInStock))
                .loanDate(LocalDate.of(2025, 6, 1)).dueDate(LocalDate.of(2025, 6, 8))
                .build();
        when(loanRepository.findAllByOrderByLoanDateDescIdDesc()).thenReturn(List.of(loan));

        List<LoanResponse> history = loanService.history();

        assertThat(history).hasSize(1);
        assertThat(history.getFirst().id()).isEqualTo("loan-1");
        assertThat(history.getFirst().items()).hasSize(1);
    }
}
