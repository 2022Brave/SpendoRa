package com.spendora.screens.analytics

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CallMade
import androidx.compose.material.icons.filled.CallReceived
import androidx.compose.material.icons.filled.DonutSmall
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.SyncAlt
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spendora.data.repository.FinancialSummary
import com.spendora.data.viewmodel.FinanceViewModel
import com.spendora.screens.home.getCategoryIcon
import com.spendora.ui.components.SpendoraCard
import com.spendora.ui.components.SpendoraChip
import com.spendora.ui.components.SpendoraEmptyState
import com.spendora.ui.components.SpendoraMetricItem
import com.spendora.ui.theme.FinanceBlue
import com.spendora.ui.theme.FinanceGreen
import com.spendora.ui.theme.FinanceRed
import com.spendora.ui.theme.LavenderLight
import com.spendora.ui.theme.PurplePrimary
import com.spendora.ui.theme.SpendoraTheme

enum class AnalyticsPeriod(val label: String) {
    CURRENT_CYCLE("Current Cycle"),
    PREVIOUS_CYCLE("Previous Cycle"),
    LAST_7_DAYS("Last 7 Days"),
    LAST_30_DAYS("Last 30 Days"),
    LAST_3_MONTHS("Last 3 Months"),
    THIS_YEAR("This Year"),
    CUSTOM_RANGE("Custom Range")
}

/**
 * SPENDO₹A Analytics Screen
 * Connected directly to Room database via FinanceViewModel.
 * Dynamic financial telemetry and breakdown powered by real database records.
 */
