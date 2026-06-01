package com.library.api.service.impl;

import com.library.api.dto.request.CreateLoanRequest;
import com.library.api.dto.response.LoanResponse;
import com.library.api.entity.LibraryItem;
import com.library.api.entity.Loan;
import com.library.api.entity.Member;
import com.library.api.exception.ResourceNotFoundException;
import com.library.api.repository.LibraryItemRepository;
import com.library.api.repository.LoanRepository;
import com.library.api.repository.MemberRepository;
import com.library.api.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Default {@link LoanService}. The seven-day loan period is the single business constant;
 * recording a loan walks the requested items, decrementing stock through the entity's own
 * {@link LibraryItem#borrowOne()} guard.
 *
 * @author stewicca
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {

    /** Items are due back this many days after they are borrowed. */
    private static final int LOAN_PERIOD_DAYS = 7;

    private final LoanRepository loanRepository;
    private final LibraryItemRepository libraryItemRepository;
    private final MemberRepository memberRepository;

    @Override
    public LocalDate calculateDueDate(LocalDate loanDate) {
        return loanDate.plusDays(LOAN_PERIOD_DAYS);
    }

    @Override
    @Transactional
    public LoanResponse record(CreateLoanRequest request) {
        Member member = memberRepository.findById(request.memberId())
                .orElseThrow(() -> new ResourceNotFoundException("Member not found: " + request.memberId()));

        List<LibraryItem> borrowed = new ArrayList<>();
        for (String itemId : request.itemIds()) {
            LibraryItem item = libraryItemRepository.findById(itemId)
                    .orElseThrow(() -> new ResourceNotFoundException("Item not found: " + itemId));
            item.borrowOne(); // throws BusinessRuleException when out of stock
            borrowed.add(item);
        }

        LocalDate today = LocalDate.now();
        Loan loan = Loan.builder()
                .member(member)
                .items(borrowed)
                .loanDate(today)
                .dueDate(calculateDueDate(today))
                .build();
        return LoanResponse.from(loanRepository.save(loan));
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoanResponse> history() {
        return loanRepository.findAllByOrderByLoanDateDescIdDesc().stream()
                .map(LoanResponse::from)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public String exportReportCsv() {
        List<Loan> loans = loanRepository.findAllByOrderByLoanDateDescIdDesc();
        try {
            Path report = Files.createTempFile("loan-report-", ".csv");
            try (var writer = Files.newBufferedWriter(report, StandardCharsets.UTF_8)) {
                writer.write("loanId,memberNumber,memberName,itemCount,loanDate,dueDate");
                writer.newLine();
                for (Loan loan : loans) {
                    writer.write(String.join(",",
                            loan.getId(),
                            loan.getMember().getMemberNumber(),
                            loan.getMember().getName(),
                            String.valueOf(loan.getItems().size()),
                            String.valueOf(loan.getLoanDate()),
                            String.valueOf(loan.getDueDate())));
                    writer.newLine();
                }
            }
            // Read the file back to prove the round-trip (Langkah 10 — file access).
            return String.join("\n", Files.readAllLines(report, StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to write loan report", e);
        }
    }
}
