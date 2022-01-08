package com.xardev.userapp.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "User")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Long?,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "email") var email: String

) {

    @ColumnInfo(name = "phone") var phone: String? = null
    @ColumnInfo(name = "work") var bio: String? = null
    @ColumnInfo(name = "img_url") var img_url: String? = null
    @ColumnInfo(name = "sub_date") var sub_date: String? = null
    @ColumnInfo(name = "facebook") var facebook: String? = null
    @ColumnInfo(name = "instagram") var instagram: String? = null
    @ColumnInfo(name = "snapchat") var snapchat: String? = null
    @ColumnInfo(name = "twitter") var twitter: String? = null
    @ColumnInfo(name = "linkedin") var linkedin: String? = null
    @ColumnInfo(name = "behance") var behance: String? = null
    @ColumnInfo(name = "qr_url") var qr_url: String? = null

}