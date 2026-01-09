package com.SpringBootMVC.ExpensesTracker.service;

import com.SpringBootMVC.ExpensesTracker.entity.Income;

import java.util.List;

public interface IncomeService {
    List<Income> findAllIncomeByClientId(int clientId);

    Income findIncomeById(int id);

    void save(Income income);

    void deleteIncomeById(int id);
}
