package com.spendora.data.repository

import com.spendora.data.cycle.CycleDateRange
import com.spendora.data.cycle.FinancialCycleCalculator
import com.spendora.data.dao.AccountDao
import com.spendora.data.dao.CategoryDao
import com.spendora.data.dao.TransactionDao
import com.spendora.data.db.SpendoraDatabase
import com.spendora.data.model.AccountEntity
import com.spendora.data.model.CategoryEntity
import com.spendora.data.model.TransactionEntity
import com.spendora.data.model.TransactionSource
import com.spendora.data.model.TransactionType
import com.spendora.screens.transactions.TransactionFilter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SpendoraRepository(
    private val database: SpendoraDatabase,
    private val cycleCalculator: FinancialCycleCalculator = FinancialCycleCalculator(1)
) {
    private val transactionDao: TransactionDao = database.transactionDao()
    private val categoryDao: CategoryDao = database.categoryDao()
    private val accountDao: AccountDao = database.accountDao()

    suspend fun initDatabase() {
        SpendoraDatabase.seedDatabase(database)
    }

    suspend fun insertTransaction(
        type: TransactionType,
        amountPaise: Long,
        merchant: String,
        category: String,
        account: String,
        dateTime: Long = System.currentTimeMillis(),
        note: String = "",
        source: TransactionSource = TransactionSource.MANUAL,
        confidence: Float = 1.0f
    ): Long {
        require(amountPaise > 0L) { "Transaction amount must be strictly greater than zero." }
        val finalCategory = category.ifBlank { "Other" }
        val finalAccount = account.ifBlank { "Bank Account" }

        val entity = TransactionEntity(
            type = type,
            amount = amountPaise,
            merchant = merchant.trim(),
            category = finalCategory,
            account = finalAccount,
            dateTime = dateTime,
            note = note.trim(),
            source = source,
            confidence = confidence,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        return transactionDao.insert(entity)
    }

    suspend fun updateTransaction(transaction: TransactionEntity) {
        transactionDao.update(transaction.copy(updatedAt = System.currentTimeMillis()))
    }

    suspend fun deleteTransaction(transaction: TransactionEntity) {
        transactionDao.delete(transaction)
    }

    suspend fun deleteTransactionById(id: Long) {
        transactionDao.deleteById(id)
    }

    suspend fun getTransactionById(id: Long): TransactionEntity? {
        return transactionDao.getById(id)
    }

    fun getAllTransactions(): Flow<List<TransactionEntity>> {
        return transactionDao.getAllTransactions()
    }

    fun getCurrentCycle(): CycleDateRange {
        return cycleCalculator.calculateCurrentCycle()
    }

    fun setCycleStartDay(day: Int) {
        cycleCalculator.cycleStartDay = day
    }

    fun getTransactionsInCurrentCycle(): Flow<List<TransactionEntity>> {
        val cycle = getCurrentCycle()
        return transactionDao.getTransactionsInDateRange(cycle.startMillis, cycle.endMillis)
    }

    fun getCycleSummary(): Flow<FinancialSummary> {
        val cycle = getCurrentCycle()
        return transactionDao.getTransactionsInDateRange(cycle.startMillis, cycle.endMillis)
            .map { list ->
                FinancialSummary.calculate(list, cycle.daysInCycle)
            }
    }

    fun getOverallSummary(): Flow<FinancialSummary> {
        return transactionDao.getAllTransactions()
            .map { list ->
                FinancialSummary.calculate(list, 30)
            }
    }

    fun getFilteredTransactions(filter: TransactionFilter, query: String): Flow<List<TransactionEntity>> {
        val baseFlow = if (query.isBlank()) {
            transactionDao.getAllTransactions()
        } else {
            transactionDao.searchTransactions(query.trim())
        }

        return baseFlow.map { list ->
            when (filter) {
                TransactionFilter.ALL -> list
                TransactionFilter.EXPENSES -> list.filter {
                    it.type == TransactionType.EXPENSE || it.type == TransactionType.CASH_WITHDRAWAL
                }
                TransactionFilter.INCOME -> list.filter {
                    it.type == TransactionType.INCOME || it.type == TransactionType.REFUND
                }
                TransactionFilter.TRANSFERS -> list.filter {
                    it.type == TransactionType.TRANSFER
                }
            }
        }
    }

    fun getCategories(): Flow<List<CategoryEntity>> = categoryDao.getAllCategories()

    fun getAccounts(): Flow<List<AccountEntity>> = accountDao.getAllAccounts()
}
