package com.example.cmu_g10.Data.Expense

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.cmu_g10.Data.Group.Group

data class ExpenseState(
    val expenses: List<Expense> = emptyList(),
    var description: MutableState<String> = mutableStateOf(""),
    var location: MutableState<String> = mutableStateOf(""),
    var amount: MutableState<String> = mutableStateOf(""),
    var dateOfExpense: MutableState<String> = mutableStateOf(""),
    val receiptPhoto: MutableState<String> = mutableStateOf(""),
    var groupId: MutableState<Int> = mutableStateOf(0),
    var paidByUserId: MutableState<Int> = mutableStateOf(0),
)