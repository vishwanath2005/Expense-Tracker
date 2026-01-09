package com.SpringBootMVC.ExpensesTracker.service;

import com.SpringBootMVC.ExpensesTracker.entity.Income;
import com.SpringBootMVC.ExpensesTracker.repository.IncomeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class IncomeServiceImpl implements IncomeService {

    private final IncomeRepository incomeRepository;

    @Autowired
    public IncomeServiceImpl(IncomeRepository incomeRepository) {
        this.incomeRepository = incomeRepository;
    }

    @Override
    public List<Income> findAllIncomeByClientId(int clientId) {
        return incomeRepository.findAllByClientId(clientId);
    }

    @Override
    public Income findIncomeById(int id) {
        Optional<Income> result = incomeRepository.findById(id);
        Income income = null;
        if (result.isPresent()) {
            income = result.get();
        } else {
            throw new RuntimeException("Did not find income id - " + id);
        }
        return income;
    }

    @Override
    public void save(Income income) {
        incomeRepository.save(income);
    }

    @Override
    public void deleteIncomeById(int id) {
        incomeRepository.deleteById(id);
    }
}
