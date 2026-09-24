package com.spendora

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.spendora.data.cycle.FinancialCycleCalculator
import com.spendora.data.db.SpendoraDatabase
import com.spendora.data.model.TransactionEntity
import com.spendora.data.model.TransactionSource
import com.spendora.data.model.TransactionType
import com.spendora.data.repository.FinancialSummary
import com.spendora.data.repository.SpendoraRepository
import com.spendora.data.viewmodel.FinanceViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.Calendar

@RunWith(RobolectricTestRunner::class)
class SpendoraFinanceDatabaseTest {

    private lateinit var context: Context
    private lateinit var database: SpendoraDatabase
    private lateinit var repository: SpendoraRepository
    private lateinit var viewModel: FinanceViewModel

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        database = Room.inMemoryDatabaseBuilder(context, SpendoraDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = SpendoraRepository(database)
        viewModel = FinanceViewModel(repository)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun testFreshInstallZeroTransactions() = runBlocking {
        // Requirement 1 & 10: Fresh install has ZERO transactions
        val count = database.transactionDao().getTransactionCountSync()
        assertEquals(0, count)

        val transactions = repository.getAllTransactions().first()
        assertTrue(transactions.isEmpty())

        val summary = repository.getOverallSummary().first()
        assertEquals(0L, summary.totalSpentPaise)
        assertEquals(0L, summary.incomePaise)
        assertEquals(0L, summary.expensesPaise)
        assertEquals("₹0", summary.formattedTotalSpent)
    }

    @Test
    fun testApprovedCategoriesSeededOnce() = runBlocking {
        // Requirement 3: Seed approved categories on creation without duplicates
        repository.initDatabase()
        val catCount = database.categoryDao().getCount()
        assertEquals(27, catCount) // 21 expense + 6 income

        // Calling init again must NOT duplicate
        repository.initDatabase()
        val catCountAfter = database.categoryDao().getCount()
        assertEquals(27, catCountAfter)

        val accountsCount = database.accountDao().getCount()
        assertEquals(5, accountsCount)
    }

    @Test
    fun testInsertAndReadManualExpense_ExactPaiseStorage() = runBlocking {
        // Requirement 2 & 5: Save ₹579 expense -> exactly 57900 paise
        val paise = viewModel.parseAmountToPaise("579")
        assertEquals(57900L, paise)

        val id = repository.insertTransaction(
            type = TransactionType.EXPENSE,
            amountPaise = paise,
            merchant = "Shopping Store",
            category = "Shopping",
            account = "Bank Account",
            dateTime = System.currentTimeMillis(),
            note = "Bought clothes",
            source = TransactionSource.MANUAL,
            confidence = 1.0f
        )
        assertTrue(id > 0)

        val loaded = repository.getTransactionById(id)
        assertNotNull(loaded)
        assertEquals(TransactionType.EXPENSE, loaded!!.type)
        assertEquals(57900L, loaded.amount) // Exact minor units
        assertEquals(579.0, loaded.amountRupees, 0.001)
        assertEquals("₹579", loaded.formattedAmount)
        assertEquals("Shopping Store", loaded.merchant)
        assertEquals("Shopping", loaded.category)
        assertEquals("Bank Account", loaded.account)
        assertEquals(TransactionSource.MANUAL, loaded.source)
    }

    @Test
    fun testUpdateExpense() = runBlocking {
        val id = repository.insertTransaction(
            type = TransactionType.EXPENSE,
            amountPaise = 57900L,
            merchant = "Old Merchant",
            category = "Shopping",
            account = "Bank Account"
        )

        val existing = repository.getTransactionById(id)!!
        val updated = existing.copy(
            merchant = "New Store",
            amount = 65000L
        )
        repository.updateTransaction(updated)

        val reloaded = repository.getTransactionById(id)
        assertEquals("New Store", reloaded!!.merchant)
        assertEquals(65000L, reloaded.amount)
        assertEquals("₹650", reloaded.formattedAmount)
    }

    @Test
    fun testDeleteExpense() = runBlocking {
        val id = repository.insertTransaction(
            type = TransactionType.EXPENSE,
            amountPaise = 57900L,
            merchant = "To Delete",
            category = "Dining",
            account = "Cash"
        )
        assertEquals(1, database.transactionDao().getTransactionCountSync())

        repository.deleteTransactionById(id)
        assertEquals(0, database.transactionDao().getTransactionCountSync())
        assertNull(repository.getTransactionById(id))
    }

    @Test
    fun testExpenseAndIncomeTotalsAndNetCashFlow() = runBlocking {
        // Requirement 6: Total Spent, Income, Expenses, Net Cash Flow
        // Insert ₹10,000 Income (1,000,000 paise)
        repository.insertTransaction(
            type = TransactionType.INCOME,
            amountPaise = 1000000L,
            merchant = "Employer",
            category = "Salary",
            account = "Bank Account"
        )

        // Insert ₹579 Expense (57,900 paise)
        repository.insertTransaction(
            type = TransactionType.EXPENSE,
            amountPaise = 57900L,
            merchant = "Store",
            category = "Shopping",
            account = "Bank Account"
        )

        val summary = repository.getOverallSummary().first()
        assertEquals(1000000L, summary.incomePaise)
        assertEquals(57900L, summary.expensesPaise)
        assertEquals(57900L, summary.totalSpentPaise)
        // Net Cash Flow = 10,000 - 579 = 9,421 (942,100 paise)
        assertEquals(942100L, summary.netCashFlowPaise)
        assertEquals("₹10,000", summary.formattedIncome)
        assertEquals("₹579", summary.formattedTotalSpent)
        assertEquals("₹9,421", summary.formattedNetCashFlow)
    }

    @Test
    fun testTransferExclusionFromOrdinarySpending() = runBlocking {
        // Requirement 6: Transfers between own accounts must NOT count as ordinary spending
        // Insert ₹579 Expense
        repository.insertTransaction(
            type = TransactionType.EXPENSE,
            amountPaise = 57900L,
            merchant = "Shopping",
            category = "Shopping",
            account = "Bank Account"
        )

        // Insert ₹2,000 Transfer between Bank Account and UPI / Wallet
        repository.insertTransaction(
            type = TransactionType.TRANSFER,
            amountPaise = 200000L,
            merchant = "Self Transfer",
            category = "Account Transfer",
            account = "Bank Account"
        )

        val summary = repository.getOverallSummary().first()
        // Ordinary spending should strictly remain ₹579, NOT ₹2,579!
        assertEquals(57900L, summary.totalSpentPaise)
        assertEquals(200000L, summary.transfersPaise)
    }

    @Test
    fun testCreditCardPaymentTreatedAsTransfer() = runBlocking {
        // Requirement 6: Credit card payments treated as TRANSFER
        repository.insertTransaction(
            type = TransactionType.TRANSFER,
            amountPaise = 1500000L,
            merchant = "HDFC Card Payment",
            category = "Credit Card Payment",
            account = "Bank Account"
        )

        val summary = repository.getOverallSummary().first()
        assertEquals(0L, summary.totalSpentPaise)
        assertEquals(1500000L, summary.transfersPaise)
    }

    @Test
    fun testInvestmentsDoNotInflateOrdinarySpending() = runBlocking {
        // Requirement 6: Investments must not inflate ordinary spending analytics
        repository.insertTransaction(
            type = TransactionType.EXPENSE,
            amountPaise = 57900L,
            merchant = "Groceries",
            category = "Groceries",
            account = "UPI / Wallet"
        )

        repository.insertTransaction(
            type = TransactionType.EXPENSE,
            amountPaise = 5000000L, // ₹50,000 investment
            merchant = "Zerodha",
            category = "Investment",
            account = "Bank Account"
        )

        val summary = repository.getOverallSummary().first()
        // Ordinary total spent is ONLY ₹579, NOT ₹50,579
        assertEquals(57900L, summary.totalSpentPaise)
        assertEquals(5000000L, summary.investmentsPaise)
        assertEquals("₹579", summary.formattedTotalSpent)
        assertEquals("₹50,000", summary.formattedInvestments)
    }

    @Test
    fun testRefundHandlingSeparately() = runBlocking {
        // Requirement 6: Refunds must be handled separately
        repository.insertTransaction(
            type = TransactionType.EXPENSE,
            amountPaise = 57900L,
            merchant = "Amazon",
            category = "Shopping",
            account = "Credit Card"
        )

        repository.insertTransaction(
            type = TransactionType.REFUND,
            amountPaise = 10000L, // ₹100 refund
            merchant = "Amazon Refund",
            category = "Refund",
            account = "Credit Card"
        )

        val summary = repository.getOverallSummary().first()
        assertEquals(57900L, summary.totalSpentPaise)
        assertEquals(10000L, summary.refundsPaise)
        assertEquals("₹100", summary.formattedRefunds)
    }

    @Test
    fun testFinancialCycleDateRangeCalculation() {
        // Requirement 7: Configurable financial cycle foundation
        val cycleCalc = FinancialCycleCalculator(1) // 1st -> end of month
        val now = Calendar.getInstance().apply {
            set(2026, Calendar.SEPTEMBER, 24, 12, 0, 0)
        }
        val range = cycleCalc.calculateCurrentCycle(now.timeInMillis)

        assertEquals(1, range.startDay)
        assertEquals(30, range.endDay) // September has 30 days
        assertEquals("September", range.monthName)
        assertEquals(2026, range.year)
        assertTrue(range.label.contains("September 2026"))

        // Future configurable start day test: 25th start day
        val customCalc = FinancialCycleCalculator(25)
        val customRange = customCalc.calculateCurrentCycle(now.timeInMillis)
        // Since Sept 24 < 25, cycle started Aug 25 and ends Sept 24
        assertEquals(25, customRange.startDay)
        assertEquals(24, customRange.endDay)
    }

    @Test
    fun testSearchAndFilterQueries() = runBlocking {
        // Requirement 8: Search and filters on Room data
        repository.insertTransaction(
            type = TransactionType.EXPENSE,
            amountPaise = 57900L,
            merchant = "Swiggy Food",
            category = "Dining",
            account = "UPI / Wallet",
            note = "Dinner with friends"
        )

        repository.insertTransaction(
            type = TransactionType.INCOME,
            amountPaise = 500000L,
            merchant = "Freelance Client",
            category = "Freelance",
            account = "Bank Account"
        )

        // Filter: EXPENSES
        val expenses = repository.getFilteredTransactions(com.spendora.screens.transactions.TransactionFilter.EXPENSES, "").first()
        assertEquals(1, expenses.size)
        assertEquals(57900L, expenses[0].amount)

        // Search: "Swiggy"
        val searchSwiggy = repository.getFilteredTransactions(com.spendora.screens.transactions.TransactionFilter.ALL, "Swiggy").first()
        assertEquals(1, searchSwiggy.size)

        // Search: "friends" (found in note)
        val searchNote = repository.getFilteredTransactions(com.spendora.screens.transactions.TransactionFilter.ALL, "friends").first()
        assertEquals(1, searchNote.size)

        // Search nonexistent
        val searchNone = repository.getFilteredTransactions(com.spendora.screens.transactions.TransactionFilter.ALL, "NonExistent").first()
        assertEquals(0, searchNone.size)
    }

    @Test
    fun testAmountParsingDecimalPrecision() {
        assertEquals(57900L, viewModel.parseAmountToPaise("579"))
        assertEquals(57900L, viewModel.parseAmountToPaise("579.00"))
        assertEquals(57950L, viewModel.parseAmountToPaise("579.50"))
        assertEquals(57950L, viewModel.parseAmountToPaise("₹579.50"))
        assertEquals(1234567L, viewModel.parseAmountToPaise("12,345.67"))
        assertEquals(0L, viewModel.parseAmountToPaise(""))
        assertEquals(0L, viewModel.parseAmountToPaise("0"))
    }
}
