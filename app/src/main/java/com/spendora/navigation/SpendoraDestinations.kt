package com.spendora.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Navigation Destinations for SPENDO₹A
 * Exactly: Home | Transactions | + | Analytics | SplitMate
 */
enum class SpendoraDestination(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val isPrimaryAction: Boolean = false
) {
    HOME(
        route = "home",
        title = "Home",
        icon = Icons.Filled.Home
    ),
    TRANSACTIONS(
        route = "transactions",
        title = "Transactions",
        icon = Icons.AutoMirrored.Filled.ReceiptLong
    ),
    ADD_TRANSACTION(
        route = "add_transaction",
        title = "Add",
        icon = Icons.Filled.Add,
        isPrimaryAction = true
    ),
    ANALYTICS(
        route = "analytics",
        title = "Analytics",
        icon = Icons.Filled.BarChart
    ),
    SPLITMATE(
        route = "splitmate",
        title = "SplitMate",
        icon = Icons.Filled.Group
    )
}
