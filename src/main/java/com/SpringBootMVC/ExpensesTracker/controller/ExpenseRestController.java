package com.SpringBootMVC.ExpensesTracker.controller;

import com.SpringBootMVC.ExpensesTracker.DTO.ExpenseDTO;
import com.SpringBootMVC.ExpensesTracker.entity.Client;
import com.SpringBootMVC.ExpensesTracker.entity.Expense;
import com.SpringBootMVC.ExpensesTracker.entity.User;
import com.SpringBootMVC.ExpensesTracker.mapper.ExpenseMapper;
import com.SpringBootMVC.ExpensesTracker.service.ClientService;
import com.SpringBootMVC.ExpensesTracker.service.ExpenseService;
import com.SpringBootMVC.ExpensesTracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseRestController {

    private final ExpenseService expenseService;
    private final ClientService clientService;
    private final UserService userService;
    private final ExpenseMapper expenseMapper;

    @Autowired
    public ExpenseRestController(ExpenseService expenseService, ClientService clientService, UserService userService,
            ExpenseMapper expenseMapper) {
        this.expenseService = expenseService;
        this.clientService = clientService;
        this.userService = userService;
        this.expenseMapper = expenseMapper;
    }

    @GetMapping
    public List<ExpenseDTO> getExpenses(@RequestParam(value = "category", required = false) String category) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        // Handle OAuth2 vs Standard Login username extraction
        if (authentication instanceof OAuth2AuthenticationToken) {
            currentPrincipalName = ((OAuth2AuthenticationToken) authentication).getPrincipal().getAttribute("email");
        }

        User user = userService.findUserByUserName(currentPrincipalName);
        Client client = clientService.findClientById(user.getId());

        List<Expense> expenses = expenseService.findAllExpensesByClientId(client.getId());

        // Filter if category is present and not "all"
        if (category != null && !category.equalsIgnoreCase("all")) {
            expenses = expenses.stream()
                    .filter(e -> e.getCategory() != null && e.getCategory().getName().equalsIgnoreCase(category))
                    .collect(Collectors.toList());
        }

        // Map to DTOs
        return expenses.stream()
                .map(expenseMapper::toDTO)
                .collect(Collectors.toList());
    }
}
