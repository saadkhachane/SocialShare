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
    var email: String,
    var phone: String? = null,
    var work: String? = null,
    var bio: String? = null,
    var img: String? = null,
    var sub_date: String? = null,
    var qr_url: String? = null,
    var enabled: Boolean? = true,
    var socialProfileList: List<SocialProfile> = emptyList()

) : Parcelable {


    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        TODO("socialProfileList")
    ) {
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
            qr_url = qr_url,
            enabled = enabled.toString()
        )

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

}