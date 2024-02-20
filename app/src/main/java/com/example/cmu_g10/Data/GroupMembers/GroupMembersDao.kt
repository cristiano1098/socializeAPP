package com.example.cmu_g10.Data.GroupMembers

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface GroupMembersDao {
    /*
     * Insert a groupMembers in the database. If the groupMembers already exists, replace it.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(groupMembers: GroupMembers)

    /*
     * Delete a groupMembers from the database.
     */
    @Delete
    suspend fun delete(groupMembers: GroupMembers)

    /*
     * Delete groupMember from the database.
     */
    @Query("DELETE FROM GroupMembers WHERE userId = :userId AND groupId = :groupId")
    suspend fun deleteByUserIdAndGroupId(userId: Int, groupId: Int)
}