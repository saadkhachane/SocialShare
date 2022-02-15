package com.xardev.userapp.data.remote.dto

import com.xardev.userapp.domain.model.SocialProfile
import com.xardev.userapp.domain.model.User

data class UserDto(
    val id: String,
    val name: String,
    val email: String,
    val work: String,
    val bio: String,
    val phone: String,
    val img: String,
    val qr_url: String,
    val sub_date: String,
    val enabled: String,
    val social_profile: List<SocialProfile>
){

    fun toUser() : User {
        return User(
            id, name, email
        ).apply {
            work = this@UserDto.work
            bio = this@UserDto.bio
            phone = this@UserDto.phone
            img = this@UserDto.img
            qr_url = this@UserDto.qr_url
            sub_date = this@UserDto.sub_date
            enabled = this@UserDto.enabled.toBoolean()
            socialProfileList = this@UserDto.social_profile
        }
    }


}