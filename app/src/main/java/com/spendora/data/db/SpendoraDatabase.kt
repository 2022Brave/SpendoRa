package com.spendora.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.spendora.data.dao.AccountDao
import com.spendora.data.dao.CategoryDao
import com.spendora.data.dao.TransactionDao
import com.spendora.data.model.AccountEntity
import com.spendora.data.model.CategoryEntity
import com.spendora.data.model.DefaultFinancialData
import com.spendora.data.model.TransactionEntity
import com.spendora.data.model.TransactionSource
import com.spendora.data.model.TransactionType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RoomConverters {
    @TypeConverter
    fun fromTransactionType(type: TransactionType?): String =
        type?.name ?: TransactionType.EXPENSE.name

    @TypeConverter
    fun toTransactionType(value: String?): TransactionType = try {
        if (value != null) TransactionType.valueOf(value) else TransactionType.EXPENSE
    } catch (e: Exception) {
        TransactionType.EXPENSE
    }

    @TypeConverter
    fun fromTransactionSource(source: TransactionSource?): String =
        source?.name ?: TransactionSource.MANUAL.name

    @TypeConverter
    fun toTransactionSource(value: String?): TransactionSource = try {
        if (value != null) TransactionSource.valueOf(value) else TransactionSource.MANUAL
    } catch (e: Exception) {
        TransactionSource.MANUAL
    }
}

@Database(
    entities = [
        TransactionEntity::class,
        CategoryEntity::class,
        AccountEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(RoomConverters::class)
abstract class SpendoraDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun accountDao(): AccountDao

    companion object {
        @Volatile
        private var INSTANCE: SpendoraDatabase? = null

        fun getInstance(context: Context): SpendoraDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SpendoraDatabase::class.java,
                    "spendora_finance.db"
                )
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Seed approved categories & accounts once on initial database creation
                        CoroutineScope(Dispatchers.IO).launch {
                            seedDatabase(getInstance(context))
                        }
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }

        suspend fun seedDatabase(database: SpendoraDatabase) {
            val categoryDao = database.categoryDao()
            val accountDao = database.accountDao()

            if (categoryDao.getCount() == 0) {
                val expenseCategories = DefaultFinancialData.EXPENSE_CATEGORIES.map { name ->
                    CategoryEntity(name = name, isExpense = true)
                }
                val incomeCategories = DefaultFinancialData.INCOME_CATEGORIES.map { name ->
                    CategoryEntity(name = name, isExpense = false)
                }
                categoryDao.insertAll(expenseCategories + incomeCategories)
            }

            if (accountDao.getCount() == 0) {
                val accounts = DefaultFinancialData.ACCOUNTS.map { (name, type) ->
                    AccountEntity(name = name, type = type)
                }
                accountDao.insertAll(accounts)
            }
        }
    }
}
