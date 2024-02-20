package com.example.cmu_g10.Data.User

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a user entity in the Room database.
 *
 * @property userId The unique identifier for the user. Auto-generated using Room database.
 * @property name The name of the user.
 * @property email The email address of the user.
 * @property phone The phone number of the user.
 * @property photo The photo URL or path associated with the user.
 * @property balance The balance or financial information associated with the user.
 */
@Entity
data class User(
    @PrimaryKey(autoGenerate = true) val userId: Int = 0,
    var name: String,
    val email: String,
    val phone: String,
    val photo: String,
    val balance: Double,
)

/**
 * Converts a list of group IDs to a JSON string.
 */
class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromGroupIdsList(groupIds: List<Int>?): String {
        return gson.toJson(groupIds)
    }

    @TypeConverter
    fun toGroupIdsList(groupIdsString: String?): List<Int> {
        if (groupIdsString == null) return listOf()
        val type = object : TypeToken<List<Int>>() {}.type
        return gson.fromJson(groupIdsString, type)
    }
}
