package com.SpringBootMVC.ExpensesTracker.controller;

import com.SpringBootMVC.ExpensesTracker.entity.Client;
import com.SpringBootMVC.ExpensesTracker.entity.Income;
import com.SpringBootMVC.ExpensesTracker.service.IncomeService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/income")
public class IncomeController {

    private final IncomeService incomeService;

    @Autowired
    public IncomeController(IncomeService incomeService) {
        this.incomeService = incomeService;
    }

    // ================= ADD INCOME =================
    @GetMapping("/showAdd")
    public String addIncome(Model model) {
        model.addAttribute("income", new Income());
        return "add-income";
    }

    @PostMapping("/submitAdd")
    public String submitAdd(@ModelAttribute("income") Income income,
            HttpSession session) {
        Client client = (Client) session.getAttribute("client");
        if (client == null) {
            return "redirect:/showLoginPage";
        }

        income.setClient(client);
        incomeService.save(income);

        return "redirect:/list";
    }

    // ================= DELETE INCOME =================
    @GetMapping("/delete")
    public String delete(@RequestParam("incomeId") int id) {
        incomeService.deleteIncomeById(id);
        return "redirect:/list";
    }
}
