package com.library.api.controller;

import com.library.api.constant.ApiRoute;
import com.library.api.dto.request.CreateLoanRequest;
import com.library.api.dto.response.LoanResponse;
import com.library.api.dto.response.WebResponse;
import com.library.api.service.LoanService;
import com.library.api.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Borrowing endpoints. All operations are staff-only ({@code LIBRARIAN}/{@code ADMIN}).
 *
 * @author stewicca
 * @version 1.0
 */
@RestController
@RequestMapping(ApiRoute.LOANS)
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAnyRole('LIBRARIAN','ADMIN')")
@Tag(name = "Loans", description = "Record borrowing transactions and view history")
public class LoanController {

    private final LoanService loanService;

    @Operation(summary = "Record a borrowing transaction; due date is set seven days out.")
    @PostMapping
    public ResponseEntity<WebResponse<LoanResponse>> record(@Valid @RequestBody CreateLoanRequest request) {
        return ResponseUtil.build(HttpStatus.CREATED, "Loan recorded", loanService.record(request));
    }

    @Operation(summary = "List loan history, most recent first.")
    @GetMapping
    public ResponseEntity<WebResponse<List<LoanResponse>>> history() {
        return ResponseUtil.ok("Loan history retrieved", loanService.history());
    }

    @Operation(summary = "Export loan history as a CSV report.")
    @GetMapping(value = "/report.csv", produces = "text/csv")
    public ResponseEntity<String> exportCsv() {
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(loanService.exportReportCsv());
    }
}
