package com.ritik.expense_tracker.dto;

import com.ritik.expense_tracker.expenses.Expense;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ExpenseResponse(Long id, String title, BigDecimal amount, String category,
                              LocalDate expenseDate, String notes,
                              LocalDateTime createdAt, LocalDateTime updatedAt) {
    public static ExpenseResponse from(Expense e) {
        return new ExpenseResponse(e.getId(), e.getTitle(), e.getAmount(), e.getCategory(),
                e.getExpenseDate(), e.getNotes(), e.getCreatedAt(), e.getUpdatedAt());
    }
}
