package com.example.cmu_g10.Data.Expense

import android.net.Uri
import android.util.Log
import androidx.annotation.MainThread
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cmu_g10.Services.AutoComplete.Feature
import com.example.cmu_g10.Services.AutoComplete.GeoapifyApiService
import com.example.cmu_g10.Data.ExpenseParticipants.ExpenseParticipants
import com.example.cmu_g10.Data.ExpenseParticipants.ExpenseParticipantsDao
import com.example.cmu_g10.Data.Payment.Payment
import com.example.cmu_g10.Data.User.User
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.abs
import kotlin.math.max

class ExpenseViewModel(
    private val dao: ExpenseDao,
    private val expenseParticipantsDao: ExpenseParticipantsDao,
    private val geoapifyApi: GeoapifyApiService
) : ViewModel() {
    private val _state = MutableStateFlow(ExpenseState())
    private val _expenseDetails = MutableLiveData<Expense?>()
    private val _participants = mutableListOf<ExpenseParticipants>()
    private val _expenses = MutableLiveData<List<ExpenseParticipants>>()
    private val _groupSpecificExpenses = mutableStateOf<List<ExpenseParticipants>>(listOf())
    private val _errorState = MutableLiveData<String>()
    private val _successState = MutableLiveData<String>()
    private val _dbExpenses = Firebase.firestore
    private val _dbExpenseParticipants = Firebase.firestore

    val expenseDetails: LiveData<Expense?> = _expenseDetails
    val expenses: MutableLiveData<List<ExpenseParticipants>> = _expenses
    val groupSpecificExpenses: MutableState<List<ExpenseParticipants>> = _groupSpecificExpenses
    val state: StateFlow<ExpenseState> = _state.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), ExpenseState()
    )
    val groupExpenses = MutableLiveData<List<Expense?>>()
    val errorState: LiveData<String> = _errorState
    val successState: LiveData<String> = _successState

    private val _autocompleteResults = MutableLiveData<List<Feature>>()
    val autocompleteResults: LiveData<List<Feature>> = _autocompleteResults

    private val _photoUri = MutableLiveData<String>()
    val photoUri: LiveData<String> = _photoUri

    /**
     * Set Photo Uri
     *
     * @param uri
     */
    fun setPhotoUri(uri: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _photoUri.postValue(uri)
        }
    }

    //Clear Autocomplete Results
    fun clearAutocompleteResults() {
        _autocompleteResults.value = emptyList()
    }

    /**
     * Search locations
     *
     * @param query
     */
    fun searchLocations(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response =
                    geoapifyApi.autocomplete(query, "f01f17cef32146fbba9f71f72030fa51")
                        .execute()
                if (response.isSuccessful) {
                    Log.d("Autocomplete Raw", "Raw response: ${response.raw()}")
                    val results = response.body()?.features ?: emptyList()
                    _autocompleteResults.postValue(results)
                    Log.d("Autocomplete", "Results: $results")
                } else {
                    _autocompleteResults.postValue(emptyList())
                    Log.d("Autocomplete", "Error: ${response.errorBody()}")
                }
            } catch (e: Exception) {
                _autocompleteResults.postValue(emptyList())
                Log.d("Autocomplete", "Error: ${e.message}")
            }
        }
    }


    /**
     * Get expenses for user
     *
     * @param userId
     * @return LiveData<List<ExpenseParticipants>>
     */
    fun getExpensesForUser(userId: Int): LiveData<List<ExpenseParticipants>> {
        val data = MutableLiveData<List<ExpenseParticipants>>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val expenses = expenseParticipantsDao.getExpensesForUser(userId)
                data.postValue(expenses)
            } catch (e: Exception) {
                _errorState.postValue("Error Fetching Expenses")
            }
        }
        return data
    }

    /**
     * Get expenses for user in group
     *
     * @param userId
     * @param groupId
     */
    fun getExpensesForUserInGroup(userId: Int, groupId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val expenses = expenseParticipantsDao.getExpensesForUserInGroup(userId, groupId)
                _groupSpecificExpenses.value = expenses
            } catch (e: Exception) {
                _errorState.postValue("Error Fetching Expenses")
            }
        }
    }

    /**
     * Get expenses for user
     *
     * @param userId
     * @return LiveData<Expense?>
     */
    fun getExpenseLiveData(expenseId: Int): LiveData<Expense?> {
        val data = MutableLiveData<Expense?>()

        viewModelScope.launch(Dispatchers.IO) {
            // Perform database operation on IO (background) thread
            val expense = if (expenseId == 0) {
                null
            } else {
                dao.getExpenseById(expenseId)
            }
            data.postValue(expense)
        }

        return data
    }

    /**
     * Get expense participants by expense id
     *
     * @param expenseId
     * @return LiveData<List<ExpenseParticipants>>
     */
    fun getExpenseParticipantsById(expenseId: Int): LiveData<List<ExpenseParticipants>> {
        val data = MutableLiveData<List<ExpenseParticipants>>()

        viewModelScope.launch(Dispatchers.IO) {
            // Perform database operation on IO (background) thread
            val expenseParticipants = if (expenseId == 0) {
                emptyList()
            } else {
                expenseParticipantsDao.getExpenseParticipantsById(expenseId)
            }
            data.postValue(expenseParticipants)
        }

        return data
    }

    /**
     * Get amount owed by user in group
     *
     * @param userId
     * @param groupId
     * @return LiveData<Double>
     */
    fun getAmountOwedByUserInGroup(userId: Int, groupId: Int): LiveData<Double> {
        val data = MutableLiveData<Double>()
        viewModelScope.launch(Dispatchers.IO) {
            val amount = expenseParticipantsDao.getTotalAmountUserOwesInGroup(userId, groupId)
            val positiveAmount = abs(amount)
            data.postValue(positiveAmount)
        }
        return data
    }

    /**
     * Get amount owed to user in group
     *
     * @param userId
     * @param groupId
     * @return LiveData<Double>
     */
    fun getAmountOwedToUserInGroup(userId: Int, groupId: Int): LiveData<Double> {
        val data = MutableLiveData<Double>()
        viewModelScope.launch(Dispatchers.IO) {
            val amount = expenseParticipantsDao.getTotalAmountOwedToUserInGroup(userId, groupId)
            val positiveAmount = abs(amount)
            data.postValue(positiveAmount)
        }
        return data
    }

    /**
     * Get net balance for user in group
     *
     * @param userId
     * @param groupId
     * @return LiveData<Double>
     */
    fun getUserNetBalanceInGroup(userId: Int, groupId: Int): LiveData<Double> {
        val data = MutableLiveData<Double>()
        viewModelScope.launch(Dispatchers.IO) {
            val amountOwedToUser = expenseParticipantsDao.getTotalAmountOwedToUserInGroup(
                userId,
                groupId
            )
            val amountUserOwes = expenseParticipantsDao.getTotalAmountUserOwesInGroup(
                userId,
                groupId
            )

            val positiveAmountOwedToUser = abs(amountOwedToUser)

            val netBalance = if (amountUserOwes > positiveAmountOwedToUser) {
                -(amountUserOwes - positiveAmountOwedToUser) // Resultado negativo
            } else {
                positiveAmountOwedToUser - amountUserOwes // Resultado positivo ou zero
            }

            data.postValue(netBalance)
        }
        return data
    }

    /**
     * Get amount owed to user
     *
     * @param userId
     * @return LiveData<Double>
     */
    fun getAmountOwedToUser(userId: Int): LiveData<Double> {
        val data = MutableLiveData<Double>()
        viewModelScope.launch(Dispatchers.IO) {
            val amount = expenseParticipantsDao.getTotalAmountOwedToUser(userId)
            val positiveAmount = abs(amount)
            data.postValue(positiveAmount)
        }
        return data
    }

    /**
     * Get amount user owes
     *
     * @param userId
     * @return LiveData<Double>
     */
    fun getAmountUserOwes(userId: Int): LiveData<Double> {
        val data = MutableLiveData<Double>()
        viewModelScope.launch(Dispatchers.IO) {
            val amount = expenseParticipantsDao.getTotalAmountUserOwes(userId)
            val positiveAmount = abs(amount)
            data.postValue(positiveAmount)
        }
        return data
    }

    /**
     * Get net balance for user
     *
     * @param userId
     * @return LiveData<Double>
     */
    fun getUserNetBalance(userId: Int): LiveData<Double> {
        val data = MutableLiveData<Double>()
        viewModelScope.launch(Dispatchers.IO) {
            val amountOwedToUser = expenseParticipantsDao.getTotalAmountOwedToUser(userId)
            val amountUserOwes = expenseParticipantsDao.getTotalAmountUserOwes(userId)

            val positiveAmountOwedToUser = abs(amountOwedToUser)

            val netBalance = if (amountUserOwes > positiveAmountOwedToUser) {
                -(amountUserOwes - positiveAmountOwedToUser) // Resultado negativo
            } else {
                positiveAmountOwedToUser - amountUserOwes // Resultado positivo ou zero
            }

            data.postValue(netBalance)
        }
        return data
    }

    /**
     * Get expenses
     *
     * @param expenseId
     */
    fun getExpense(expenseId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val expense = dao.getExpenseById(expenseId)
                _expenseDetails.postValue(expense)
            } catch (e: Exception) {
                _errorState.postValue("Error Fetching Expense")
            }
        }
    }

    /**
     * Get expenses for group
     *
     * @param groupId
     */
    fun fetchGroupExpenses(groupId: Int) {
        try {
            viewModelScope.launch {
                groupExpenses.value = dao.getExpensesForGroup(groupId)
            }
        } catch (e: Exception) {
            _errorState.postValue("Error fetching expenses")
        }
    }

    /**
     * Delete expense
     *
     * @param expenseId
     * @param groupId
     */
    fun deleteExpense(expenseId: Int, groupId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                dao.deleteExpense(expenseId)
                expenseParticipantsDao.deleteParticipantsByExpense(expenseId)

                // Delete expense from firebase
                _dbExpenses.collection("expenses")
                    .document(expenseId.toString())
                    .delete()
                    .addOnSuccessListener {
                        _successState.postValue("Expense Deleted Successfully")
                    }
                    .addOnFailureListener {
                        _errorState.postValue("Error Deleting Expense: ${it.message}")
                    }

                fetchGroupExpenses(groupId)
                _expenseDetails.postValue(null)
                _successState.postValue("Expense Deleted Successfully")
            } catch (e: Exception) {
                _errorState.postValue("Error Deleting Expense: ${e.message}")
            }
        }
    }

    /**
     * Get amounts user owes
     *
     * @param userId
     * @return MutableLiveData<List<ExpenseParticipants>?>
     */
    fun getAmountsUserOwes(userId: Int): MutableLiveData<List<ExpenseParticipants>?> {
        val data = MutableLiveData<List<ExpenseParticipants>?>()

        viewModelScope.launch(Dispatchers.IO) {
            val amountsUserOwes = expenseParticipantsDao.getAmountsUserOwes(userId)
            data.postValue(amountsUserOwes)
        }
        return data
    }

    /**
     * Updates the amount a user owes for a specific expense.
     *
     * @param expensePayerId The user ID of the user who paid for the expense.
     * @param expensePayeeId The user ID of the user who owes money for the expense.
     * @param expenseId The expense ID of the expense.
     * @param amountPaid The amount paid by the user who paid for the expense.
     */
    fun updateAmountUserOwes(
        expensePayerId: Int,
        expensePayeeId: Int,
        expenseId: Int,
        amountPaid: Double
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val currentOwedAmountPayer =
                    expenseParticipantsDao.getOwedAmount(expensePayerId, expenseId) ?: 0.0
                val currentOwedAmountPayee =
                    expenseParticipantsDao.getOwedAmount(expensePayeeId, expenseId) ?: 0.0


                val newAmountPayer = -(currentOwedAmountPayer) - amountPaid
                val newAmountPayee = currentOwedAmountPayee - amountPaid

                expenseParticipantsDao.updateOwedAmount(
                    expensePayerId,
                    expenseId,
                    -(newAmountPayer)
                )
                expenseParticipantsDao.updateOwedAmount(expensePayeeId, expenseId, newAmountPayee)

                if (newAmountPayer <= 0.01) {
                    expenseParticipantsDao.deleteParticipantsByExpense(expenseId)
                    dao.deleteExpense(expenseId)
                }
                _successState.postValue("Amount Owed Updated Successfully")
            } catch (e: Exception) {
                _errorState.postValue("Error Updating Amount Owed: ${e.message}")
            }
        }
    }

    //On event
    fun onEvent(event: ExpenseEvent) {
        when (event) {
            is ExpenseEvent.AddExpense -> {

                viewModelScope.launch(Dispatchers.IO) {
                    try {
                        val expense = event.dateOfExpense.value?.let {
                            Expense(
                                description = _state.value.description.value,
                                location = event.location,
                                amount = _state.value.amount.value,
                                dateOfExpense = it,
                                groupId = event.groupId,
                                receiptPhoto = _state.value.receiptPhoto.value,
                                paidByUserId = event.paidByUserId,
                            )
                        }

                        Log.d("ExpenseViewModel", "Expense: $expense")

                        val expenseId = expense?.let { dao.insert(it).toInt() }

                        // Add expenses to firebase
                        val expenseMap = hashMapOf(
                            "description" to expense?.description,
                            "location" to expense?.location,
                            "amount" to expense?.amount,
                            "dateOfExpense" to expense?.dateOfExpense,
                            "groupId" to expense?.groupId,
                            "receiptPhoto" to expense?.receiptPhoto,
                            "paidByUserId" to expense?.paidByUserId,
                        )
                        _dbExpenses.collection("expenses")
                            .document(expenseId.toString())
                            .set(expenseMap)
                            .addOnSuccessListener {
                                _successState.postValue("Expense Created")
                            }
                            .addOnFailureListener {
                                _errorState.postValue("Error Creating Expense: ${it.message}")
                            }

                        if (expenseId != null) {
                            val totalParticipants = event.selectedUsers.value.size + 1
                            val amountPerUser =
                                _state.value.amount.value.toDouble() / totalParticipants

                            _participants.add(
                                ExpenseParticipants(
                                    expenseId = expenseId,
                                    userId = event.paidByUserId,
                                    groupId = event.groupId,
                                    owedAmount = -(_state.value.amount.value.toDouble() - amountPerUser)
                                )
                            )

                            event.selectedUsers.value.forEach { userId ->
                                _participants.add(
                                    ExpenseParticipants(
                                        expenseId = expenseId,
                                        userId = userId,
                                        groupId = event.groupId,
                                        owedAmount = amountPerUser
                                    )
                                )
                            }
                            _participants.forEach { expenseParticipant ->
                                expenseParticipantsDao.insert(expenseParticipant)

                                // Add expenseParticipants to firebase
                                val expenseParticipantsMap = hashMapOf(
                                    "expenseId" to expenseParticipant.expenseId,
                                    "userId" to expenseParticipant.userId,
                                    "groupId" to expenseParticipant.groupId,
                                    "owedAmount" to expenseParticipant.owedAmount,
                                )
                                _dbExpenseParticipants.collection("expenseParticipants")
                                    .document(expenseParticipant.expenseId.toString())
                                    .set(expenseParticipantsMap)
                                    .addOnSuccessListener {
                                        _successState.postValue("Expense Created")
                                    }
                                    .addOnFailureListener {
                                        _errorState.postValue("Error Creating Expense: ${it.message}")
                                    }
                            }

                            _expenseDetails.postValue(expense.copy(expenseId = expenseId))
                            _successState.postValue("Expense Created")
                        }
                    } catch (e: Exception) {
                        _errorState.postValue("Error Creating Expense: ${e.message}")
                    }
                }
            }

            is ExpenseEvent.UpdateExpense -> {
                viewModelScope.launch(Dispatchers.IO) {
                    try {
                        val expense = event.dateOfExpense.let {
                            Expense(
                                expenseId = event.expenseId,
                                description = event.description,
                                location = event.location,
                                amount = event.amount,
                                dateOfExpense = it,
                                groupId = event.groupId,
                                receiptPhoto = _state.value.description.value,
                                paidByUserId = event.paidByUserId,
                            )
                        }

                        val expenseId = expense.let { dao.update(it) }

                        val totalParticipants = event.selectedUsers.size + 1
                        val amountPerUser =
                            event.amount.toDouble() / totalParticipants

                        _participants.add(
                            ExpenseParticipants(
                                expenseId = event.expenseId,
                                userId = event.paidByUserId,
                                groupId = event.groupId,
                                owedAmount = -(event.amount.toDouble() - amountPerUser)
                            )
                        )

                        event.selectedUsers.forEach { userId ->
                            _participants.add(
                                ExpenseParticipants(
                                    expenseId = event.expenseId,
                                    userId = userId,
                                    groupId = event.groupId,
                                    owedAmount = amountPerUser
                                )
                            )
                        }
                        _participants.forEach { expenseParticipant ->
                            expenseParticipantsDao.insert(expenseParticipant)
                        }


                        _expenseDetails.postValue(expense.copy(expenseId = expenseId))
                        _successState.postValue("Expense Edited")

                    } catch (e: Exception) {
                        _errorState.postValue("Error Editing Expense: ${e.message}")
                    }
                }
            }
        }
    }
}