@Composable
fun AnalyticsScreen(
    viewModel: FinanceViewModel,
    modifier: Modifier = Modifier
) {
    var selectedPeriod by remember { mutableStateOf(AnalyticsPeriod.CURRENT_CYCLE) }
    val cycleSummary by viewModel.cycleSummary.collectAsState()
    val allTransactions by viewModel.allTransactions.collectAsState()
    val hasData = allTransactions.isNotEmpty()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("analytics_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Header Title & Subtitle
        item {
            Column {
                Text(
                    text = "Analytics & Reports",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Financial breakdown and performance telemetry",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // 2. Period Selector Chips
        item {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("analytics_period_selector"),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(AnalyticsPeriod.values()) { period ->
                    SpendoraChip(
                        text = period.label,
                        selected = selectedPeriod == period,
                        onClick = { selectedPeriod = period },
                        testTag = "period_${period.name.lowercase()}"
                    )
                }
            }
        }

        // 3. Section Title: Metrics Overview
        item {
            Text(
                text = "FINANCIAL METRICS",
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = LavenderLight
            )
        }

        // 4. Seven Financial Metrics (Grid/Cards connected to Room)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SpendoraMetricItem(
                        title = "Income",
                        value = cycleSummary.formattedIncome,
                        icon = Icons.Filled.CallReceived,
                        accentColor = FinanceGreen,
                        modifier = Modifier.weight(1f),
                        subtitle = "Selected Period"
                    )
                    SpendoraMetricItem(
                        title = "Expenses",
                        value = cycleSummary.formattedExpenses,
                        icon = Icons.Filled.CallMade,
                        accentColor = FinanceRed,
                        modifier = Modifier.weight(1f),
                        subtitle = "Selected Period"
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SpendoraMetricItem(
                        title = "Investments",
                        value = cycleSummary.formattedInvestments,
                        icon = Icons.Filled.TrendingUp,
                        accentColor = PurplePrimary,
                        modifier = Modifier.weight(1f)
                    )
                    SpendoraMetricItem(
                        title = "Refunds",
                        value = cycleSummary.formattedRefunds,
                        icon = Icons.Filled.MonetizationOn,
                        accentColor = FinanceGreen,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SpendoraMetricItem(
                        title = "Transfers",
                        value = cycleSummary.formattedTransfers,
                        icon = Icons.Filled.SyncAlt,
                        accentColor = FinanceBlue,
                        modifier = Modifier.weight(1f)
                    )
                    SpendoraMetricItem(
                        title = "Net Cash Flow",
                        value = cycleSummary.formattedNetCashFlow,
                        icon = Icons.Filled.Savings,
                        accentColor = if (cycleSummary.netCashFlowPaise < 0) FinanceRed else LavenderLight,
                        modifier = Modifier.weight(1f)
                    )
                }

                SpendoraMetricItem(
                    title = "Average Daily Spend",
                    value = cycleSummary.formattedAverageDailySpend,
                    icon = Icons.Filled.BarChart,
                    accentColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth(),
                    subtitle = "${cycleSummary.formattedAverageDailySpend} / day based on active days"
                )
            }
        }

        // 5. Section Title: Visual Analytics Charts
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "VISUAL INTELLIGENCE",
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = LavenderLight
            )
        }

        // Chart Card 1: Spending Trend
        item {
            AnalyticsChartCard(
                title = "Spending Trend",
                subtitle = "Velocity of daily outflow over selected cycle",
                icon = Icons.Filled.Timeline,
                hasData = hasData && cycleSummary.dailySpending.isNotEmpty(),
                emptyDescription = "No trend data recorded for this cycle.",
                testTag = "chart_spending_trend"
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    cycleSummary.dailySpending.forEach { (day, amount) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = day, style = MaterialTheme.typography.bodySmall)
                            Text(
                                text = FinancialSummary.formatPaise(amount),
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = FinanceRed
                            )
                        }
                    }
                }
            }
        }

        // Chart Card 2: Income vs Expense
        item {
            AnalyticsChartCard(
                title = "Income vs Expense",
                subtitle = "Comparison of inflow volume against expenditure",
                icon = Icons.Filled.BarChart,
                hasData = hasData && (cycleSummary.incomePaise > 0 || cycleSummary.expensesPaise > 0),
                emptyDescription = "Zero inflow or outflow detected in this window.",
                testTag = "chart_income_vs_expense"
            ) {
                val totalVolume = (cycleSummary.incomePaise + cycleSummary.expensesPaise).coerceAtLeast(1L)
                val incomeRatio = cycleSummary.incomePaise.toFloat() / totalVolume
                val expenseRatio = cycleSummary.expensesPaise.toFloat() / totalVolume

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Income: ${cycleSummary.formattedIncome}", color = FinanceGreen, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                        Text(text = "Expenses: ${cycleSummary.formattedExpenses}", color = FinanceRed, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                    }
                    LinearProgressIndicator(
                        progress = { expenseRatio },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = FinanceRed,
                        trackColor = FinanceGreen
                    )
                }
            }
        }

        // Chart Card 3: Expense by Category
        item {
            AnalyticsChartCard(
                title = "Expense by Category",
                subtitle = "Visual breakdown of top spending categories",
                icon = Icons.Filled.PieChart,
                hasData = hasData && cycleSummary.categoryBreakdown.isNotEmpty(),
                emptyDescription = "Category distribution will appear once expenses are tagged.",
                testTag = "chart_expense_by_category"
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val maxCat = (cycleSummary.categoryBreakdown.values.maxOrNull() ?: 1L).coerceAtLeast(1L)
                    cycleSummary.categoryBreakdown.forEach { (cat, amount) ->
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = getCategoryIcon(cat),
                                        contentDescription = null,
                                        tint = LavenderLight,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.size(6.dp))
                                    Text(text = cat, style = MaterialTheme.typography.bodySmall)
                                }
                                Text(
                                    text = FinancialSummary.formatPaise(amount),
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { amount.toFloat() / maxCat },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp)),
                                color = PurplePrimary,
                                trackColor = SpendoraTheme.customColors.surfaceHighlight
                            )
                        }
                    }
                }
            }
        }

        // Chart Card 4: Category Breakdown
        item {
            AnalyticsChartCard(
                title = "Category Breakdown",
                subtitle = "Sub-category allocation and percentage share",
                icon = Icons.Filled.DonutSmall,
                hasData = hasData && cycleSummary.categoryBreakdown.isNotEmpty(),
                emptyDescription = "No classified transactions available for breakdown.",
                testTag = "chart_category_breakdown"
            ) {
                val totalSpent = cycleSummary.totalSpentPaise.coerceAtLeast(1L)
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    cycleSummary.categoryBreakdown.forEach { (cat, amount) ->
                        val share = (amount * 100) / totalSpent
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = cat, style = MaterialTheme.typography.bodySmall)
                            Text(
                                text = "$share% (${FinancialSummary.formatPaise(amount)})",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
                            )
                        }
                    }
                }
            }
        }

        // Chart Card 5: Account Breakdown
        item {
            AnalyticsChartCard(
                title = "Account Breakdown",
                subtitle = "Balances and volume handled per financial account",
                icon = Icons.Filled.AccountBalance,
                hasData = hasData && cycleSummary.accountBreakdown.isNotEmpty(),
                emptyDescription = "No registered accounts or bank activity recorded.",
                testTag = "chart_account_breakdown"
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    cycleSummary.accountBreakdown.forEach { (acc, amount) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = acc, style = MaterialTheme.typography.bodySmall)
                            Text(
                                text = FinancialSummary.formatPaise(amount),
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = LavenderLight
                            )
                        }
                    }
                }
            }
        }

        // Chart Card 6: Daily Spending
        item {
            AnalyticsChartCard(
                title = "Daily Spending",
                subtitle = "Histogram of transaction concentrations by day",
                icon = Icons.Filled.ShowChart,
                hasData = hasData && cycleSummary.dailySpending.isNotEmpty(),
                emptyDescription = "Daily distribution activates when expenses are recorded.",
                testTag = "chart_daily_spending"
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    cycleSummary.dailySpending.forEach { (day, amount) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = day, style = MaterialTheme.typography.bodySmall)
                            Text(
                                text = FinancialSummary.formatPaise(amount),
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }
        }

        // Chart Card 7: Investment Summary
        item {
            AnalyticsChartCard(
                title = "Investment Summary",
                subtitle = "Portfolio allocations and recurring wealth transfers",
                icon = Icons.Filled.TrendingUp,
                hasData = hasData && cycleSummary.investmentsPaise > 0,
                emptyDescription = "Zero investments registered for this reporting cycle.",
                testTag = "chart_investment_summary"
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Total Invested", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        text = cycleSummary.formattedInvestments,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = PurplePrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun AnalyticsChartCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    hasData: Boolean,
    emptyDescription: String,
    testTag: String,
    content: @Composable () -> Unit = {}
) {
    SpendoraCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(PurplePrimary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = LavenderLight,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.size(10.dp))
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (!hasData) {
                SpendoraEmptyState(
                    icon = icon,
                    title = "No chart data yet",
                    description = emptyDescription,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            } else {
                content()
            }
        }
    }
}
