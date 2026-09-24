package com.spendora.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CallMade
import androidx.compose.material.icons.filled.CallReceived
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.SyncAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spendora.data.cycle.CycleDateRange
import com.spendora.data.model.TransactionEntity
import com.spendora.data.model.TransactionType
import com.spendora.data.repository.FinancialSummary
import com.spendora.data.viewmodel.FinanceViewModel
import com.spendora.ui.components.SpendoraCard
import com.spendora.ui.components.SpendoraEmptyState
import com.spendora.ui.theme.FinanceBlue
import com.spendora.ui.theme.FinanceGreen
import com.spendora.ui.theme.FinanceRed
import com.spendora.ui.theme.LavenderLight
import com.spendora.ui.theme.PurplePrimary
import com.spendora.ui.theme.SpendoraTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * SPENDO₹A Home Screen
 * Connected directly to Room database via FinanceViewModel.
 * Dynamic calculations for Total Spent, Income, Expenses, Net Flow,
 * and categories. Empty states automatically disappear when transactions exist.
 */
@Composable
fun HomeScreen(
    viewModel: FinanceViewModel,
    onNavigateToAdd: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cycleSummary by viewModel.cycleSummary.collectAsState()
    val allTransactions by viewModel.allTransactions.collectAsState()
    val currentCycle = viewModel.currentCycle

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Reporting Cycle Card
        item {
            ReportingCycleCard(currentCycle)
        }

        // 2. Hero Total Spent Banner
        item {
            HeroTotalSpentCard(cycleSummary)
        }

        // 3. Financial Metrics Row: Income, Expenses, Net Cash Flow
        item {
            FinancialMetricsRow(cycleSummary)
        }

        // 4. Compact Calendar Day Strip
        item {
            CompactCalendarStrip()
        }

        // 5. Spending by Category (Real Room aggregation)
        item {
            CategorySpendingOverview(cycleSummary)
        }

        // 6. Recent Activity (Real Room persistence)
        item {
            RecentActivitySection(
                transactions = allTransactions,
                onNavigateToAdd = onNavigateToAdd
            )
        }
    }
}

@Composable
private fun ReportingCycleCard(currentCycle: CycleDateRange) {
    val customColors = SpendoraTheme.customColors
    SpendoraCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("card_reporting_cycle")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(PurplePrimary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.CalendarToday,
                        contentDescription = null,
                        tint = LavenderLight,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "REPORTING CYCLE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 1.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = LavenderLight
                    )
                    Text(
                        text = currentCycle.label,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(customColors.surfaceHighlight)
                    .border(1.dp, customColors.border, RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Active",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = FinanceGreen
                )
            }
        }
    }
}

@Composable
private fun HeroTotalSpentCard(summary: FinancialSummary) {
    val customColors = SpendoraTheme.customColors
    SpendoraCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("card_total_spent"),
        shapeRadius = 22.dp,
        borderGlow = true
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            PurplePrimary.copy(alpha = 0.14f),
                            Color.Transparent
                        ),
                        radius = 450f
                    )
                )
                .padding(20.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TOTAL SPENT",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "This Cycle",
                        style = MaterialTheme.typography.labelSmall,
                        color = customColors.textMuted
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = summary.formattedTotalSpent,
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Black
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(14.dp))

                LinearProgressIndicator(
                    progress = { 0f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = PurplePrimary,
                    trackColor = customColors.surfaceHighlight
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Cycle Budget: ₹0",
                        style = MaterialTheme.typography.labelSmall,
                        color = customColors.textMuted
                    )
                    Text(
                        text = "0% utilized",
                        style = MaterialTheme.typography.labelSmall,
                        color = customColors.textMuted
                    )
                }
            }
        }
    }
}

@Composable
private fun FinancialMetricsRow(summary: FinancialSummary) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Income
        FinancialMetricCard(
            title = "Income",
            value = summary.formattedIncome,
            icon = Icons.Filled.CallReceived,
            accentColor = FinanceGreen,
            modifier = Modifier.weight(1f),
            testTag = "card_metric_income"
        )

        // Expenses
        FinancialMetricCard(
            title = "Expenses",
            value = summary.formattedExpenses,
            icon = Icons.Filled.CallMade,
            accentColor = FinanceRed,
            modifier = Modifier.weight(1f),
            testTag = "card_metric_expenses"
        )

        // Net Cash Flow
        FinancialMetricCard(
            title = "Net Flow",
            value = summary.formattedNetCashFlow,
            icon = Icons.Filled.SyncAlt,
            accentColor = if (summary.netCashFlowPaise < 0) FinanceRed else FinanceBlue,
            modifier = Modifier.weight(1f),
            testTag = "card_metric_net_flow"
        )
    }
}

