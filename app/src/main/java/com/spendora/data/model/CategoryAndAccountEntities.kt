package com.spendora.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    val isExpense: Boolean,
    val iconName: String = "",
    val colorHex: String = ""
)

@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    val type: String
)

object DefaultFinancialData {
    val EXPENSE_CATEGORIES = listOf(
        "Food",
        "Groceries",
        "Dining",
        "Transport",
        "Fuel",
        "Shopping",
        "Bills",
        "Rent",
        "Utilities",
        "Entertainment",
        "Health",
        "Medicine",
        "Education",
        "Travel",
        "Subscriptions",
        "Personal Care",
        "Insurance",
        "EMI",
        "Investment",
        "Cash Withdrawal",
        "Other"
    )

    val INCOME_CATEGORIES = listOf(
        "Salary",
        "Freelance",
        "Business",
        "Interest",
        "Refund",
        "Other Income"
    )

    val ACCOUNTS = listOf(
        "Bank Account" to "BANK",
        "Credit Card" to "CREDIT_CARD",
        "Cash" to "CASH",
        "UPI / Wallet" to "UPI_WALLET",
        "Other" to "OTHER"
    )
}
