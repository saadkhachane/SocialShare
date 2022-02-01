package com.xardev.userapp.data

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class User(
    @PrimaryKey @ColumnInfo(name = "id") var id: String,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "email") var email: String

) : Parcelable {

    @ColumnInfo(name = "phone") var phone: String? = null
    @ColumnInfo(name = "work") var work: String? = null
    @ColumnInfo(name = "bio") var bio: String? = null
    @ColumnInfo(name = "img") var img: String? = null
    @ColumnInfo(name = "sub_date") var sub_date: String? = null
    @ColumnInfo(name = "qr_url") var qr_url: String? = null
    @ColumnInfo(name = "enabled") var enabled: String? = null

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    ) {
        phone = parcel.readString()
        work = parcel.readString()
        bio = parcel.readString()
        img = parcel.readString()
        sub_date = parcel.readString()
        qr_url = parcel.readString()
        enabled = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(email)
        parcel.writeString(phone)
        parcel.writeString(work)
        parcel.writeString(bio)
        parcel.writeString(img)
        parcel.writeString(sub_date)
        parcel.writeString(qr_url)
        parcel.writeString(enabled)
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