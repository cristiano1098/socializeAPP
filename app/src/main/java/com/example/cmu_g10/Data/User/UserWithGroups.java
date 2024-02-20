package com.example.cmu_g10.Data.User;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import com.example.cmu_g10.Data.Group.Group;
import com.example.cmu_g10.Data.GroupMembers.GroupMembers;

import java.util.List;

// This is the UserWithGroups class that is used to get the user with groups
public class UserWithGroups {
    @Embedded
    public User user;

    @Relation(
            parentColumn = "userId",
            entityColumn = "groupId",
            associateBy = @Junction(GroupMembers.class)
    )
    public List<Group> groups;
}
