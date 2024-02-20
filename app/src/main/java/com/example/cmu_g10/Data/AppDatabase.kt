package com.example.cmu_g10.Data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.cmu_g10.Data.Expense.Expense
import com.example.cmu_g10.Data.Expense.ExpenseDao
import com.example.cmu_g10.Data.ExpenseParticipants.ExpenseParticipants
import com.example.cmu_g10.Data.ExpenseParticipants.ExpenseParticipantsDao
import com.example.cmu_g10.Data.Group.Group
import com.example.cmu_g10.Data.Group.GroupDao
import com.example.cmu_g10.Data.User.Converters
import com.example.cmu_g10.Data.User.User
import com.example.cmu_g10.Data.User.UserDao
import com.example.cmu_g10.Data.GroupMembers.GroupMembers
import com.example.cmu_g10.Data.GroupMembers.GroupMembersDao
import com.example.cmu_g10.Data.Payment.Payment
import com.example.cmu_g10.Data.Payment.PaymentDao

/**
 * Room database class representing the Socialize database, including entities and their corresponding DAOs.
 *
 * @property userDao The Data Access Object (DAO) for User entities.
 * @property groupDao The Data Access Object (DAO) for Group entities.
 * @property groupMembersDao The Data Access Object (DAO) for GroupMembers entities.
 * @property expenseDao The Data Access Object (DAO) for Expense entities.
 * @property expenseParticipantsDao The Data Access Object (DAO) for ExpenseParticipants entities.
 * @property paymentDao The Data Access Object (DAO) for Payment entities.
 */
@Database(
    entities = [
        User::class,
        Group::class,
        GroupMembers::class,
        Expense::class,
        ExpenseParticipants::class,
        Payment::class
    ],
    version = 22,
)
@TypeConverters(Converters::class)
abstract class SocializeDatabase : RoomDatabase() {

    /**
     * Provides access to the UserDao for performing operations on User entities.
     */
    abstract val userDao: UserDao

    /**
     * Provides access to the GroupDao for performing operations on Group entities.
     */
    abstract val groupDao: GroupDao

    /**
     * Provides access to the GroupMembersDao for performing operations on GroupMembers entities.
     */
    abstract val groupMembersDao: GroupMembersDao

    /**
     * Provides access to the ExpenseDao for performing operations on Expense entities.
     */
    abstract val expenseDao: ExpenseDao

    /**
     * Provides access to the ExpenseParticipantsDao for performing operations on ExpenseParticipants entities.
     */
    abstract val expenseParticipantsDao: ExpenseParticipantsDao

    /**
     * Provides access to the PaymentDao for performing operations on Payment entities.
     */
    abstract val paymentDao: PaymentDao
}