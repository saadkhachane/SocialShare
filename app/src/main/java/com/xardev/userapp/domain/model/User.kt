package com.xardev.userapp.domain.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.xardev.userapp.data.local.entity.UserEntity


data class User(
    var id: String,
    var name: String,
    var email: String

) : Parcelable {

    var phone: String? = null
    var work: String? = null
    var bio: String? = null
    var img: String? = null
    var sub_date: String? = null
    var qr_url: String? = null
    var enabled: Boolean? = true

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
        enabled = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
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
        parcel.writeValue(enabled)
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

    fun toUserEntity() : UserEntity =
        UserEntity(
            id = id,
            name = name,
            email = email,
            phone = phone,
            work = work,
            bio = bio,
            img = img,
            sub_date = sub_date,
            qr_url = qr_url
        )

}