package com.example.cmu_g10.Data.Group

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Data class that represents a group.
 *
 * @param groupId The id of the group.
 * @param name The name of the group.
 * @param dateAdded The date the group was added.
 */
@Entity
data class Group(
    @PrimaryKey(autoGenerate = true) val groupId: Int = 0,
    var name: String,
    val dateAdded: Long
)
