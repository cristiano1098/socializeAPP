package com.example.cmu_g10.Data.ExpenseParticipants

import com.example.cmu_g10.Data.GroupMembers.GroupMembers

//Represents the default state of the ExpenseParticipants entity.
data class ExpenseParticipantsState(
    val expenseParticipants: List<ExpenseParticipants> = emptyList(),
    var expenseId: Int = 0,
    var userId: Int = 0,
    var groupId: Int = 0,
    var owedAmount: Double = 0.0,
)