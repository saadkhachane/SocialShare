package com.xardev.userapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.xardev.userapp.data.local.entity.SocialProfileEntity
import com.xardev.userapp.data.local.entity.UserEntity
import com.xardev.userapp.domain.model.User

@Database(entities = [UserEntity::class, SocialProfileEntity::class], version = 6, exportSchema = false)
 abstract class UserDatabase : RoomDatabase() {
    abstract val userDao : UserDao
}