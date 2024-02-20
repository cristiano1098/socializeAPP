package com.example.cmu_g10.Data.Group

import com.example.cmu_g10.Data.User.User

/**
 * Sealed interface representing various events related to groups.
 */
sealed interface GroupEvent {

    /**
     * Represents the event of saving a group.
     *
     * @property name The name of the group to be saved.
     * @property userId The user identifier associated with the group.
     */
    class SaveGroup(
        val name: String,
        val selectedUsers: List<User>
    ) : GroupEvent

    /**
     * Represents the event of adding members to a group.
     *
     * @property group The group to which members are to be added.
     * @property selectedUsers The users to be added to the group.
     */
    class AddMembersToGroup(val groupId: Int, val selectedUsers: List<User>) : GroupEvent

    /**
     * Represents the event of updating a group.
     *
     * @property group The group to be updated.
     */
    class UpdateGroupName(val groupId: Int, val newName: String) : GroupEvent

    /**
     * Represents the event of deleting a group.
     *
     * @property group The group to be deleted.
     */
    class DeleteGroup(val group: Group) : GroupEvent

    /**
     * Represents the event of sorting groups.
     */
    object SortGroups : GroupEvent

    /**
     * Represents the event of removing a user from a group.
     *
     * @property groupId The ID of the group.
     * @property userId The ID of the user.
     */
    class RemoveUserFromGroup(val groupId: Int, val userId: Int) : GroupEvent
}