package com.example.cmu_g10.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.cmu_g10.Services.Camara.CamaraActivity
import com.example.cmu_g10.Services.AutoComplete.GeoapifyApiService
import com.example.cmu_g10.Data.Expense.ExpenseViewModel
import com.example.cmu_g10.Data.Group.GroupViewModel
import com.example.cmu_g10.Services.Notifications.NOTIFICATION_CHANNEL_ID
import com.example.cmu_g10.Services.Notifications.NOTIFICATION_CHANNEL_NAME
import com.example.cmu_g10.Data.Payment.PaymentViewModel
import com.example.cmu_g10.Data.SocializeDatabase
import com.example.cmu_g10.Data.User.UserViewModel
import com.example.cmu_g10.ui.theme.CMU_G10Theme
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Main activity class for the CMU_G10 application.
 */
class  MainActivity : ComponentActivity() {

    // Initialize the SocializeDatabase instance
    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            SocializeDatabase::class.java,
            "socialize.db"
        ).fallbackToDestructiveMigration().build()
    }

    //Initialize the GeoapifyApiService using Retrofit
    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.geoapify.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val geoapifyApi = retrofit.create(GeoapifyApiService::class.java)

    // Initialize the GroupViewModel
    private val groupViewmodel by viewModels<GroupViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return GroupViewModel(db.groupDao, db.groupMembersDao) as T
                }
            }
        }
    )

    // Initialize the UserViewModel
    private val userViewmodel by viewModels<UserViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return UserViewModel(db.userDao) as T
                }
            }
        }
    )

    //Initialize the ExpenseViewModel
    private val expenseViewmodel by viewModels<ExpenseViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ExpenseViewModel(db.expenseDao, db.expenseParticipantsDao, geoapifyApi) as T
                }
            }
        }
    )

    //Initialize the PaymentViewModel
    private val paymentViewmodel by viewModels<PaymentViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return PaymentViewModel(db.paymentDao) as T
                }
            }
        }
    )

    /**
     * Function to load the email addresses from the device's contacts.
     *
     * @return A list of email addresses.
     */
    @SuppressLint("Range")
    private fun loadEmailAddresses(): List<String> {
        val emailsList = mutableListOf<String>()
        val contentResolver = contentResolver
        val uri = ContactsContract.CommonDataKinds.Email.CONTENT_URI
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Email.ADDRESS,
            ContactsContract.CommonDataKinds.Email.DISPLAY_NAME
        )

        val cursor: Cursor? = contentResolver.query(uri, projection, null, null, null)

        cursor?.use {
            while (it.moveToNext()) {
                val email = it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS))
                val displayName = it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Email.DISPLAY_NAME))
                emailsList.add(email)
            }
        }
        return emailsList
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Create Notification Channel
        val notificationManager: NotificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationChannel = NotificationChannel (
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(notificationChannel)

        setContent {
            // Remember the dark mode state using a mutableStateOf
            val darkmode by remember { mutableStateOf(true) }

            // Apply the CMU_G10Theme with the provided dark mode state
            CMU_G10Theme(
                darkTheme = darkmode
            ) {
                // Collect the state from the GroupViewModel as a Compose state
                val groupState by groupViewmodel.state.collectAsState()
                val userState by userViewmodel.state.collectAsState()
                val expenseState by expenseViewmodel.state.collectAsState()
                val paymentState by paymentViewmodel.state.collectAsState()
                val context = this

                // Request permission to read contacts
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(context,
                        arrayOf(Manifest.permission.READ_CONTACTS),
                        1)
                }

                val contactEmails = loadEmailAddresses()

                // Display the navigation using NavHost
                Nav(
                    groupState = groupState,
                    userState = userState,
                    expenseState = expenseState,
                    paymentState = paymentState,
                    onGroupEvent = groupViewmodel::onEvent,
                    onUserEvent = userViewmodel::onEvent,
                    onExpenseEvent = expenseViewmodel::onEvent,
                    groupViewmodel = groupViewmodel,
                    userViewmodel = userViewmodel,
                    expenseViewmodel = expenseViewmodel,
                    paymentViewmodel = paymentViewmodel,
                    contactEmails = contactEmails,
                    context = context
                )
            }
        }
    }
}