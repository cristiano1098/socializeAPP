package com.example.cmu_g10.Data.Group

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cmu_g10.Data.User.User
import com.example.cmu_g10.Data.GroupMembers.GroupMembers
import com.example.cmu_g10.Data.GroupMembers.GroupMembersDao
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel class responsible for managing the state and events related to groups in the application.
 *
 * @property dao The data access object for group-related database operations.
 * @property groupMembersDao The data access object for group members-related database operations.
 */
class GroupViewModel(
    private val dao: GroupDao,
    private val groupMembersDao: GroupMembersDao
) : ViewModel() {
    private val isSortedByDateAdded = MutableStateFlow(true)
    private val _groupDetails = MutableLiveData<Group>()
    private val _groupMembers = MutableLiveData<List<User>>()
    private val _errorState = MutableLiveData<String>()
    private val _successState = MutableLiveData<String>()
    private val dbGroup = Firebase.firestore
    private val dbGroupMembers = Firebase.firestore
    private val _state = MutableStateFlow(GroupState())
    private var groups =
        isSortedByDateAdded.flatMapLatest { sort ->
            if (sort) {
                dao.getGroupsOrderdByDateAdded()
            } else {
                dao.getGroupsOrderdByTitle()
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val userGroups = MutableLiveData<List<Group>>()
    val groupDetails: LiveData<Group> = _groupDetails
    val groupMembers: LiveData<List<User>> = _groupMembers
    val errorState: LiveData<String> = _errorState
    val successState: LiveData<String> = _successState
    val state =
        combine(_state, isSortedByDateAdded, groups) { state, isSortedByDateAdded, groups ->
            state.copy(
                groups = groups
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), GroupState())


    fun getGroupLiveData(groupId: Int): LiveData<Group> {
        return dao.getLiveGroupById(groupId)
    }

    /**
     * Function to add members to a group.
     *
     * @param groupId The ID of the group to which members are to be added.
     * @param selectedUsers The users to be added to the group.
     */
    private fun addMembersToGroup(groupId: Int, selectedUsers: List<User>) {
        viewModelScope.launch {
            try {
                selectedUsers.forEach { user ->
                    val groupMembers = GroupMembers(userId = user.userId, groupId = groupId)
                    groupMembersDao.insert(groupMembers)

                    //insert into firestone
                    val groupMembersMap = hashMapOf(
                        "userId" to user.userId,
                        "groupId" to groupId
                    )

                    dbGroupMembers.collection("groupMembers").document(user.userId.toString())
                        .set(groupMembersMap)
                        .addOnSuccessListener { documentReference ->
                            println("DocumentSnapshot added with ID: ${documentReference}")
                        }
                        .addOnFailureListener { e ->
                            println("Error adding document: $e")
                        }
                }
                // Update the group members LiveData
                groupMembers.value?.let {
                    _groupMembers.postValue(it + selectedUsers)
                }
            } catch (e: Exception) {
                _errorState.postValue("Error adding members to group")
            }
        }
    }

    /**
     * Function to get updated group Details.
     *
     * @param groupId The ID of the group.
     */
    private suspend fun getUpdatedGroupDetails(groupId: Int): Group {
        return dao.getGroupById(groupId)
    }

    /**
     * Function to get updated group members.
     *
     * @param groupId The ID of the group.
     */
    fun fetchGroupMembers(groupId: Int) {
        viewModelScope.launch {
            try {
                val members = dao.getGroupMembers(groupId)
                _groupMembers.postValue(members)
            } catch (e: Exception) {
                _errorState.postValue("Error fetching group members")
            }
        }
    }

    /**
     * Function to get groups for a user.
     *
     * @param userId The ID of the user.
     */
    fun fetchGroupsForUser(userId: Int) {
        try {
            viewModelScope.launch {
                userGroups.value = dao.getGroupsForUser(userId)
            }
        } catch (e: Exception) {
            _errorState.postValue("Error fetching groups")
        }
    }

    /**
     * Function to get group details.
     *
     * @param groupId The ID of the group.
     */
    fun fetchGroupDetails(groupId: Int) {
        viewModelScope.launch {
            try {
                val group = dao.getGroupById(groupId)
                _groupDetails.value = group
            } catch (e: Exception) {
                _errorState.postValue("Error fetching group details")
            }
        }
    }

    /**
     * Function to update a group name.
     *
     * @param groupId The ID of the group.
     * @param newName The new name of the group.
     */
    fun updateGroupName(groupId: Int, newName: String) {
        viewModelScope.launch {
            try {
                // Update in local database
                val group = dao.getGroupById(groupId)
                group.name = newName
                dao.update(group)

                // Prepare data for Firestore update
                val groupMap = hashMapOf(
                    "name" to newName,
                    "dateAdded" to group.dateAdded
                )

                // Update in Firestore
                dbGroup.collection("groups").document(groupId.toString())
                    .update(groupMap as Map<String, Any>)
                    .addOnSuccessListener {
                        _successState.postValue("Group name updated successfully")
                    }
                    .addOnFailureListener { e ->
                        _errorState.postValue("Error updating group name in Firestore: ${e.message}")
                    }

            } catch (e: Exception) {
                _errorState.postValue("Error updating group name: ${e.message}")
            }
        }
    }

    /**
     * Function to remove a member from a group.
     *
     * @param groupId The ID of the group.
     * @param userId The ID of the user.
     */
    private fun removeMemberFromGroup(groupId: Int, userId: Int) {
        viewModelScope.launch {
            try {
                groupMembersDao.deleteByUserIdAndGroupId(userId, groupId)

                //delete from firestone
                dbGroupMembers.collection("groupMembers").document(userId.toString())
                    .delete()
                    .addOnSuccessListener { println("DocumentSnapshot successfully deleted!") }
                    .addOnFailureListener { e -> println("Error deleting document: $e") }
            } catch (e: Exception) {
                _errorState.postValue("Error removing member from group")
            }
        }
    }

    /**
     * Function to handle events related to groups.
     *
     * @param event The group event to be handled.
     */
    fun onEvent(event: GroupEvent) {
        when (event) {
            is GroupEvent.SaveGroup -> {
                viewModelScope.launch {
                    try {
                        val group = Group(
                            name = state.value.name.value,
                            dateAdded = System.currentTimeMillis()
                        )
                        //get the group id
                        val groupId = dao.insert(group).toInt()
                        //insert into firestone
                        val groupMap = hashMapOf(
                            "name" to group.name,
                            "dateAdded" to group.dateAdded
                        )

                        dbGroup.collection("groups").document(groupId.toString())
                            .set(groupMap)
                            .addOnSuccessListener { documentReference ->
                                println("DocumentSnapshot added with ID: ${documentReference}")
                            }
                            .addOnFailureListener { e ->
                                println("Error adding document: $e")
                            }


                        // Associate each selected user with the new group
                        event.selectedUsers.forEach { user ->
                            val groupMembers = GroupMembers(userId = user.userId, groupId = groupId)
                            groupMembersDao.insert(groupMembers)

                            //insert into firestone
                            val groupMembersMap = hashMapOf(
                                "userId" to user.userId,
                                "groupId" to groupId
                            )

                            dbGroupMembers.collection("groupMembers")
                                .document(user.userId.toString())
                                .set(groupMembersMap)
                                .addOnSuccessListener { documentReference ->
                                    println("DocumentSnapshot added with ID: ${documentReference}")
                                }
                                .addOnFailureListener { e ->
                                    println("Error adding document: $e")
                                }
                        }
                        _state.update {
                            it.copy(name = mutableStateOf(""))
                        }
                    } catch (e: Exception) {
                        _errorState.postValue("Error saving group")
                    }
                }
            }

            is GroupEvent.AddMembersToGroup -> addMembersToGroup(event.groupId, event.selectedUsers)
            is GroupEvent.UpdateGroupName -> updateGroupName(event.groupId, event.newName)
            is GroupEvent.DeleteGroup -> {
                viewModelScope.launch() {
                    dbGroup.collection("groups").document(event.group.groupId.toString())
                        .delete()
                        .addOnSuccessListener { println("DocumentSnapshot successfully deleted!") }
                        .addOnFailureListener { e -> println("Error deleting document: $e") }
                    dao.delete(event.group)
                }
            }

            is GroupEvent.RemoveUserFromGroup -> {
                removeMemberFromGroup(event.groupId, event.userId)
                // remove from firestone
                dbGroupMembers.collection("groupMembers").document(event.userId.toString())
                    .delete()
                    .addOnSuccessListener { println("DocumentSnapshot successfully deleted!") }
                    .addOnFailureListener { e -> println("Error deleting document: $e") }

                groupMembers.value?.let {
                    _groupMembers.postValue(it.filter { user -> user.userId != event.userId })
                }
            }

            GroupEvent.SortGroups -> {
                isSortedByDateAdded.value = !isSortedByDateAdded.value
            }
        }
    }
}