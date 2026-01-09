package com.SpringBootMVC.ExpensesTracker.mapper;

import com.SpringBootMVC.ExpensesTracker.DTO.ExpenseDTO;
import com.SpringBootMVC.ExpensesTracker.entity.Expense;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class ExpenseMapper {

    public ExpenseDTO toDTO(Expense expense) {
        ExpenseDTO dto = new ExpenseDTO();
        dto.setExpenseId(expense.getId());
        dto.setAmount(expense.getAmount());
        dto.setDateTime(expense.getDateTime());
        dto.setDescription(expense.getDescription());

        if (expense.getCategory() != null) {
            dto.setCategoryId(expense.getCategory().getId());
        }

        if (expense.getClient() != null) {
            dto.setClientId(expense.getClient().getId());
        }

        return dto;
    }

    // Helper to format date for UI if needed (logic moved from controller)
    public void enrichExpense(Expense expense) {
        // CATEGORY
        expense.setCategoryName(
                expense.getCategory() != null
                        ? expense.getCategory().getName()
                        : "Uncategorized");

        // DATE & TIME
        if (expense.getDateTime() != null) {
            LocalDateTime dateTime = LocalDateTime.parse(
                    expense.getDateTime(),
                    DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            expense.setDate(dateTime.toLocalDate().toString());
            expense.setTime(dateTime.toLocalTime().toString());
        }
    }
}
