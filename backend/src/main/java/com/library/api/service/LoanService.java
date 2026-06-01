package com.library.api.service;

import com.library.api.dto.request.CreateLoanRequest;
import com.library.api.dto.response.LoanResponse;

import java.time.LocalDate;
import java.util.List;

/**
 * Borrowing use cases.
 *
 * @author stewicca
 * @version 1.0
 */
public interface LoanService {

    /**
     * Pure function: the due date is always the loan date plus the fixed loan period.
     *
     * @param loanDate the day the item is borrowed
     * @return the day it must be returned
     */
    LocalDate calculateDueDate(LocalDate loanDate);

    /**
     * Record a loan: validates the member and items, decrements stock, and persists.
     *
     * @param request which member borrows which items
     * @return the stored loan
     * @throws com.library.api.exception.ResourceNotFoundException if the member or an item is missing
     * @throws com.library.api.exception.BusinessRuleException     if any item is out of stock
     */
    LoanResponse record(CreateLoanRequest request);

    /** @return loan history, most recent first. */
    List<LoanResponse> history();

    /**
     * Export the loan history to a CSV file on disk and read it back.
     * Demonstrates file I/O ({@code Files.newBufferedWriter} + {@code Files.readAllLines}).
     *
     * @return the CSV content
     */
    String exportReportCsv();
}
