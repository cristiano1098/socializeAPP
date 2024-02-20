package com.example.cmu_g10.Data.Expense

import androidx.compose.runtime.MutableState
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.net.URI

/**
 * Represents a expense entity in the Room database.
 *
 * @property expenseId The unique identifier for the expense. Auto-generated using Room database.
 * @property description The description of the expense.
 * @property amount The amount of the expense.
 * @property dateOfExpense The date of the expense.
 * @property groupId The group ID of the expense.
 * @property receiptPhoto The receipt photo of the expense.
 * @property paidByUserId The user ID of the user who paid for the expense.
 */
@Entity
data class Expense(
    @PrimaryKey(autoGenerate = true) val expenseId: Int = 0,
    var description: String,
    var location: String,
    var amount: String,
    var dateOfExpense: String,
    var groupId: Int,
    var receiptPhoto : String,
    var paidByUserId: Int,
)
