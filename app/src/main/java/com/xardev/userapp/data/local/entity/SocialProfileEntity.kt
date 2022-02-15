package com.xardev.userapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.xardev.userapp.domain.model.SocialProfile

@Entity(tableName = "social_profile")
data class SocialProfileEntity(
    @PrimaryKey val id : String,
    val title : String,
    val icon : String,
    var profile : String? = null
) {

    fun toSocialProfile() : SocialProfile =
        SocialProfile(
            id,
            title,
            icon,
            profile
        )

}
