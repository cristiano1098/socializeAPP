package com.example.cmu_g10.Data.Payment

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Data class that represents a payment.
 *
 * @param paymentId The id of the payment.
 * @param expenseId The id of the expense.
 * @param payerUserId The id of the payer.
 * @param payeeUserId The id of the payee.
 * @param amount The amount of the payment.
 */
@Entity
data class Payment(
    @PrimaryKey(autoGenerate = true) val paymentId: Int = 0,
    var expenseId : Int,
    var payerUserId : Int,
    var payeeUserId : Int,
    var amount : Double,
)
