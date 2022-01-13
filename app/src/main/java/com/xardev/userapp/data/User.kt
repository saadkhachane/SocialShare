package com.xardev.userapp.data

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "User")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Long?,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "email") var email: String

) : Parcelable {

    @ColumnInfo(name = "phone") var phone: String? = null
    @ColumnInfo(name = "work") var work: String? = null
    @ColumnInfo(name = "bio") var bio: String? = null
    @ColumnInfo(name = "img_url") var img_url: String? = null
    @ColumnInfo(name = "sub_date") var sub_date: String? = null
    @ColumnInfo(name = "facebook") var facebook: String? = null
    @ColumnInfo(name = "instagram") var instagram: String? = null
    @ColumnInfo(name = "snapchat") var snapchat: String? = null
    @ColumnInfo(name = "twitter") var twitter: String? = null
    @ColumnInfo(name = "linkedin") var linkedin: String? = null
    @ColumnInfo(name = "behance") var behance: String? = null
    @ColumnInfo(name = "qr_url") var qr_url: String? = null

    constructor(parcel: Parcel) : this(
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readString()!!,
        parcel.readString()!!
    ) {
        phone = parcel.readString()
        bio = parcel.readString()
        img_url = parcel.readString()
        sub_date = parcel.readString()
        facebook = parcel.readString()
        instagram = parcel.readString()
        snapchat = parcel.readString()
        twitter = parcel.readString()
        linkedin = parcel.readString()
        behance = parcel.readString()
        qr_url = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(id)
        parcel.writeString(name)
        parcel.writeString(email)
        parcel.writeString(phone)
        parcel.writeString(bio)
        parcel.writeString(img_url)
        parcel.writeString(sub_date)
        parcel.writeString(facebook)
        parcel.writeString(instagram)
        parcel.writeString(snapchat)
        parcel.writeString(twitter)
        parcel.writeString(linkedin)
        parcel.writeString(behance)
        parcel.writeString(qr_url)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }

}