package com.xardev.userapp.data.local.entity

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class UserEntity(
    @PrimaryKey @ColumnInfo(name = "id") var id: String,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "email") var email: String,
    @ColumnInfo(name = "phone") var phone: String? = null,
    @ColumnInfo(name = "work") var work: String? = null,
    @ColumnInfo(name = "bio") var bio: String? = null,
    @ColumnInfo(name = "img") var img: String? = null,
    @ColumnInfo(name = "sub_date") var sub_date: String? = null,
    @ColumnInfo(name = "qr_url") var qr_url: String? = null,
    @ColumnInfo(name = "enabled") var enabled: String? = null
)