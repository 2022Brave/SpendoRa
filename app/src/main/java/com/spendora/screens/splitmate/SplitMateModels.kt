package com.spendora.screens.splitmate

enum class SplitSyncStatus(val label: String) {
    SYNCED("Synced"),
    SYNC_PENDING("Sync Pending"),
    SYNC_FAILED("Sync Failed")
}

data class SplitMateExpense(
    val id: String,
    val title: String,
    val amount: Double,
    val paidBy: String,
    val splitWith: String,
    val category: String,
    val date: String,
    val syncStatus: SplitSyncStatus
)

data class SplitSheetConnection(
    val sheetId: String,
    val sheetName: String,
    val sheetUrl: String,
    val spaceName: String,
    val joinCode: String,
    val connectedAccount: String,
    val lastSyncedTime: String,
    val syncStatus: SplitSyncStatus = SplitSyncStatus.SYNCED
)

data class SplitMateState(
    val connection: SplitSheetConnection? = null,
    val expenses: List<SplitMateExpense> = emptyList(),
    val isSyncing: Boolean = false,
    val isOfflineMode: Boolean = false,
    val showCreateDialog: Boolean = false,
    val showConnectDialog: Boolean = false,
    val showJoinDialog: Boolean = false,
    val showManageDialog: Boolean = false,
    val showAddExpenseDialog: Boolean = false,
    val syncErrorMessage: String? = null
) {
    val totalShared: Double
        get() = expenses.sumOf { it.amount }

    val youSpent: Double
        get() = expenses.filter { it.paidBy.equals("You", ignoreCase = true) }.sumOf { it.amount }

    val roommateSpent: Double
        get() = expenses.filter { !it.paidBy.equals("You", ignoreCase = true) }.sumOf { it.amount }

    // Settlement calculation (Half share standard)
    val settlementAmount: Double
        get() {
            if (expenses.isEmpty()) return 0.0
            val yourShare = totalShared / 2.0
            return youSpent - yourShare // positive means roommate owes you; negative means you owe roommate
        }
}
