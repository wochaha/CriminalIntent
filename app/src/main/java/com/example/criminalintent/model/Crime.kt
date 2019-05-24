package com.example.criminalintent.model

import java.util.*

class Crime(_id:UUID = UUID.randomUUID()) {
    var mRequiresPolice:Boolean = false
    val mId:UUID = _id
    var mDate:Date = Date()
    var mSolved:Boolean = false
    var mTitle:String? = " "
    var mSuspect:String? = null

    override fun equals(other: Any?): Boolean {
        return this.mId == (other as Crime).mId
    }

    fun getPhotoFilename(): String {
        return "IMG_$mId.jpg"
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}