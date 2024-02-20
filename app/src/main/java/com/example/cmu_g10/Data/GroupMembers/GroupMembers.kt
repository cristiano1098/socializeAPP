package com.example.cmu_g10.Data.GroupMembers

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Data class that represents groupMembers.
 *
 * @param memberId The id of the groupMember.
 * @param userId The id of the user.
 * @param groupId The id of the group.
 */
@Entity(primaryKeys = ["userId", "groupId"])
data class GroupMembers(
    val userId: Int,
    val groupId: Int
)
