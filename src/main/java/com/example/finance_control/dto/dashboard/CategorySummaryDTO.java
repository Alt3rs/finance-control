package com.example.finance_control.dto.dashboard;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class CategorySummaryDTO {
    private final String categoryCode;
    private final String categoryName;
    private final String emoji;
    private final String color;
    private final Double totalValue;
    private final Double percentage;
    private final Integer transactionCount;
    private final Double averageTransactionValue;
    private final List<RecentTransaction> recentTransactions;

    @Getter
    @Builder
    public static class RecentTransaction {
        private final String id;
        private final String description;
        private final Double value;
        private final String date;
    }
}