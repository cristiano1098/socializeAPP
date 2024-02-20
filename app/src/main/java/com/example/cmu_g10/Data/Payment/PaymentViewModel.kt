package com.example.cmu_g10.Data.Payment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cmu_g10.Data.Expense.ExpenseDao
import com.example.cmu_g10.Data.Expense.ExpenseState
import com.example.cmu_g10.Data.Expense.ExpenseViewModel
import com.example.cmu_g10.Data.ExpenseParticipants.ExpenseParticipants
import com.example.cmu_g10.Data.Group.Group
import com.example.cmu_g10.Data.User.UserState
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PaymentViewModel(
    private val dao: PaymentDao
) : ViewModel() {
    private val _state = MutableStateFlow(PaymentState())
    private val _error = MutableLiveData<String>()
    private val _success = MutableLiveData<String>()
    private val _dbPayment = Firebase.firestore

    val state: StateFlow<PaymentState> = _state.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), PaymentState()
    )

    /**
     * Creates a new payment.
     *
     * @param expenseId The id of the expense.
     * @param payerUserId The id of the payer.
     * @param payeeUserId The id of the payee.
     * @param amount The amount of the payment.
     */
    fun recordPayment(
        expenseId: Int,
        payerUserId: Int,
        payeeUserId: Int,
        amount: Double
    ) {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                val payment = Payment(
                    expenseId = expenseId,
                    payerUserId = payerUserId,
                    payeeUserId = payeeUserId,
                    amount = amount,
                )
                dao.insertPayment(payment)

                val paymentMap = hashMapOf(
                    "expenseId" to expenseId,
                    "payerUserId" to payerUserId,
                    "payeeUserId" to payeeUserId,
                    "amount" to amount,
                )
                _dbPayment.collection("payments")
                    .document(expenseId.toString())
                    .set(paymentMap)
                    .addOnSuccessListener {
                        _success.postValue("Expense Created")
                    }
                    .addOnFailureListener {
                        _error.postValue("Error Creating Expense: ${it.message}")
                    }
            }
            _success.postValue("Payment recorded successfully")
        } catch (e: Exception) {
            _error.postValue("Payment failed to record")
        }
    }

    fun fetchUserPayments(userId: Int): LiveData<List<Payment>> {
        val data = MutableLiveData<List<Payment>>()
        try {
            viewModelScope.launch(Dispatchers.IO) {
                val payments = dao.fetchUserPayments(userId)
                data.postValue(payments)
            }
        } catch (e: Exception) {
            _error.postValue("Failed to fetch payments")
        }
        return data
    }
}
