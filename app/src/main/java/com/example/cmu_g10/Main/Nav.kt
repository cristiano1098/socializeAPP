package com.example.cmu_g10.main

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.cmu_g10.Services.Camara.CamaraActivity
import com.example.cmu_g10.Services.AutoComplete.GeoapifyApiService
import com.example.cmu_g10.Data.Expense.ExpenseEvent
import com.example.cmu_g10.Data.Expense.ExpenseState
import com.example.cmu_g10.Data.Expense.ExpenseViewModel
import com.example.cmu_g10.Data.Group.GroupEvent
import com.example.cmu_g10.Data.Group.GroupState
import com.example.cmu_g10.Data.Group.GroupViewModel
import com.example.cmu_g10.Services.Maps.GoogleMapClustering
import com.example.cmu_g10.Services.Maps.geocodeAddress
import com.example.cmu_g10.Data.Payment.PaymentState
import com.example.cmu_g10.Data.Payment.PaymentViewModel
import com.example.cmu_g10.Data.User.UserEvent
import com.example.cmu_g10.Data.User.UserState
import com.example.cmu_g10.Data.User.UserViewModel
import com.example.cmu_g10.Screens.AddExpenseScreen
import com.example.cmu_g10.Screens.AddGroupScreen
import com.example.cmu_g10.Screens.EditExpenseScreen
import com.example.cmu_g10.Screens.EditGroupScreen
import com.example.cmu_g10.Screens.EditProfileScreen
import com.example.cmu_g10.Screens.ExpenseChartScreen
import com.example.cmu_g10.Screens.GroupExpenseScreen
import com.example.cmu_g10.Screens.HomeScreen
import com.example.cmu_g10.Screens.LoginRegisterScreen
import com.example.cmu_g10.Screens.ManageGroupMembersScreen
import com.example.cmu_g10.Screens.PasswordRecoveryScreen
import com.example.cmu_g10.Screens.SettleBalanceScreen
import com.example.cmu_g10.Screens.SettleBalancesDetailsScreen
import com.example.cmu_g10.Screens.SettleGroupBalanceScreen
import com.example.cmu_g10.Screens.SuccessScreen
import kotlin.reflect.KFunction1

/**
 * Composable function representing the navigation structure of the CMU_G10 application.
 *
 * @param groupState The [GroupState] that holds the state for the group.
 * @param userState The [UserState] that holds the state for the user.
 * @param expenseState The [ExpenseState] that holds the state for the expense.
 * @param paymentState The [PaymentState] that holds the state for the payment.
 * @param onGroupEvent The event handler for the group.
 * @param onUserEvent The event handler for the user.
 * @param onExpenseEvent The event handler for the expense.
 * @param groupViewmodel The [GroupViewModel] that holds the state for the group.
 * @param userViewmodel The [UserViewModel] that holds the state for the user.
 * @param expenseViewmodel The [ExpenseViewModel] that holds the state for the expense.
 * @param paymentViewmodel The [PaymentViewModel] that holds the state for the payment.
 * @param contactEmails The list of emails of contacts.
 * @param context The context of the application.
 */
