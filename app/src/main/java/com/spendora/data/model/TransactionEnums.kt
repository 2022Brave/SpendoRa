package com.spendora.data.model

enum class TransactionType(val label: String) {
    EXPENSE("EXPENSE"),
    INCOME("INCOME"),
    TRANSFER("TRANSFER"),
    REFUND("REFUND"),
    CASH_WITHDRAWAL("CASH_WITHDRAWAL")
}

enum class TransactionSource {
    MANUAL,
    SMS
}
