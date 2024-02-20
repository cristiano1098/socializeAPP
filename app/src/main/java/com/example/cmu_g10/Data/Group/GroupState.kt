package com.example.cmu_g10.Data.Group

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

/**
 * Data class representing the state of groups within the application.
 *
 * @property groups A list of groups currently in the state. Defaults to an empty list.
 * @property name A mutable state for the name associated with the group. Defaults to an empty string.
 */
data class GroupState(
    val groups: List<Group> = emptyList(),
    var name: MutableState<String> = mutableStateOf(""),
)