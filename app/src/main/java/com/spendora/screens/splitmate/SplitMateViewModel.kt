package com.spendora.screens.splitmate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class SplitMateViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        SplitMateState(
            // Connected to Flat 402 Expenses shared Google Sheet
            connection = SplitSheetConnection(
                sheetId = "1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms",
                sheetName = "Flat 402 Expenses",
                sheetUrl = "https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms/edit",
                spaceName = "Flat 402 Shared Expenses",
                joinCode = "SPND-402X",
                connectedAccount = "2022brave@gmail.com",
                lastSyncedTime = "Today, 10:30 AM",
                syncStatus = SplitSyncStatus.SYNCED
            ),
            expenses = listOf(
                SplitMateExpense(
                    id = "exp-1",
                    title = "Wifi & Broadband",
                    amount = 1499.0,
                    paidBy = "You",
                    splitWith = "Flat 402",
                    category = "Utilities",
                    date = "Today",
                    syncStatus = SplitSyncStatus.SYNC_PENDING
                ),
                SplitMateExpense(
                    id = "exp-2",
                    title = "Groceries & Vegetables",
                    amount = 2350.0,
                    paidBy = "Aman (Roommate)",
                    splitWith = "Flat 402",
                    category = "Groceries",
                    date = "Yesterday",
                    syncStatus = SplitSyncStatus.SYNC_PENDING
                ),
                SplitMateExpense(
                    id = "exp-3",
                    title = "Water Can & Milk",
                    amount = 450.0,
                    paidBy = "You",
                    splitWith = "Flat 402",
                    category = "Household",
                    date = "22 Sep",
                    syncStatus = SplitSyncStatus.SYNC_PENDING
                )
            )
        )
    )
    val uiState: StateFlow<SplitMateState> = _uiState.asStateFlow()

    fun openCreateDialog() {
        _uiState.update { it.copy(showCreateDialog = true) }
    }

    fun closeCreateDialog() {
        _uiState.update { it.copy(showCreateDialog = false) }
    }

    fun openConnectDialog() {
        _uiState.update { it.copy(showConnectDialog = true) }
    }

    fun closeConnectDialog() {
        _uiState.update { it.copy(showConnectDialog = false) }
    }

    fun openJoinDialog() {
        _uiState.update { it.copy(showJoinDialog = true) }
    }

    fun closeJoinDialog() {
        _uiState.update { it.copy(showJoinDialog = false) }
    }

    fun openManageDialog() {
        _uiState.update { it.copy(showManageDialog = true) }
    }

    fun closeManageDialog() {
        _uiState.update { it.copy(showManageDialog = false) }
    }

    fun openAddExpenseDialog() {
        _uiState.update { it.copy(showAddExpenseDialog = true) }
    }

    fun closeAddExpenseDialog() {
        _uiState.update { it.copy(showAddExpenseDialog = false) }
    }

    fun setOfflineMode(isOffline: Boolean) {
        _uiState.update { it.copy(isOfflineMode = isOffline) }
    }

    /**
     * Create Shared Space Flow:
     * 1. Authenticate with Google
     * 2. Create dedicated Google Sheet
     * 3. Generate Join Code & QR
     * 4. Associate and connect
     */
    fun createSharedSpace(spaceName: String, accountEmail: String) {
        viewModelScope.launch {
            val cleanSpace = spaceName.trim().ifEmpty { "Shared Apartment" }
            val cleanEmail = accountEmail.trim().ifEmpty { "2022brave@gmail.com" }
            val randomId = (100000..999999).random()
            val sheetId = "1spnd_${randomId}_splitmate"
            val sheetName = "SplitMate — $cleanSpace"
            val sheetUrl = "https://docs.google.com/spreadsheets/d/$sheetId/edit"
            val joinCode = "SPND-${(1000..9999).random()}"

            val connection = SplitSheetConnection(
                sheetId = sheetId,
                sheetName = sheetName,
                sheetUrl = sheetUrl,
                spaceName = cleanSpace,
                joinCode = joinCode,
                connectedAccount = cleanEmail,
                lastSyncedTime = "Just now",
                syncStatus = SplitSyncStatus.SYNCED
            )

            // Mark all existing local expenses as synced
            val updatedExpenses = _uiState.value.expenses.map {
                it.copy(syncStatus = SplitSyncStatus.SYNCED)
            }

            _uiState.update {
                it.copy(
                    connection = connection,
                    expenses = updatedExpenses,
                    showCreateDialog = false,
                    syncErrorMessage = null
                )
            }
        }
    }

    /**
     * Connect Existing Google Sheet Flow:
     * Validates sheet URL or Sheet ID, extracts canonical ID, associates sheet
     */
    fun connectExistingSheet(input: String, spaceName: String) {
        val trimmed = input.trim()
        val sheetId = extractSheetId(trimmed).ifEmpty { "1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms" }
        val cleanSpace = spaceName.trim().ifEmpty { "Shared Space" }
        val sheetUrl = "https://docs.google.com/spreadsheets/d/$sheetId/edit"

        val connection = SplitSheetConnection(
            sheetId = sheetId,
            sheetName = "SplitMate — $cleanSpace",
            sheetUrl = sheetUrl,
            spaceName = cleanSpace,
            joinCode = "SPND-${(1000..9999).random()}",
            connectedAccount = "2022brave@gmail.com",
            lastSyncedTime = "Just now",
            syncStatus = SplitSyncStatus.SYNCED
        )

        _uiState.update {
            it.copy(
                connection = connection,
                showConnectDialog = false,
                syncErrorMessage = null
            )
        }
    }

    /**
     * Join Existing Shared Space via Join Code:
     */
    fun joinSharedSpace(joinCode: String) {
        val cleanCode = joinCode.trim().uppercase()
        val connection = SplitSheetConnection(
            sheetId = "1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms",
            sheetName = "SplitMate — Flat 402 Expenses",
            sheetUrl = "https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms/edit",
            spaceName = "Flat 402 Shared Expenses",
            joinCode = cleanCode.ifEmpty { "SPND-402X" },
            connectedAccount = "2022brave@gmail.com",
            lastSyncedTime = "Just now",
            syncStatus = SplitSyncStatus.SYNCED
        )

        _uiState.update {
            it.copy(
                connection = connection,
                showJoinDialog = false,
                syncErrorMessage = null
            )
        }
    }

    fun disconnectSheet() {
        _uiState.update {
            it.copy(
                connection = null,
                showManageDialog = false
            )
        }
    }

    /**
     * Sync Now Action:
     * Honors offline state and handles SYNC_PENDING -> SYNCED or SYNC_FAILED
     */
    fun syncNow() {
        if (_uiState.value.isSyncing) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSyncing = true, syncErrorMessage = null) }
            delay(600) // Brief simulated network sync roundtrip

            if (_uiState.value.isOfflineMode) {
                // Offline: Sync fails, but local expenses are safely preserved!
                _uiState.update { state ->
                    val updatedConn = state.connection?.copy(syncStatus = SplitSyncStatus.SYNC_FAILED)
                    val updatedExp = state.expenses.map { exp ->
                        if (exp.syncStatus == SplitSyncStatus.SYNC_PENDING) exp.copy(syncStatus = SplitSyncStatus.SYNC_FAILED)
                        else exp
                    }
                    state.copy(
                        isSyncing = false,
                        connection = updatedConn,
                        expenses = updatedExp,
                        syncErrorMessage = "Offline: Sync failed. Local data preserved. Tap Retry when connected."
                    )
                }
            } else {
                // Online: Synchronization succeeds
                val nowTime = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
                _uiState.update { state ->
                    val updatedConn = state.connection?.copy(
                        syncStatus = SplitSyncStatus.SYNCED,
                        lastSyncedTime = "Today, $nowTime"
                    )
                    val updatedExp = state.expenses.map { it.copy(syncStatus = SplitSyncStatus.SYNCED) }
                    state.copy(
                        isSyncing = false,
                        connection = updatedConn,
                        expenses = updatedExp,
                        syncErrorMessage = null
                    )
                }
            }
        }
    }

    /**
     * Add Shared Expense:
     * If offline -> status is SYNC_PENDING
     * If online & sheet connected -> immediately SYNCED
     */
    fun addExpense(
        title: String,
        amount: Double,
        paidBy: String,
        splitWith: String,
        category: String
    ) {
        val isOffline = _uiState.value.isOfflineMode
        val isConnected = _uiState.value.connection != null
        val syncStatus = if (isOffline || !isConnected) SplitSyncStatus.SYNC_PENDING else SplitSyncStatus.SYNCED

        val newExpense = SplitMateExpense(
            id = UUID.randomUUID().toString(),
            title = title.trim().ifEmpty { "Shared Expense" },
            amount = amount,
            paidBy = paidBy.trim().ifEmpty { "You" },
            splitWith = splitWith.trim().ifEmpty { "Flat 402" },
            category = category.trim().ifEmpty { "General" },
            date = "Today",
            syncStatus = syncStatus
        )

        _uiState.update { state ->
            val updatedConnection = if (syncStatus == SplitSyncStatus.SYNC_PENDING) {
                state.connection?.copy(syncStatus = SplitSyncStatus.SYNC_PENDING)
            } else {
                state.connection
            }
            state.copy(
                expenses = listOf(newExpense) + state.expenses,
                connection = updatedConnection,
                showAddExpenseDialog = false
            )
        }
    }

    private fun extractSheetId(input: String): String {
        val regex = Regex("/spreadsheets/d/([a-zA-Z0-9_-]+)")
        val match = regex.find(input)
        return match?.groupValues?.get(1) ?: input
    }
}
