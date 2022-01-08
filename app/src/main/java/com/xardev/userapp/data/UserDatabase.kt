package com.xardev.userapp.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.xardev.userapp.data.local.UserDao

@Database(entities = [User::class], version = 1, exportSchema = false)
 abstract class UserDatabase : RoomDatabase() {
    abstract fun userDao() : UserDao
}