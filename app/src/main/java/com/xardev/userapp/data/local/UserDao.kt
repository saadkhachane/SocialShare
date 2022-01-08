package com.xardev.userapp.data.local

import androidx.room.*
import com.xardev.userapp.data.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Query("SELECT * FROM User")
    fun getUsers() : Flow<List<User>>

    @Query("SELECT * FROM User WHERE User.id == :id LIMIT 1")
    fun getUserById(id: Long) : Flow<User>

    @Query("SELECT * FROM User WHERE User.email == :email LIMIT 1")
    fun getUserByEmail(email: String) : Flow<User>

    @Query("SELECT COUNT(*) FROM User WHERE User.email == :email")
    fun userExists(email: String) : Int

    @Insert
    suspend fun addUser(user: User)

    @Update(onConflict = OnConflictStrategy.ABORT)
    suspend fun updateUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)

}