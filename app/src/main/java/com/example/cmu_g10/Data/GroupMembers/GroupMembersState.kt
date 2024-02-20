package com.example.cmu_g10.Data.GroupMembers

/**
 * State representing the groupMembers.
 */
data class GroupMembersState(
    val userGroupId: Int = 0,
    val groupMembers: List<GroupMembers> = emptyList(),
    var userId: Int = 0,
    var groupId: Int = 0,
)