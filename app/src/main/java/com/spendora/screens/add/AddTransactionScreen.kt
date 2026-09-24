package com.spendora.screens.add

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CardTravel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.LocalGroceryStore
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spendora.data.model.TransactionType
import com.spendora.data.viewmodel.FinanceViewModel
import com.spendora.ui.components.SpendoraCard
import com.spendora.ui.components.SpendoraChip
import com.spendora.ui.theme.FinanceBlue
import com.spendora.ui.theme.FinanceGreen
import com.spendora.ui.theme.FinanceRed
import com.spendora.ui.theme.LavenderLight
import com.spendora.ui.theme.PurplePrimary
import com.spendora.ui.theme.SpendoraTheme
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private data class CategoryOption(val name: String, val icon: ImageVector)
private data class AccountOption(val name: String, val icon: ImageVector)

/**
 * SPENDO₹A Add Transaction Screen
 * Connected directly to Room database via FinanceViewModel.
 */
@Composable
fun AddTransactionScreen(
    viewModel: FinanceViewModel,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedType by remember { mutableStateOf(TransactionType.EXPENSE) }
    var amount by remember { mutableStateOf("") }
    var merchant by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Shopping") }
    var selectedAccount by remember { mutableStateOf("Bank Account") }
    val now = remember { System.currentTimeMillis() }
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.US) }
    var dateTime by remember { mutableStateOf(dateFormat.format(Date(now))) }
    var note by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val customColors = SpendoraTheme.customColors
    val scrollState = rememberScrollState()

    val expenseCategories = remember {
        listOf(
            CategoryOption("Shopping", Icons.Filled.ShoppingBag),
            CategoryOption("Dining", Icons.Filled.Fastfood),
            CategoryOption("Groceries", Icons.Filled.LocalGroceryStore),
            CategoryOption("Transport", Icons.Filled.DirectionsCar),
            CategoryOption("Fuel", Icons.Filled.LocalGasStation),
            CategoryOption("Bills", Icons.Filled.Receipt),
            CategoryOption("Utilities", Icons.Filled.Receipt),
            CategoryOption("Rent", Icons.Filled.Home),
            CategoryOption("Entertainment", Icons.Filled.Movie),
            CategoryOption("Health", Icons.Filled.LocalHospital),
            CategoryOption("Medicine", Icons.Filled.LocalHospital),
            CategoryOption("Education", Icons.Filled.School),
            CategoryOption("Travel", Icons.Filled.CardTravel),
            CategoryOption("Subscriptions", Icons.Filled.Subscriptions),
            CategoryOption("Personal Care", Icons.Filled.FitnessCenter),
            CategoryOption("Insurance", Icons.Filled.Security),
            CategoryOption("EMI", Icons.Filled.CreditCard),
            CategoryOption("Investment", Icons.Filled.TrendingUp),
            CategoryOption("Cash Withdrawal", Icons.Filled.Payments),
            CategoryOption("Other", Icons.Filled.Payments)
        )
    }

    val incomeCategories = remember {
        listOf(
            CategoryOption("Salary", Icons.Filled.Work),
            CategoryOption("Freelance", Icons.Filled.Work),
            CategoryOption("Business", Icons.Filled.TrendingUp),
            CategoryOption("Interest", Icons.Filled.AccountBalance),
            CategoryOption("Refund", Icons.Filled.Payments),
            CategoryOption("Other Income", Icons.Filled.Payments)
        )
    }

    val transferCategories = remember {
        listOf(
            CategoryOption("Account Transfer", Icons.Filled.AccountBalance),
            CategoryOption("Credit Card Payment", Icons.Filled.CreditCard),
            CategoryOption("ATM Withdrawal", Icons.Filled.Payments)
        )
    }

    val currentCategories = when (selectedType) {
        TransactionType.EXPENSE, TransactionType.CASH_WITHDRAWAL -> expenseCategories
        TransactionType.INCOME, TransactionType.REFUND -> incomeCategories
        TransactionType.TRANSFER -> transferCategories
    }

    val accounts = remember {
        listOf(
            AccountOption("Bank Account", Icons.Filled.AccountBalance),
            AccountOption("UPI / Wallet", Icons.Filled.AccountBalanceWallet),
            AccountOption("Credit Card", Icons.Filled.CreditCard),
            AccountOption("Cash", Icons.Filled.Payments),
            AccountOption("Other", Icons.Filled.Payments)
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("add_transaction_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "New Transaction",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Record expense, income or transfer",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("add_transaction_close_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 1. Transaction Type Selector Tabs: EXPENSE | INCOME | TRANSFER
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(customColors.surfaceHighlight)
                    .border(1.dp, customColors.border, RoundedCornerShape(14.dp))
                    .padding(4.dp)
                    .testTag("transaction_type_selector"),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf(TransactionType.EXPENSE, TransactionType.INCOME, TransactionType.TRANSFER).forEach { type ->
                    val isSelected = selectedType == type
                    val tabColor = when (type) {
                        TransactionType.EXPENSE, TransactionType.CASH_WITHDRAWAL -> FinanceRed
                        TransactionType.INCOME, TransactionType.REFUND -> FinanceGreen
                        TransactionType.TRANSFER -> FinanceBlue
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) tabColor.copy(alpha = 0.2f) else Color.Transparent)
                            .border(
                                1.dp,
                                if (isSelected) tabColor else Color.Transparent,
                                RoundedCornerShape(10.dp)
                            )
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = ripple(color = tabColor.copy(alpha = 0.3f)),
                                onClick = {
                                    selectedType = type
                                    selectedCategory = when (type) {
                                        TransactionType.EXPENSE -> "Shopping"
                                        TransactionType.INCOME -> "Salary"
                                        TransactionType.TRANSFER -> "Account Transfer"
                                        else -> "Other"
                                    }
                                }
                            )
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = type.label,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            ),
                            color = if (isSelected) tabColor else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 2. Amount Field with Prominent ₹ Currency Prefix
            SpendoraCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_amount_input"),
                borderGlow = true
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "AMOUNT",
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 1.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "₹",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = LavenderLight
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedTextField(
                            value = amount,
                            onValueChange = { amount = it },
                            placeholder = {
                                Text(
                                    text = "0.00",
                                    style = MaterialTheme.typography.headlineLarge.copy(
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = customColors.textMuted
                                )
                            },
                            textStyle = MaterialTheme.typography.headlineLarge.copy(
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                cursorColor = PurplePrimary
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_amount")
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Merchant / Payee
            OutlinedTextField(
                value = merchant,
                onValueChange = { merchant = it },
                label = { Text("Merchant / Payee") },
                placeholder = { Text("e.g. Swiggy, Amazon, Landlord") },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PurplePrimary,
                    unfocusedBorderColor = customColors.border,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_merchant")
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 4. Category Selector
            Text(
                text = "CATEGORY",
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 0.8.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("category_selector")
            ) {
                items(currentCategories) { cat ->
                    SpendoraChip(
                        text = cat.name,
                        icon = cat.icon,
                        selected = selectedCategory == cat.name,
                        onClick = { selectedCategory = cat.name },
                        testTag = "category_${cat.name.lowercase().replace(" ", "_")}"
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 5. Account Selector
            Text(
                text = "ACCOUNT",
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 0.8.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("account_selector")
            ) {
                items(accounts) { acc ->
                    SpendoraChip(
                        text = acc.name,
                        icon = acc.icon,
                        selected = selectedAccount == acc.name,
                        onClick = { selectedAccount = acc.name },
                        testTag = "account_${acc.name.lowercase().replace(" ", "_").replace("/", "")}"
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 6. Date / Time Field
            OutlinedTextField(
                value = dateTime,
                onValueChange = { dateTime = it },
                label = { Text("Date & Time") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.CalendarToday,
                        contentDescription = null,
                        tint = LavenderLight,
                        modifier = Modifier.size(18.dp)
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PurplePrimary,
                    unfocusedBorderColor = customColors.border,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_datetime")
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 7. Note Field
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Note / Description (Optional)") },
                placeholder = { Text("Add transaction context...") },
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PurplePrimary,
                    unfocusedBorderColor = customColors.border,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_note")
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 8. Save Transaction Button — Real Room Persistence
            Button(
                onClick = {
                    if (isSaving) return@Button
                    isSaving = true
                    viewModel.addTransaction(
                        type = selectedType,
                        amountInput = amount,
                        merchant = merchant.ifBlank { selectedCategory },
                        category = selectedCategory,
                        account = selectedAccount,
                        dateTimeMillis = System.currentTimeMillis(),
                        note = note,
                        onSuccess = {
                            isSaving = false
                            onDismiss()
                        },
                        onError = { errorMsg ->
                            isSaving = false
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(errorMsg)
                            }
                        }
                    )
                },
                enabled = !isSaving,
                colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("save_transaction_button")
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Save Transaction",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp)
        )
    }
}
