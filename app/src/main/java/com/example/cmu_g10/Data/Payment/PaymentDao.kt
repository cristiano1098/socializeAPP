package com.example.cmu_g10.Data.Payment

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PaymentDao {
    /*
     * Insert a payment in the database.
     */
    @Insert
    fun insertPayment(payment: Payment): Long

    /*
     * Fetch all payments from the database.
     */
    @Query("SELECT * FROM Payment WHERE payerUserId = :userId OR payeeUserId = :userId")
    fun fetchUserPayments(userId: Int): List<Payment>
}