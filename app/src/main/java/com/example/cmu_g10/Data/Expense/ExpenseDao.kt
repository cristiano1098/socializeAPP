package com.example.cmu_g10.Data.Expense

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.cmu_g10.Data.ExpenseParticipants.ExpenseParticipants
import com.example.cmu_g10.Data.Group.Group
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) interface for performing CRUD operations on the "Expense" entity in the Room database.
 */
@Dao
interface ExpenseDao {

    @Insert
    suspend fun insert(expense: Expense): Long

    @Update
    suspend fun update(expense: Expense): Int

    @Query("DELETE FROM Expense WHERE expenseId = :expenseId")
    suspend fun deleteExpense(expenseId: Int)

    @Query("SELECT * FROM expense WHERE expenseId = :expenseId")
    fun getExpenseById(expenseId: Int): Expense?

    @Query("SELECT * FROM expense WHERE groupId = :groupId ORDER BY dateOfExpense")
    fun getExpensesByGroupId(groupId: Int): Flow<List<Expense>>

    @Query("SELECT * FROM expense WHERE groupId = :groupId ORDER BY description ASC")
    fun getExpensesOrderedByTitle(groupId: Int): Flow<List<Expense>>

    @Transaction
    @Query("SELECT * FROM expense WHERE groupId = :groupId")
    suspend fun getExpensesForGroup(groupId: Int): List<Expense>
}