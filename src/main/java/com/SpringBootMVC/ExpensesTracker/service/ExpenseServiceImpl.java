package com.SpringBootMVC.ExpensesTracker.service;

import com.SpringBootMVC.ExpensesTracker.DTO.ExpenseDTO;
import com.SpringBootMVC.ExpensesTracker.DTO.FilterDTO;
import com.SpringBootMVC.ExpensesTracker.entity.Category;
import com.SpringBootMVC.ExpensesTracker.entity.Expense;
import com.SpringBootMVC.ExpensesTracker.repository.ExpenseRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final ClientService clientService;
    private final CategoryService categoryService;
    private final EntityManager entityManager;

    @Autowired
    public ExpenseServiceImpl(
            ExpenseRepository expenseRepository,
            ClientService clientService,
            CategoryService categoryService,
            EntityManager entityManager) {
        this.expenseRepository = expenseRepository;
        this.clientService = clientService;
        this.categoryService = categoryService;
        this.entityManager = entityManager;
    }

    // ================= FIND BY ID =================
    @Override
    public Expense findExpenseById(int id) {
        return expenseRepository.findById(id).orElse(null);
    }

    // ================= SAVE =================
    @Transactional
    @Override
    public void save(ExpenseDTO expenseDTO) {

        if (expenseDTO.getCategoryId() == null) {
            throw new RuntimeException("Category must be selected");
        }

        Expense expense = new Expense();
        expense.setAmount(expenseDTO.getAmount());
        expense.setDateTime(expenseDTO.getDateTime());
        expense.setDescription(expenseDTO.getDescription());

        expense.setClient(
                clientService.findClientById(expenseDTO.getClientId()));

        Category category = categoryService.findCategoryById(expenseDTO.getCategoryId());

        if (category == null) {
            throw new RuntimeException("Invalid category id");
        }

        expense.setCategory(category);

        expenseRepository.save(expense);
    }

    // ================= UPDATE =================
    @Override
    public void update(ExpenseDTO expenseDTO) {

        Expense expense = expenseRepository.findById(expenseDTO.getExpenseId())
                .orElseThrow(() -> new RuntimeException("Expense not found"));

        expense.setAmount(expenseDTO.getAmount());
        expense.setDateTime(expenseDTO.getDateTime());
        expense.setDescription(expenseDTO.getDescription());

        Category category = categoryService.findCategoryById(expenseDTO.getCategoryId());

        if (category == null) {
            throw new RuntimeException("Invalid category id");
        }

        expense.setCategory(category);

        expenseRepository.save(expense);
    }

    // ================= FIND ALL =================
    @Override
    public List<Expense> findAllExpenses() {
        return expenseRepository.findAll();
    }

    // ================= FIND BY CLIENT =================
    @Override
    public List<Expense> findAllExpensesByClientId(int id) {
        return expenseRepository.findByClientId(id);
    }

    // ================= DELETE =================
    @Override
    public void deleteExpenseById(int id) {
        expenseRepository.deleteById(id);
    }

    // ================= FILTER =================
    @Override
    public List<Expense> findFilterResult(FilterDTO filter, int clientId) {

        StringBuilder query = new StringBuilder("select e from Expense e where e.client.id = " + clientId);

        if (filter.getCategoryId() != null) {
            query.append(" AND e.category.id = ").append(filter.getCategoryId());
        }

        if (filter.getFrom() > 0 || filter.getTo() > 0) {
            query.append(" AND e.amount between ")
                    .append(filter.getFrom())
                    .append(" and ")
                    .append(filter.getTo());
        }

        TypedQuery<Expense> typedQuery = entityManager.createQuery(query.toString(), Expense.class);

        return typedQuery.getResultList();
    }
}
