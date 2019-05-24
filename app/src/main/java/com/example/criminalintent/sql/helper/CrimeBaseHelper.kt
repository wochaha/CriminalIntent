package com.example.criminalintent.sql.helper

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Parcel
import android.os.Parcelable
import com.example.criminalintent.model.CrimeDbSchema

class CrimeBaseHelper(context: Context?, name: String = DATABASE_NAME, factory: SQLiteDatabase.CursorFactory? = null, version: Int = VERSION) :
    SQLiteOpenHelper(context, name, factory, version) {

    companion object{
        private const val VERSION = 1
        private const val DATABASE_NAME = "crimeBase.db"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("create table ${CrimeDbSchema.CrimeTable.NAME} (" +
                "_id integer primary key autoincrement," +
                "${CrimeDbSchema.CrimeTable.Cols.UUID}," +
                "${CrimeDbSchema.CrimeTable.Cols.DATE}," +
                "${CrimeDbSchema.CrimeTable.Cols.TITLE}," +
                "${CrimeDbSchema.CrimeTable.Cols.SOLVED}," +
                "${CrimeDbSchema.CrimeTable.Cols.SUSPECT})")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }

}