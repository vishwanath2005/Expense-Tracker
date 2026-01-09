package com.SpringBootMVC.ExpensesTracker.controller;

import com.SpringBootMVC.ExpensesTracker.DTO.ExpenseDTO;
import com.SpringBootMVC.ExpensesTracker.DTO.FilterDTO;
import com.SpringBootMVC.ExpensesTracker.entity.Client;
import com.SpringBootMVC.ExpensesTracker.entity.Expense;
import com.SpringBootMVC.ExpensesTracker.entity.Income;
import com.SpringBootMVC.ExpensesTracker.service.CategoryService;
import com.SpringBootMVC.ExpensesTracker.service.ExpenseService;
import com.SpringBootMVC.ExpensesTracker.service.IncomeService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class MainController {

    private final ExpenseService expenseService;
    private final CategoryService categoryService;
    private final IncomeService incomeService;

    @Autowired
    public MainController(ExpenseService expenseService,
            CategoryService categoryService,
            IncomeService incomeService) {
        this.expenseService = expenseService;
        this.categoryService = categoryService;
        this.incomeService = incomeService;
    }

    // ================= LANDING PAGE =================
    @GetMapping("/")
    public String landingPage(HttpSession session, Model model) {
        Client client = (Client) session.getAttribute("client");
        model.addAttribute("sessionClient", client);
        return "landing-page";
    }

    // ================= ADD EXPENSE =================
    @GetMapping("/showAdd")
    public String addExpense(Model model) {
        model.addAttribute("expense", new ExpenseDTO());
        model.addAttribute("categories", categoryService.findAllCategories());
        return "add-expense";
    }

    @PostMapping("/submitAdd")
    public String submitAdd(@ModelAttribute("expense") ExpenseDTO expenseDTO,
            HttpSession session) {
        Client client = (Client) session.getAttribute("client");
        expenseDTO.setClientId(client.getId());
        expenseService.save(expenseDTO);
        return "redirect:/list";
    }

    // ================= LIST EXPENSE & INCOME =================
    @GetMapping("/list")
    public String list(Model model, HttpSession session) {

        Client client = (Client) session.getAttribute("client");
        int clientId = client.getId();

        // 1. Fetch Expenses
        List<Expense> expenseList = expenseService.findAllExpensesByClientId(clientId);
        enrichExpenseList(expenseList);

        // 2. Fetch Income
        List<Income> incomeList = incomeService.findAllIncomeByClientId(clientId);
        enrichIncomeList(incomeList);

        // 3. Calculate Balance
        int totalExpense = expenseList.stream().mapToInt(Expense::getAmount).sum();
        int totalIncome = incomeList.stream().mapToInt(Income::getAmount).sum();
        int balance = totalIncome - totalExpense;

        model.addAttribute("expenseList", expenseList);
        model.addAttribute("incomeList", incomeList);
        model.addAttribute("balance", balance);
        model.addAttribute("totalIncome", totalIncome);
        model.addAttribute("totalExpense", totalExpense);

        model.addAttribute("filter", new FilterDTO());
        model.addAttribute("categories", categoryService.findAllCategories());
        return "list-page";
    }

    // ================= UPDATE EXPENSE =================
    @GetMapping("/showUpdate")
    public String showUpdate(@RequestParam("expId") int id, Model model) {

        Expense expense = expenseService.findExpenseById(id);

        ExpenseDTO expenseDTO = new ExpenseDTO();
        expenseDTO.setExpenseId(expense.getId());
        expenseDTO.setAmount(expense.getAmount());
        expenseDTO.setDescription(expense.getDescription());
        expenseDTO.setDateTime(expense.getDateTime());

        // ✅ CORRECT (ID BASED)
        if (expense.getCategory() != null) {
            expenseDTO.setCategoryId(expense.getCategory().getId());
        }

        model.addAttribute("expense", expenseDTO);
        model.addAttribute("expenseId", id);
        model.addAttribute("categories", categoryService.findAllCategories());

        return "update-page";
    }

    @PostMapping("/submitUpdate")
    public String update(@RequestParam("expId") int id,
            @ModelAttribute("expense") ExpenseDTO expenseDTO,
            HttpSession session) {

        Client client = (Client) session.getAttribute("client");
        expenseDTO.setExpenseId(id);
        expenseDTO.setClientId(client.getId());
        expenseService.update(expenseDTO);
        return "redirect:/list";
    }

    // ================= DELETE EXPENSE =================
    @GetMapping("/delete")
    public String delete(@RequestParam("expId") int id) {
        expenseService.deleteExpenseById(id);
        return "redirect:/list";
    }

    // ================= FILTER =================
    @PostMapping("/processFilter")
    public String processFilter(@ModelAttribute("filter") FilterDTO filter,
            Model model, HttpSession session) {

        Client client = (Client) session.getAttribute("client");
        int clientId = client.getId();

        List<Expense> expenseList = expenseService.findFilterResult(filter, clientId);
        enrichExpenseList(expenseList);

        model.addAttribute("expenseList", expenseList);

        // Also add categories so the filter modal can be re-rendered if needed (though
        // it's a separate page here)
        // Actually, processFilter returns "filter-result" page. I should check if that
        // page exists or needs attention.
        // Assuming "filter-result" is similar to "list-page" or a fragment.

        return "filter-result";
    }

    // ================= HELPER =================
    private void enrichExpenseList(List<Expense> expenseList) {
        for (Expense expense : expenseList) {
            // CATEGORY
            expense.setCategoryName(
                    expense.getCategory() != null
                            ? expense.getCategory().getName()
                            : "Uncategorized");
            formatDateAndTime(expense);
        }
    }

    private void enrichIncomeList(List<Income> incomeList) {
        for (Income income : incomeList) {
            if (income.getDateTime() != null) {
                parseAndSetDate(income);
            }
        }
    }

    private void formatDateAndTime(Expense expense) {
        if (expense.getDateTime() != null) {
            parseAndSetDate(expense);
        }
    }

    // Robust Date Parsing
    private void parseAndSetDate(Object entity) {
        try {
            String dateStr = null;
            if (entity instanceof Expense)
                dateStr = ((Expense) entity).getDateTime();
            else if (entity instanceof Income)
                dateStr = ((Income) entity).getDateTime();

            if (dateStr == null || dateStr.isEmpty())
                return;

            LocalDateTime dateTime;
            try {
                dateTime = LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            } catch (Exception e) {
                // Fallback for formats without seconds (yyyy-MM-ddTHH:mm)
                dateTime = LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
            }

            if (entity instanceof Expense) {
                ((Expense) entity).setDate(dateTime.toLocalDate().toString());
                ((Expense) entity).setTime(dateTime.toLocalTime().toString());
            } else if (entity instanceof Income) {
                ((Income) entity).setDate(dateTime.toLocalDate().toString());
                ((Income) entity).setTime(dateTime.toLocalTime().toString());
            }
        } catch (Exception e) {
            System.err.println("Error parsing date: " + e.getMessage());
        }
    }
}
