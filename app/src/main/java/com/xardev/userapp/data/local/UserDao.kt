package com.xardev.userapp.data.local

import androidx.room.*
import com.xardev.userapp.data.local.entity.SocialProfileEntity
import com.xardev.userapp.data.local.entity.UserEntity
import com.xardev.userapp.domain.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Query("SELECT * FROM User")
    fun getUsers() : Flow<List<UserEntity>>

    @Query("SELECT * FROM User WHERE User.id == :id LIMIT 1")
    fun getUserById(id: String) : UserEntity

    @Query("SELECT * FROM User WHERE User.email == :email LIMIT 1")
    fun getUserByEmail(email: String) : UserEntity

    @Query("SELECT COUNT(*) FROM User WHERE User.email == :email")
    fun userExists(email: String) : Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Delete
    suspend fun deleteUser(user: UserEntity)


    @Query("SELECT * FROM social_profile")
    fun getSocialProfiles() : List<SocialProfileEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addSocialProfiles(sp: List<SocialProfileEntity>)

    @Update
    suspend fun updateSocialProfiles(sp: List<SocialProfileEntity>)

    @Delete
    suspend fun deleteSocialProfile(sp: List<SocialProfileEntity>)

}