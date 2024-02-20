package com.example.cmu_g10.Data.ExpenseParticipants

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.cmu_g10.Data.Expense.Expense
import com.example.cmu_g10.Data.GroupMembers.GroupMembers
import com.example.cmu_g10.Data.User.User

@Dao
interface ExpenseParticipantsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(expenseParticipants: ExpenseParticipants): Long

    @Query("UPDATE ExpenseParticipants SET owedAmount = :newAmount WHERE userId = :userId AND expenseId = :expenseId")
    suspend fun updateOwedAmount(userId: Int, expenseId: Int, newAmount: Double)

    @Query("DELETE FROM ExpenseParticipants WHERE expenseId = :expenseId")
    suspend fun deleteParticipantsByExpense(expenseId: Int)

    //get expenseParticipants by id
    @Query("SELECT * FROM ExpenseParticipants WHERE expenseId = :expenseId")
    fun getExpenseParticipantsById(expenseId: Int): List<ExpenseParticipants>

    @Query("SELECT SUM(owedAmount) FROM ExpenseParticipants WHERE userId = :userId AND owedAmount > 0")
    fun getTotalAmountUserOwes(userId: Int): Double

    @Query("SELECT SUM(owedAmount) FROM ExpenseParticipants WHERE userId = :userId AND owedAmount < 0")
    fun getTotalAmountOwedToUser(userId: Int): Double

    @Query("SELECT SUM(expPart.owedAmount) FROM Expense exp INNER JOIN ExpenseParticipants expPart ON exp.expenseId = expPart.expenseId WHERE expPart.userId = :userId AND exp.groupId = :groupId AND owedAmount < 0")
    fun getTotalAmountOwedToUserInGroup(userId: Int, groupId: Int): Double

    @Query("SELECT SUM(expPart.owedAmount) FROM Expense exp INNER JOIN ExpenseParticipants expPart ON exp.expenseId = expPart.expenseId WHERE expPart.userId = :userId AND exp.groupId = :groupId AND owedAmount > 0")
    fun getTotalAmountUserOwesInGroup(userId: Int, groupId: Int): Double

    @Query("SELECT * FROM ExpenseParticipants WHERE userId = :userId AND owedAmount > 0")
    fun getAmountsUserOwes(userId: Int): List<ExpenseParticipants>?

    @Query(
        """
        SELECT expenseId, userId, groupId, owedAmount 
        FROM ExpenseParticipants 
        WHERE userId = :userId AND groupId = :groupId AND owedAmount > 0
        """
    )
    fun getExpensesForUserInGroup(userId: Int, groupId: Int): List<ExpenseParticipants>
    @Query("""
        SELECT *
        FROM ExpenseParticipants 
        WHERE userId = :userId
""")
    fun getExpensesForUser(userId: Int): List<ExpenseParticipants>

    @Query("SELECT owedAmount FROM ExpenseParticipants WHERE userId = :userId AND expenseId = :expenseId")
    fun getOwedAmount(userId: Int, expenseId: Int): Double?

    @Query("SELECT * FROM User WHERE userId IN (SELECT userId FROM ExpenseParticipants WHERE expenseId = :expenseId)")
     fun getExpenseParticipantsByUserId(expenseId: Int): List<User>

}