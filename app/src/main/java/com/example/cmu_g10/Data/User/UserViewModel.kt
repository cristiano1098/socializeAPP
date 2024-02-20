package com.example.cmu_g10.Data.User

import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cmu_g10.Data.Group.GroupState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import kotlin.math.abs

class UserViewModel(
    private val dao: UserDao
) : ViewModel() {
    private val _state = MutableStateFlow(UserState())
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val _loginStatus = MutableLiveData(LoginStatus.Idle)
    private val usersMap = mutableMapOf<Int, User>()
    private val filteredUsers = MutableLiveData<List<User>>()

    val loginStatus: LiveData<LoginStatus> = _loginStatus
    val registrationResult = MutableLiveData<RegistrationResult?>()
    val loggedUserData = MutableLiveData<User?>()
    val userData = MutableLiveData<User?>()
    val allUsers = MutableLiveData<List<User>>()
    val state: StateFlow<UserState> = _state.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        UserState()
    )

    //Enum class for login status
    enum class LoginStatus {
        Idle, Success, Failed
    }

    //Enum class for registration result
    enum class RegistrationResult {
        Success,
        EmailAlreadyRegistered,
    }

    //Initialize
    init {
        getAllUsers()
    }

    //Reset login status
    fun resetLoginStatus() {
        _loginStatus.value = LoginStatus.Idle
    }

    //Get User Info
    fun getUserInfo(userId: Int): LiveData<User> {
        return dao.getUserById(userId)
    }

    //Get User Live Data
    fun getUserLiveData(userId: Int): LiveData<User> {
        return dao.getUserById(userId)
    }

    //send Password Reset Email
    fun sendPasswordResetEmail(
        email: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                firebaseAuth.sendPasswordResetEmail(email).await()
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "An unknown error occurred")
            }
        }
    }

    /*
     * Function to get all users from the database
     */
    private fun getAllUsers() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Fetch user data from the database or a remote source
                val users = dao.getAllUsers()
                // Post the user data to LiveData
                allUsers.postValue(users)
                filteredUsers.postValue(users) // Initialize filteredUsers with all users initially
            } catch (e: Exception) {
                // Handle exceptions, like network errors or database issues
                allUsers.postValue(listOf())
                filteredUsers.postValue(listOf())
            }
        }
    }

    /**
     * Function to check if the email is already registered
     *
     * @param email The email address to check
     * @return True if the email is already registered, false otherwise
     */
    private fun isEmailRegistered(email: String): Boolean {
        return dao.getUserByEmail(email) != null
    }

    /**
     * Get a user by their ID
     *
     * @param id The ID of the user to get
     */
    fun getUser(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val user = dao.getUser(id)
                userData.postValue(user)
            } catch (e: Exception) {
                userData.postValue(null)
            }
        }
    }

    //Reset registration result
    fun resetRegistrationResult() {
        registrationResult.value = null
    }

    //logout user
    fun logoutUser() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                firebaseAuth.signOut()
                loggedUserData.postValue(null)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /*
     * Function to handle user events.
     */
    fun onEvent(event: UserEvent) {
        when (event) {
            is UserEvent.RegisterUser -> {
                viewModelScope.launch(Dispatchers.IO) {
                    if (!isEmailRegistered(event.email)) {
                        try {
                            val result = firebaseAuth.createUserWithEmailAndPassword(
                                event.email,
                                _state.value.password.value
                            ).await()

                            result.user?.let { firebaseUser ->
                                val user = User(
                                    name = _state.value.name.value,
                                    email = _state.value.email.value,
                                    phone = _state.value.phone.value,
                                    photo = "",
                                    balance = 0.0
                                )
                                val userId = dao.insert(user)
                                val registeredUser = user.copy(userId = userId.toInt())
                                loggedUserData.postValue(registeredUser)
                                registrationResult.postValue(RegistrationResult.Success)
                                getAllUsers()
                            }
                        } catch (e: FirebaseAuthException) {
                            // Handle FirebaseAuthException
                            registrationResult.postValue(RegistrationResult.EmailAlreadyRegistered)
                        } catch (e: Exception) {
                            // Handle generic exceptions
                            registrationResult.postValue(RegistrationResult.EmailAlreadyRegistered)
                        }
                    } else {
                        registrationResult.postValue(RegistrationResult.EmailAlreadyRegistered)
                    }
                }
            }

            is UserEvent.UpdateUser -> {
                viewModelScope.launch(Dispatchers.IO) {
                    try {
                        if (event.password?.isNotEmpty() == true) {
                            event.password.let {
                                if (it != null) {
                                    firebaseAuth.currentUser?.updatePassword(it)?.await()
                                }
                            }
                        } else {
                            event.password = null
                        }

                        val user = User(
                            userId = event.id,
                            name = event.name,
                            email = event.email,
                            phone = event.phone,
                            photo = "",
                            balance = 0.0,
                        )
                        dao.update(user)
                        loggedUserData.postValue(user)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            is UserEvent.LoginUser -> {
                viewModelScope.launch(Dispatchers.IO) {
                    try {
                        val result = firebaseAuth.signInWithEmailAndPassword(
                            event.email,
                            event.password
                        ).await()

                        if (result.user != null) {
                            val user = dao.getUserByEmail(event.email)
                            loggedUserData.postValue(user)
                            _loginStatus.postValue(LoginStatus.Success)
                        } else {
                            loggedUserData.postValue(null)
                            _loginStatus.postValue(LoginStatus.Failed)
                        }
                    } catch (e: Exception) {
                        loggedUserData.postValue(null)
                        _loginStatus.postValue(LoginStatus.Failed)
                    }
                }
            }
        }
    }
}