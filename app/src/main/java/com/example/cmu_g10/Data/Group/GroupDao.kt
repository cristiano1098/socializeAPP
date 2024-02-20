package com.example.cmu_g10.Data.Group

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import com.example.cmu_g10.Data.User.User
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) interface for performing CRUD operations on the "Group" entity in the Room database.
 */
@Dao
interface GroupDao {

    /**
     * Inserts or updates a group in the database.
     *
     * @param group The group to be inserted or updated.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(group: Group): Long

    /**
     * Updates a group in the database.
     *
     * @param group The group to be updated.
     */
    @Update
    suspend fun update(group: Group)

    /**
     * Deletes a group from the database.
     *
     * @param group The group to be deleted.
     */
    @Delete
    suspend fun delete(group: Group)

    /**
     * Retrieves all groups from the database, ordered by dateAdded in ascending order.
     *
     * @return A flow of lists containing groups ordered by dateAdded.
     */
    @Query("SELECT * FROM `group` ORDER BY dateAdded")
    fun getGroupsOrderdByDateAdded(): Flow<List<Group>>

    /**
     * Retrieves all groups from the database, ordered by name in ascending order.
     *
     * @return A flow of lists containing groups ordered by name.
     */
    @Query("SELECT * FROM `group` ORDER BY name ASC")
    fun getGroupsOrderdByTitle(): Flow<List<Group>>

    /**
     * Gets all groups that a user is a member of.
     *
     * @param userId The ID of the user.
     */
    @Transaction
    @Query("SELECT * FROM `group` INNER JOIN GroupMembers ON `group`.groupId = GroupMembers.groupId WHERE GroupMembers.userId = :userId")
    suspend fun getGroupsForUser(userId: Int): List<Group>

    /**
     * Retrieves a group from the database by its ID.
     *
     * @param groupId The ID of the group.
     * @return The group with the specified ID.
     */
    @Query("SELECT * FROM `group` WHERE groupId = :groupId")
    suspend fun getGroupById(groupId: Int): Group

    /**
     * Retrieves a group from the database by its ID.
     *
     * @param groupId The ID of the group.
     * @return The group with the specified ID.
     */
    @Query("SELECT * FROM `group` WHERE groupId = :groupId")
    abstract fun getLiveGroupById(groupId: Int): LiveData<Group>

    /**
     * Retrieves all members of a specific group.
     *
     * @param groupId The ID of the group.
     * @return A list of users who are members of the specified group.
     */
    @Transaction
    @Query("SELECT User.* FROM User INNER JOIN GroupMembers ON User.userId = GroupMembers.userId WHERE GroupMembers.groupId = :groupId")
    suspend fun getGroupMembers(groupId: Int): List<User>
}