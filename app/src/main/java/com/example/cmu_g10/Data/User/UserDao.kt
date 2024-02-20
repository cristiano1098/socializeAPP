package com.example.cmu_g10.Data.User

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.cmu_g10.Data.GroupMembers.GroupMembers

/**
 * Data Access Object (DAO) interface for performing CRUD operations on the "User" entity in the Room database.
 */
@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: User): Long

    @Update
    fun update(user: User): Int

    //getting user by id
    @Query("SELECT * FROM user WHERE userId = :userId")
    fun getUser(userId: Int): User

    //getting user by id
    @Query("SELECT * FROM user WHERE userId = :userId")
    fun getUserById(userId: Int): LiveData<User>

    //get all users
    @Query("SELECT * FROM user")
    fun getAllUsers(): List<User>

    //getUserByEmail
    @Query("SELECT * FROM user WHERE email = :email")
    fun getUserByEmail(email: String): User

    @Insert
    fun insertUserGroup(groupMembers: GroupMembers)

    @Transaction
    @Query("SELECT * FROM User WHERE userId = :userId")
    fun getUserWithGroups(userId: Int): UserWithGroups
}