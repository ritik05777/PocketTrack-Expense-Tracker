package com.ritik.expense_tracker.expenses;

import com.ritik.expense_tracker.dto.ExpenseRequest;
import com.ritik.expense_tracker.dto.ExpenseResponse;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/expenses")
@CrossOrigin(origins = {"http://localhost:5500", "http://127.0.0.1:5500", "http://localhost:3000"})
public class ExpenseController {
    private final ExpenseService service;
    public ExpenseController(ExpenseService service) { this.service = service; }

    @GetMapping public List<ExpenseResponse> getAll(
        @RequestParam(required = false) String category,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return service.getAll(category, startDate, endDate);
    }
    @GetMapping("/{id}") public ExpenseResponse getById(@PathVariable Long id) { return service.getById(id); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public ExpenseResponse create(@Valid @RequestBody ExpenseRequest request) { return service.create(request); }
    @PutMapping("/{id}") public ExpenseResponse update(@PathVariable Long id, @Valid @RequestBody ExpenseRequest request) { return service.update(id, request); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable Long id) { service.delete(id); }
    @GetMapping("/summary") public Map<String, Object> summary() {
        return Map.of("allTimeTotal", service.getTotal(), "currentMonthTotal", service.getCurrentMonthTotal());
    }
}
