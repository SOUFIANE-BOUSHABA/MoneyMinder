package com.example.moneyminder.service.impl;

import com.example.moneyminder.VMs.StatisticsVM;
import com.example.moneyminder.entity.User;
import com.example.moneyminder.repository.InvoiceRepository;
import com.example.moneyminder.repository.QuoteRepository;
import com.example.moneyminder.repository.TransactionRepository;
import com.example.moneyminder.repository.UserRepository;
import com.example.moneyminder.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final InvoiceRepository invoiceRepository;
    private final QuoteRepository quoteRepository;

    @Override
    public StatisticsVM getUserStatistics() {
        User currentUser = getCurrentUser();

        StatisticsVM statistics = new StatisticsVM();


        List<Object[]> incomeExpenseData = transactionRepository.sumByTypeForUser(currentUser.getId());
        Map<String, Double> incomeExpense = new HashMap<>();
        for (Object[] data : incomeExpenseData) {
            incomeExpense.put(data[0].toString(), (Double) data[1]);
        }
        statistics.setIncomeExpense(incomeExpense);


        List<Object[]> invoiceData = invoiceRepository.countInvoicesByStatusForUser(currentUser.getId());
        Map<String, Long> invoiceStatus = new HashMap<>();
        for (Object[] data : invoiceData) {
            invoiceStatus.put(data[0].toString(), (Long) data[1]);
        }
        statistics.setInvoiceStatus(invoiceStatus);


        List<Object[]> quoteData = quoteRepository.countQuotesByStatusForUser(currentUser.getId());
        Map<String, Long> quoteStatus = new HashMap<>();
        for (Object[] data : quoteData) {
            quoteStatus.put(data[0].toString(), (Long) data[1]);
        }
        statistics.setQuoteStatus(quoteStatus);


        List<Object[]> monthlyData = transactionRepository.monthlyTransactionSummaryForUser(currentUser.getId());
        Map<String, Map<String, Double>> monthlySummary = new HashMap<>();
        for (Object[] data : monthlyData) {
            String monthYear = data[1] + "-" + data[0];
            String type = data[2].toString();
            Double amount = (Double) data[3];

            monthlySummary.putIfAbsent(monthYear, new HashMap<>());
            monthlySummary.get(monthYear).put(type, amount);
        }
        statistics.setMonthlySummary(monthlySummary);

        return statistics;
    }

    private User getCurrentUser() {
        String email = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }
}
