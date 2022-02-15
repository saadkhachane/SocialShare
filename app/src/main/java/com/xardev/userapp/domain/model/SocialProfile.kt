package com.xardev.userapp.domain.model

import com.xardev.userapp.data.local.entity.SocialProfileEntity

data class SocialProfile(
    val id: String,
    val title: String,
    val icon: String,
    var profile: String?
) {

    fun toSocialProfileEntity() : SocialProfileEntity =
        SocialProfileEntity(
            id, title, icon, profile
        )
}