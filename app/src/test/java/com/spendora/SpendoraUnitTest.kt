package com.spendora

import com.spendora.navigation.SpendoraDestination
import com.spendora.data.model.TransactionType
import com.spendora.screens.analytics.AnalyticsPeriod
import com.spendora.screens.splitmate.SplitMateExpense
import com.spendora.screens.splitmate.SplitMateState
import com.spendora.screens.splitmate.SplitSheetConnection
import com.spendora.screens.splitmate.SplitSyncStatus
import com.spendora.screens.transactions.TransactionFilter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SpendoraUnitTest {

    @Test
    fun testSpendoraDestinations() {
        val destinations = SpendoraDestination.values()
        assertEquals(5, destinations.size)
        assertEquals("Home", destinations[0].title)
        assertEquals("Transactions", destinations[1].title)
        assertEquals("Add", destinations[2].title)
        assertTrue(destinations[2].isPrimaryAction)
        assertEquals("Analytics", destinations[3].title)
        assertEquals("SplitMate", destinations[4].title)
    }

    @Test
    fun testTransactionTypes() {
        val types = TransactionType.values()
        assertEquals(5, types.size)
        assertEquals("EXPENSE", types[0].label)
        assertEquals("INCOME", types[1].label)
        assertEquals("TRANSFER", types[2].label)
        assertEquals("REFUND", types[3].label)
        assertEquals("CASH_WITHDRAWAL", types[4].label)
    }

    @Test
    fun testTransactionFilters() {
        val filters = TransactionFilter.values()
        assertEquals(4, filters.size)
        assertEquals("All", filters[0].label)
        assertEquals("Expenses", filters[1].label)
        assertEquals("Income", filters[2].label)
        assertEquals("Transfers", filters[3].label)
    }

    @Test
    fun testAnalyticsPeriods() {
        val periods = AnalyticsPeriod.values()
        assertEquals(7, periods.size)
        assertEquals("Current Cycle", periods[0].label)
        assertEquals("Previous Cycle", periods[1].label)
        assertEquals("Last 7 Days", periods[2].label)
        assertEquals("Last 30 Days", periods[3].label)
        assertEquals("Last 3 Months", periods[4].label)
        assertEquals("This Year", periods[5].label)
        assertEquals("Custom Range", periods[6].label)
    }

    @Test
    fun testSplitMateGoogleSheetConnection() {
        val connection = SplitSheetConnection(
            sheetId = "1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms",
            sheetName = "SplitMate — Flat 402 Expenses",
            sheetUrl = "https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms/edit",
            spaceName = "Flat 402 Shared Expenses",
            joinCode = "SPND-402X",
            connectedAccount = "2022brave@gmail.com",
            lastSyncedTime = "Just now",
            syncStatus = SplitSyncStatus.SYNCED
        )

        assertNotNull(connection)
        assertTrue(connection.sheetUrl.startsWith("https://docs.google.com/spreadsheets/d/"))
        assertTrue(connection.sheetUrl.contains(connection.sheetId))
        assertEquals(SplitSyncStatus.SYNCED, connection.syncStatus)
        assertEquals("SPND-402X", connection.joinCode)
    }

    @Test
    fun testSplitMateCalculationsAndOfflineSyncStatus() {
        val expenses = listOf(
            SplitMateExpense(
                id = "1",
                title = "Wifi",
                amount = 1000.0,
                paidBy = "You",
                splitWith = "All",
                category = "Utilities",
                date = "Today",
                syncStatus = SplitSyncStatus.SYNCED
            ),
            SplitMateExpense(
                id = "2",
                title = "Groceries",
                amount = 2000.0,
                paidBy = "Roommate",
                splitWith = "All",
                category = "Groceries",
                date = "Yesterday",
                syncStatus = SplitSyncStatus.SYNC_PENDING
            )
        )

        val state = SplitMateState(
            expenses = expenses,
            isOfflineMode = true
        )

        assertEquals(3000.0, state.totalShared, 0.001)
        assertEquals(1000.0, state.youSpent, 0.001)
        assertEquals(2000.0, state.roommateSpent, 0.001)
        // Total = 3000, your share = 1500, you spent 1000 => you owe 500
        assertEquals(-500.0, state.settlementAmount, 0.001)

        // Offline mode preserves local data with SYNC_PENDING
        assertEquals(SplitSyncStatus.SYNC_PENDING, state.expenses[1].syncStatus)
        assertEquals(SplitSyncStatus.SYNCED, state.expenses[0].syncStatus)
    }

    @Test
    fun testInitialSplitMateStateNoConnection() {
        val state = SplitMateState()
        org.junit.Assert.assertNull(state.connection)
        assertFalse(state.isOfflineMode)
        assertFalse(state.isSyncing)
    }
}
