
package com.ritik.expense_tracker.expenses;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    @Query("""
            SELECT e FROM Expense e
            WHERE (:category IS NULL OR LOWER(e.category) = LOWER(:category))
            AND (:start IS NULL OR e.expenseDate >= :start)
            AND (:end IS NULL OR e.expenseDate <= :end)
            ORDER BY e.expenseDate DESC, e.id DESC
            """)
    List<Expense> searchExpenses(
            @Param("category") String category,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );
}
