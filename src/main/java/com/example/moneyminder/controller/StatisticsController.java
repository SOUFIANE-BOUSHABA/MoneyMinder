package com.example.moneyminder.controller;

import com.example.moneyminder.VMs.StatisticsVM;
import com.example.moneyminder.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping
    public StatisticsVM getUserStatistics() {
        return statisticsService.getUserStatistics();
    }
}
