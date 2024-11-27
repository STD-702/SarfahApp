package com.example.sarfahapp

import android.os.Parcel
import android.os.Parcelable

data class Student(
    val studentId: Int,
    val firstName: String,
    val middleName: String,
    val lastName: String,
    val className: String = "",
    val grade: String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(studentId)
        parcel.writeString(firstName)
        parcel.writeString(middleName)
        parcel.writeString(lastName)
        parcel.writeString(className)
        parcel.writeString(grade)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Student> {
        override fun createFromParcel(parcel: Parcel): Student {
            return Student(parcel)
        }

        override fun newArray(size: Int): Array<Student?> {
            return arrayOfNulls(size)
        }
    }
}