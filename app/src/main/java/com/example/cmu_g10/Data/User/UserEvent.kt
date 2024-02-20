package com.example.cmu_g10.Data.User

/**
 * Sealed interface representing various events related to user interactions.
 */
sealed interface UserEvent {

    //add user
    class RegisterUser(
        val name: String,
        val email: String,
        val password: String,
        val phone: String,
    ) : UserEvent

    //update user
    class UpdateUser(
        val id: Int,
        val name: String,
        val email: String,
        var password: String?,
        val phone: String,
    ) : UserEvent

    //login user
    class LoginUser(
        val email: String,
        val password: String,
    ) : UserEvent
}