@Composable
fun Nav(
    groupState: GroupState,
    userState: UserState,
    expenseState: ExpenseState,
    paymentState: PaymentState,
    onGroupEvent: KFunction1<GroupEvent, Unit>,
    onUserEvent: (UserEvent) -> Unit,
    onExpenseEvent: KFunction1<ExpenseEvent, Unit>,
    groupViewmodel: GroupViewModel,
    userViewmodel: UserViewModel,
    expenseViewmodel: ExpenseViewModel,
    paymentViewmodel: PaymentViewModel,
    contactEmails: List<String>,
    context: Context
) {
    // Create a NavController using rememberNavController
    val navController = rememberNavController()

    // Define the navigation structure using NavHost
    NavHost(navController = navController, startDestination = "login/Register") {
        composable("login/Register") {
            // Screen for login and registration
            LoginRegisterScreen(
                navController = navController,
                state = userState,
                onEvent = userViewmodel::onEvent,
                userViewmodel
            )
        }
        composable("RecoverPassword") {
            // Screen for password recovery
            PasswordRecoveryScreen(navController = navController, userViewmodel)
        }
        composable("homeScreen") {
            // Home screen displaying group and expense information
            HomeScreen(
                navController = navController,
                userViewmodel,
                groupViewmodel,
                expenseViewmodel,
                paymentViewmodel
            )
        }
        composable("editProfile") {
            // Screen for editing user profile information
            EditProfileScreen(navController = navController, userViewmodel, onUserEvent, userState)
        }
        composable("settleBalance") {
            // Screen for settling balance within the group
            SettleBalanceScreen(navController = navController, userViewmodel, expenseViewmodel)
        }
        composable("settleBalance/{groupId}") { backStackEntry ->
            // Extract the group ID from the backStackEntry
            val groupId = backStackEntry.arguments?.getString("groupId")?.toIntOrNull()
            // Screen for settling balance within the group
            SettleGroupBalanceScreen(navController = navController, userViewmodel, expenseViewmodel, groupId)
        }
        composable("settleBalanceDetail/{expenseId}/{owedAmount}") { backStackEntry ->
            // Extract the expense ID from the backStackEntry
            val expenseId = backStackEntry.arguments?.getString("expenseId")?.toIntOrNull()
            val owedAmount = backStackEntry.arguments?.getString("owedAmount")?.toDoubleOrNull()
            // Screen displaying details of settled balances
            SettleBalancesDetailsScreen(
                navController = navController,
                expenseId,
                expenseViewmodel,
                userViewmodel,
                owedAmount,
                paymentViewmodel
            )
        }
        composable("group/{groupId}") { backStackEntry ->
            // Extract the group ID from the backStackEntry
            val groupId = backStackEntry.arguments?.getString("groupId")?.toIntOrNull()
            if (groupId != null) {
                GroupExpenseScreen(
                    groupId,
                    navController,
                    groupViewmodel,
                    userViewmodel,
                    expenseViewmodel,
                    onExpenseEvent,
                )
            } else {
                Toast.makeText(LocalContext.current, "Invalid group ID", Toast.LENGTH_SHORT).show()
            }
        }
        composable("addExpense/{groupId}") { backStackEntry ->
            // Screen for adding new expenses
            val groupId = backStackEntry.arguments?.getString("groupId")?.toIntOrNull()
            if (groupId != null) {
                AddExpenseScreen(
                    groupId,
                    navController,
                    expenseState,
                    expenseViewmodel::onEvent,
                    userViewmodel,
                    expenseViewmodel,
                    groupViewmodel,
                    context
                )
            } else {
                Toast.makeText(LocalContext.current, "Invalid group ID", Toast.LENGTH_SHORT).show()
            }
        }
        composable("editExpense/{groupId}/{expenseId}") {backStackEntry ->
            // Screen for editing expenses
            val groupId = backStackEntry.arguments?.getString("groupId")?.toIntOrNull()
            val expenseId = backStackEntry.arguments?.getString("expenseId")?.toIntOrNull()
            if (groupId != null && expenseId != null) {
                EditExpenseScreen(
                    groupId,
                    navController,
                    expenseState,
                    expenseViewmodel::onEvent,
                    userViewmodel,
                    expenseViewmodel,
                    groupViewmodel,
                    expenseId
                )
            }
        }
        composable("createGroup") {
            // Screen for creating new groups
            AddGroupScreen(
                navController = navController,
                state = groupState,
                onEvent = groupViewmodel::onEvent,
                userViewmodel,
                groupViewmodel,
                contactEmails
            )
        }
        composable("editGroup/{groupId}") { backStackEntry ->
            val groupId = backStackEntry.arguments?.getString("groupId")?.toIntOrNull()
            if (groupId != null) {
                // Screen for editing groups
                EditGroupScreen(navController, groupViewmodel, groupId)
            } else {
                Toast.makeText(LocalContext.current, "Invalid group ID", Toast.LENGTH_SHORT).show()
            }
        }
        composable("chartScreen") {
            // Screen for displaying expense charts
            ExpenseChartScreen(navController = navController)
        }
        composable("successScreen/{name}") { backStackEntry ->
            val name = backStackEntry.arguments?.getString("name")
            // Success screen for completed actions
            if (name != null) {
                SuccessScreen(navController = navController, name)
            }
        }
        composable("manageGroupMembers/{groupId}") { backStackEntry ->
            val groupId = backStackEntry.arguments?.getString("groupId")?.toIntOrNull()
            if (groupId != null) {
                // Screen for managing group members
                ManageGroupMembersScreen(navController, groupViewmodel, groupId, userViewmodel, contactEmails)
            } else {
                Toast.makeText(LocalContext.current, "Invalid group ID", Toast.LENGTH_SHORT).show()
            }
        }
        composable("map/{address}", arguments = listOf(navArgument("address") { type = NavType.StringType })) { backStackEntry ->
            val context = LocalContext.current
            backStackEntry.arguments?.getString("address")?.let { address ->

                val coordinates = geocodeAddress(context, address)
                coordinates?.let {
                    // Screen for displaying map
                    GoogleMapClustering(it.latitude, it.longitude, navController)
                }
            }
        }
    }
}