@Composable
private fun FinancialMetricCard(
    title: String,
    value: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
    testTag: String = ""
) {
    SpendoraCard(
        modifier = modifier.testTag(testTag),
        shapeRadius = 16.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(14.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                ),
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
private fun CompactCalendarStrip() {
    val customColors = SpendoraTheme.customColors
    val calendar = Calendar.getInstance()
    val currentDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
    val monthFormat = SimpleDateFormat("MMMM", Locale.US)
    val currentMonthName = monthFormat.format(calendar.time)

    // Window of 7 days centered around today
    val dayNames = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    val days = remember {
        val list = mutableListOf<Pair<String, String>>()
        val tempCal = Calendar.getInstance()
        tempCal.add(Calendar.DAY_OF_MONTH, -3)
        for (i in 0..6) {
            val dName = dayNames[tempCal.get(Calendar.DAY_OF_WEEK) - 1]
            val dNum = tempCal.get(Calendar.DAY_OF_MONTH).toString()
            list.add(dName to dNum)
            tempCal.add(Calendar.DAY_OF_MONTH, 1)
        }
        list
    }

    val todayNum = currentDayOfMonth.toString()

    SpendoraCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("card_calendar_strip")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "CYCLE CALENDAR",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 0.8.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = currentMonthName,
                    style = MaterialTheme.typography.labelSmall,
                    color = customColors.textMuted
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                items(days) { (dayName, dayNum) ->
                    val isToday = dayNum == todayNum
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isToday) PurplePrimary else customColors.surfaceHighlight
                            )
                            .border(
                                1.dp,
                                if (isToday) LavenderLight else customColors.border,
                                RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 11.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = dayName,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = if (isToday) FontWeight.Bold else FontWeight.Medium
                                ),
                                color = if (isToday) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = dayNum,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = if (isToday) Color.White else MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CategorySpendingOverview(summary: FinancialSummary) {
    val customColors = SpendoraTheme.customColors
    val categoryList = if (summary.categoryBreakdown.isNotEmpty()) {
        summary.categoryBreakdown.entries.map { entry ->
            Triple(
                entry.key,
                getCategoryIcon(entry.key),
                FinancialSummary.formatPaise(entry.value)
            )
        }
    } else {
        listOf(
            Triple("Shopping", Icons.Filled.ShoppingBag, "₹0"),
            Triple("Dining", Icons.Filled.Fastfood, "₹0"),
            Triple("Utilities", Icons.Filled.Receipt, "₹0"),
            Triple("Fuel", Icons.Filled.LocalGasStation, "₹0")
        )
    }

    SpendoraCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("card_category_spending")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SPENDING BY CATEGORY",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 0.8.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${summary.formattedTotalSpent} Total",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = customColors.textMuted
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                categoryList.forEach { (name, icon, formattedVal) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(PurplePrimary.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = LavenderLight,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = name,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = formattedVal,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RecentActivitySection(
    transactions: List<TransactionEntity>,
    onNavigateToAdd: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("dd MMM, hh:mm a", Locale.US) }

    SpendoraCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("card_recent_activity")
    ) {
        if (transactions.isEmpty()) {
            SpendoraEmptyState(
                icon = Icons.AutoMirrored.Filled.ReceiptLong,
                title = "No transactions yet",
                description = "Your spending insights will appear here.",
                actionButton = {
                    Button(
                        onClick = onNavigateToAdd,
                        colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("home_add_first_transaction")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Add Transaction",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                    }
                }
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "RECENT ACTIVITY",
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 0.8.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${transactions.size} Total",
                        style = MaterialTheme.typography.labelSmall,
                        color = LavenderLight
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    transactions.take(5).forEach { tx ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val icon = getCategoryIcon(tx.category)
                            val isIncome = tx.type == TransactionType.INCOME || tx.type == TransactionType.REFUND
                            val isTransfer = tx.type == TransactionType.TRANSFER
                            val color = when {
                                isIncome -> FinanceGreen
                                isTransfer -> FinanceBlue
                                else -> FinanceRed
                            }

                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(color.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = color,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = tx.merchant.ifBlank { tx.category },
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Text(
                                    text = "${tx.category} • ${tx.account} • ${dateFormat.format(Date(tx.dateTime))}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            val prefix = when {
                                isIncome -> "+"
                                isTransfer -> ""
                                else -> "-"
                            }
                            Text(
                                text = "$prefix${tx.formattedAmount}",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = color
                            )
                        }
                    }
                }
            }
        }
    }
}

fun getCategoryIcon(category: String): ImageVector {
    return when (category.lowercase()) {
        "shopping" -> Icons.Filled.ShoppingBag
        "dining", "food" -> Icons.Filled.Fastfood
        "groceries" -> Icons.Filled.ShoppingBag
        "fuel", "transport" -> Icons.Filled.LocalGasStation
        "bills", "utilities" -> Icons.Filled.Receipt
        "salary", "freelance", "business" -> Icons.Filled.Payments
        "transfer", "account transfer" -> Icons.Filled.SyncAlt
        else -> Icons.Filled.Payments
    }
}
