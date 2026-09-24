package com.spendora.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.spendora.data.cycle.CycleDateRange
import com.spendora.data.model.AccountEntity
import com.spendora.data.model.CategoryEntity
import com.spendora.data.model.TransactionEntity
import com.spendora.data.model.TransactionSource
import com.spendora.data.model.TransactionType
import com.spendora.data.repository.FinancialSummary
import com.spendora.data.repository.SpendoraRepository
import com.spendora.screens.transactions.TransactionFilter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode

@OptIn(ExperimentalCoroutinesApi::class)
class FinanceViewModel(
    private val repository: SpendoraRepository
) : ViewModel() {

    val currentCycle: CycleDateRange = repository.getCurrentCycle()

    // 1. All transactions from Room
    val allTransactions: StateFlow<List<TransactionEntity>> = repository.getAllTransactions()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // 2. Transactions within current financial cycle
    val cycleTransactions: StateFlow<List<TransactionEntity>> = repository.getTransactionsInCurrentCycle()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // 3. Real-time computed financial summary for the cycle
    val cycleSummary: StateFlow<FinancialSummary> = repository.getCycleSummary()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = FinancialSummary()
        )

    // 4. Filter & Search states for Transactions Screen
    private val _currentFilter = MutableStateFlow(TransactionFilter.ALL)
    val currentFilter: StateFlow<TransactionFilter> = _currentFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val filteredTransactions: StateFlow<List<TransactionEntity>> = combine(
        _currentFilter,
        _searchQuery
    ) { filter, query ->
        filter to query
    }.flatMapLatest { (filter, query) ->
        repository.getFilteredTransactions(filter, query)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // 5. Categories and Accounts
    val categories: StateFlow<List<CategoryEntity>> = repository.getCategories()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val accounts: StateFlow<List<AccountEntity>> = repository.getAccounts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun setFilter(filter: TransactionFilter) {
        _currentFilter.value = filter
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    /**
     * Parse amount safely into integer minor units (paise).
     * ₹579 -> 57900 paise. Strictly avoids floating point issues.
     */
    fun parseAmountToPaise(amountInput: String): Long {
        val clean = amountInput.trim().replace(",", "").replace("₹", "")
        if (clean.isBlank()) return 0L
        return try {
            BigDecimal(clean)
                .multiply(BigDecimal(100))
                .setScale(0, RoundingMode.HALF_UP)
                .longValueExact()
        } catch (e: Exception) {
            0L
        }
    }

    /**
     * Inserts transaction into Room database.
     */
    fun addTransaction(
        type: TransactionType,
        amountInput: String,
        merchant: String,
        category: String,
        account: String,
        dateTimeMillis: Long = System.currentTimeMillis(),
        note: String = "",
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val paise = parseAmountToPaise(amountInput)
        if (paise <= 0L) {
            onError("Please enter a valid amount greater than ₹0")
            return
        }

        viewModelScope.launch {
            try {
                repository.insertTransaction(
                    type = type,
                    amountPaise = paise,
                    merchant = merchant,
                    category = category,
                    account = account,
                    dateTime = dateTimeMillis,
                    note = note,
                    source = TransactionSource.MANUAL,
                    confidence = 1.0f
                )
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Failed to save transaction")
            }
        }
    }

    fun deleteTransaction(id: Long) {
        viewModelScope.launch {
            repository.deleteTransactionById(id)
        }
    }

    class Factory(private val repository: SpendoraRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(FinanceViewModel::class.java)) {
                return FinanceViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
