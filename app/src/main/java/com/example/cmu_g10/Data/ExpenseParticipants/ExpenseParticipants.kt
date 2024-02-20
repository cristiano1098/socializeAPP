package com.example.cmu_g10.Data.ExpenseParticipants

import androidx.room.Entity

/**
 * Represents the relationship between an expense and its participants in the Room database.
 *
 * @property expenseId The unique identifier for the expense. Auto-generated using Room database.
 * @property userId The unique identifier for the user. Auto-generated using Room database.
 * @property groupId The unique identifier for the group. Auto-generated using Room database.
 * @property owedAmount The amount owed by the user for the expense.
 */
@Entity(primaryKeys = ["expenseId", "userId"])
data class ExpenseParticipants(
    val expenseId: Int,
    val userId: Int,
    val groupId: Int,
    val owedAmount: Double,
)
