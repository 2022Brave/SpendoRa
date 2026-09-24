package com.spendora.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Locale

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val type: TransactionType,
    val amount: Long, // Minor units (paise). ₹579.00 = 57900L. Strictly no floating point in DB.
    val merchant: String,
    val category: String,
    val account: String,
    val dateTime: Long, // Milliseconds since epoch
    val note: String = "",
    val source: TransactionSource = TransactionSource.MANUAL,
    val confidence: Float = 1.0f,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    val amountRupees: Double
        get() = amount / 100.0

    val formattedAmount: String
        get() = if (amount % 100L == 0L) {
            "₹${java.text.NumberFormat.getNumberInstance(Locale.US).format(amount / 100)}"
        } else {
            "₹${String.format(Locale.US, "%.2f", amount / 100.0)}"
        }
}
