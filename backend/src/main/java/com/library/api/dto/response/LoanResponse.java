package com.library.api.dto.response;

import com.library.api.entity.Loan;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

/**
 * Client-facing view of a borrowing transaction.
 *
 * @author stewicca
 * @version 1.0
 */
@Builder
public record LoanResponse(
        String id,
        String memberId,
        String memberNumber,
        String memberName,
        List<LibraryItemResponse> items,
        LocalDate loanDate,
        LocalDate dueDate
) {
    public static LoanResponse from(Loan loan) {
        return LoanResponse.builder()
                .id(loan.getId())
                .memberId(loan.getMember().getId())
                .memberNumber(loan.getMember().getMemberNumber())
                .memberName(loan.getMember().getName())
                .items(loan.getItems().stream().map(LibraryItemResponse::from).toList())
                .loanDate(loan.getLoanDate())
                .dueDate(loan.getDueDate())
                .build();
    }
}
