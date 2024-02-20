package com.example.cmu_g10.Data.Expense

import androidx.compose.runtime.MutableState
import java.net.URI

interface ExpenseEvent {

    class AddExpense(
        val description: String,
        var location: String,
        val amount: String,
        val dateOfExpense: MutableState<String?>,
        val groupId: Int,
        val paidByUserId: Int,
        val receiptPhoto: String,
        val selectedUsers: MutableState<List<Int>>
    ): ExpenseEvent

    class UpdateExpense(
        val expenseId: Int,
        var location: String,
        val description: String,
        val amount: String,
        val dateOfExpense: String,
        val groupId: Int,
        val paidByUserId: Int,
        val selectedUsers: List<Int>
    ) : ExpenseEvent


}