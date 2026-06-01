package com.library.api.repository;

import com.library.api.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Data-access for borrowing transactions.
 *
 * @author stewicca
 * @version 1.0
 */
public interface LoanRepository extends JpaRepository<Loan, String> {

    /** Loan history, most recent first. */
    List<Loan> findAllByOrderByLoanDateDescIdDesc();
}
