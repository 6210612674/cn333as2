package com.example.mynotes.models

import android.os.Parcel
import android.os.Parcelable

class TaskList(val name: String, val tasks: ArrayList<String> = ArrayList()) : Parcelable {
    // create constructor
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,  // ใส่ !! เพื่อไม่เป็น null แน่นอน
        parcel.createStringArrayList()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeStringList(tasks)
    }

    override fun describeContents() = 0

    // create static method
    companion object CREATOR : Parcelable.Creator<TaskList> {
        // override from Parcelable.Creator<TaskList>
        override fun createFromParcel(parcel: Parcel): TaskList {
            return TaskList(parcel)
        }

        override fun newArray(size: Int): Array<TaskList?> {
            return arrayOfNulls(size)
        }
    }
}