package com.spendora.data.repository

import com.spendora.data.model.TransactionEntity
import com.spendora.data.model.TransactionType
import java.util.Locale

data class FinancialSummary(
    val totalSpentPaise: Long = 0L,     // Ordinary expenses excluding investments & transfers
    val incomePaise: Long = 0L,         // Total income
    val expensesPaise: Long = 0L,       // Total expenses (ordinary expenses + cash withdrawal)
    val investmentsPaise: Long = 0L,    // Investment transactions
    val refundsPaise: Long = 0L,        // Refund transactions
    val transfersPaise: Long = 0L,      // Transfer transactions
    val netCashFlowPaise: Long = 0L,    // income - (totalSpent + investments) + refunds
    val averageDailySpendPaise: Long = 0L,
    val categoryBreakdown: Map<String, Long> = emptyMap(),
    val accountBreakdown: Map<String, Long> = emptyMap(),
    val dailySpending: Map<String, Long> = emptyMap()
) {
    val totalSpentRupees: Double get() = totalSpentPaise / 100.0
    val incomeRupees: Double get() = incomePaise / 100.0
    val expensesRupees: Double get() = expensesPaise / 100.0
    val investmentsRupees: Double get() = investmentsPaise / 100.0
    val refundsRupees: Double get() = refundsPaise / 100.0
    val transfersRupees: Double get() = transfersPaise / 100.0
    val netCashFlowRupees: Double get() = netCashFlowPaise / 100.0
    val averageDailySpendRupees: Double get() = averageDailySpendPaise / 100.0

    val formattedTotalSpent: String get() = formatPaise(totalSpentPaise)
    val formattedIncome: String get() = formatPaise(incomePaise)
    val formattedExpenses: String get() = formatPaise(expensesPaise)
    val formattedInvestments: String get() = formatPaise(investmentsPaise)
    val formattedRefunds: String get() = formatPaise(refundsPaise)
    val formattedTransfers: String get() = formatPaise(transfersPaise)
    val formattedNetCashFlow: String get() = if (netCashFlowPaise < 0) {
        "-${formatPaise(-netCashFlowPaise)}"
    } else {
        formatPaise(netCashFlowPaise)
    }
    val formattedAverageDailySpend: String get() = formatPaise(averageDailySpendPaise)

    companion object {
        fun formatPaise(paise: Long): String {
            return if (paise % 100L == 0L) {
                "₹${java.text.NumberFormat.getNumberInstance(Locale.US).format(paise / 100)}"
            } else {
                "₹${String.format(Locale.US, "%,.2f", paise / 100.0)}"
            }
        }

        fun calculate(transactions: List<TransactionEntity>, daysActive: Int = 1): FinancialSummary {
            var income = 0L
            var ordinaryExpenses = 0L
            var investments = 0L
            var refunds = 0L
            var transfers = 0L
            val catMap = mutableMapOf<String, Long>()
            val accMap = mutableMapOf<String, Long>()
            val dailyMap = mutableMapOf<String, Long>()
            val dateFormat = java.text.SimpleDateFormat("dd MMM", Locale.US)

            for (tx in transactions) {
                val isCreditCardPayment = tx.category.equals("Credit Card Payment", ignoreCase = true) ||
                        tx.note.contains("Credit Card Payment", ignoreCase = true)
                val isInvestment = tx.category.equals("Investment", ignoreCase = true) ||
                        tx.category.equals("Mutual Funds", ignoreCase = true) ||
                        tx.category.equals("Stocks", ignoreCase = true)
                val isRefund = tx.type == TransactionType.REFUND ||
                        tx.category.equals("Refund", ignoreCase = true)

                when {
                    tx.type == TransactionType.TRANSFER || isCreditCardPayment -> {
                        // Transfers between own accounts do NOT count as ordinary spending
                        transfers += tx.amount
                        accMap[tx.account] = (accMap[tx.account] ?: 0L) + tx.amount
                    }
                    isRefund -> {
                        // Refunds handled separately
                        refunds += tx.amount
                        accMap[tx.account] = (accMap[tx.account] ?: 0L) + tx.amount
                    }
                    tx.type == TransactionType.INCOME -> {
                        income += tx.amount
                        accMap[tx.account] = (accMap[tx.account] ?: 0L) + tx.amount
                    }
                    isInvestment -> {
                        // Investments must not inflate ordinary spending analytics
                        investments += tx.amount
                        accMap[tx.account] = (accMap[tx.account] ?: 0L) + tx.amount
                    }
                    tx.type == TransactionType.EXPENSE || tx.type == TransactionType.CASH_WITHDRAWAL -> {
                        ordinaryExpenses += tx.amount
                        catMap[tx.category] = (catMap[tx.category] ?: 0L) + tx.amount
                        accMap[tx.account] = (accMap[tx.account] ?: 0L) + tx.amount
                        val dayKey = dateFormat.format(java.util.Date(tx.dateTime))
                        dailyMap[dayKey] = (dailyMap[dayKey] ?: 0L) + tx.amount
                    }
                }
            }

            val totalSpent = ordinaryExpenses
            val netCashFlow = income - (totalSpent + investments) + refunds
            val effectiveDays = daysActive.coerceAtLeast(1)
            val avgDaily = totalSpent / effectiveDays

            return FinancialSummary(
                totalSpentPaise = totalSpent,
                incomePaise = income,
                expensesPaise = ordinaryExpenses,
                investmentsPaise = investments,
                refundsPaise = refunds,
                transfersPaise = transfers,
                netCashFlowPaise = netCashFlow,
                averageDailySpendPaise = avgDaily,
                categoryBreakdown = catMap,
                accountBreakdown = accMap,
                dailySpending = dailyMap
            )
        }
    }
}
