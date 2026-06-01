package com.library.api.service.impl;

import com.library.api.dto.request.CreateMemberRequest;
import com.library.api.dto.response.MemberResponse;
import com.library.api.entity.Member;
import com.library.api.exception.BusinessRuleException;
import com.library.api.exception.ResourceNotFoundException;
import com.library.api.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberServiceImplTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberServiceImpl memberService;

    @Test
    @DisplayName("register stores a member with a unique member number")
    void registerSucceeds() {
        when(memberRepository.existsByMemberNumber("M-001")).thenReturn(false);
        when(memberRepository.save(any(Member.class))).thenAnswer(inv -> inv.getArgument(0));

        MemberResponse response = memberService.register(
                new CreateMemberRequest("M-001", "Alice", "alice@example.com"));

        assertThat(response.memberNumber()).isEqualTo("M-001");
        assertThat(response.name()).isEqualTo("Alice");
    }

    @Test
    @DisplayName("register rejects a duplicate member number")
    void registerDuplicate() {
        when(memberRepository.existsByMemberNumber("M-001")).thenReturn(true);

        assertThatThrownBy(() -> memberService.register(
                new CreateMemberRequest("M-001", "Alice", "alice@example.com")))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("M-001");
        verify(memberRepository, never()).save(any());
    }

    @Test
    @DisplayName("getById returns not-found for an unknown id")
    void getByIdNotFound() {
        when(memberRepository.findById("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> memberService.getById("ghost"))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
