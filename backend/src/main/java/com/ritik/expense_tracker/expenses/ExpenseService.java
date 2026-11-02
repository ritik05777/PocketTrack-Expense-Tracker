
package com.ritik.expense_tracker.expenses;

import com.ritik.expense_tracker.dto.ExpenseRequest;
import com.ritik.expense_tracker.dto.ExpenseResponse;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class ExpenseService {

    private final ExpenseRepository repository;

    public ExpenseService(ExpenseRepository repository) {
        this.repository = repository;
    }

    // GET: Fetch all expenses with optional filters
    public List<ExpenseResponse> getAll(
            String category,
            LocalDate start,
            LocalDate end) {

        // Validate date range
        if (start != null && end != null && end.isBefore(start)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "endDate must be on or after startDate"
            );
        }

        // Normalize category
        String selectedCategory =
                (category == null || category.isBlank())
                        ? null
                        : category.trim();

        // Fetch matching expenses
        return repository.searchExpenses(
                        selectedCategory,
                        start,
                        end
                )
                .stream()
                .map(ExpenseResponse::from)
                .toList();
    }

    // GET: Fetch expense by ID
    public ExpenseResponse getById(Long id) {
        return ExpenseResponse.from(find(id));
    }

    // POST: Create a new expense
    public ExpenseResponse create(ExpenseRequest request) {

        Expense expense = toEntity(request, new Expense());

        Expense savedExpense = repository.save(expense);

        return ExpenseResponse.from(savedExpense);
    }

    // PUT: Update an existing expense
    public ExpenseResponse update(Long id, ExpenseRequest request) {

        Expense expense = find(id);

        toEntity(request, expense);

        Expense updatedExpense = repository.save(expense);

        return ExpenseResponse.from(updatedExpense);
    }

    // DELETE: Delete an expense
    public void delete(Long id) {
        repository.delete(find(id));
    }

    // Calculate all-time spending
    public BigDecimal getTotal() {

        return repository.findAll()
                .stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Calculate current month's spending
    public BigDecimal getCurrentMonthTotal() {

        LocalDate now = LocalDate.now();

        return repository.findAll()
                .stream()
                .filter(e ->
                        e.getExpenseDate().getYear() == now.getYear()
                                && e.getExpenseDate().getMonth() == now.getMonth()
                )
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Find expense or return 404
    private Expense find(Long id) {

        return repository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Expense not found with ID: " + id
                        )
                );
    }

    // Convert request DTO into Expense entity
    private Expense toEntity(ExpenseRequest request, Expense expense) {

        expense.setTitle(request.title().trim());
        expense.setAmount(request.amount());
        expense.setCategory(request.category().trim());
        expense.setExpenseDate(request.expenseDate());
        expense.setNotes(request.notes());

        return expense;
    }
}
