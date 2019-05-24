package com.example.criminalintent.model

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.criminalintent.sql.helper.CrimeBaseHelper
import java.util.*
import com.example.criminalintent.model.CrimeDbSchema.CrimeTable
import com.example.criminalintent.sql.cursor.CrimeCursorWrapper
import java.io.File
import kotlin.collections.ArrayList

class CrimeLab private constructor(_context: Context){
    val mCrimes = arrayListOf<Crime>()
    private var context:Context = _context.applicationContext
    private val mDataBase:SQLiteDatabase = CrimeBaseHelper(context).writableDatabase

    fun addCrime(c:Crime){
        val values = getContentValues(c)
        mDataBase.insert(CrimeTable.NAME,null,values)
    }

    fun getCrime(id:UUID): Crime? {
        val wrapper = queryCrimes("${CrimeTable.Cols.UUID} = ?", arrayOf(id.toString()))
        wrapper.use { wrapper->
            if (wrapper.count == 0){
                return null
            }
            wrapper.moveToFirst()
            return wrapper.getCrime()
        }
    }

    fun getCrimes():ArrayList<Crime>{
        mCrimes.clear() //避免重复加载
        val wrapper = queryCrimes(null,null)
        wrapper.use { wrapper ->
            wrapper.moveToFirst()
            while (!wrapper.isAfterLast){
                mCrimes.add(wrapper.getCrime())
                wrapper.moveToNext()
            }
        }
        Log.d(TAG,mCrimes.size.toString())
        return mCrimes
    }

    fun updateCrime(crime: Crime){
        val uuidString = crime.mId.toString()
        val values = getContentValues(crime)
        mDataBase.update(CrimeTable.NAME,values,"${CrimeTable.Cols.UUID} = ?", arrayOf(uuidString))
        mCrimes.clear()
        getCrimes()
    }

    fun getCrimePosition(id:UUID) :Int?{
        for (i in 0..mCrimes.size){
            if (id == mCrimes[i].mId) return i
        }
        return null
    }

    fun deleteCrime(crime: Crime){
        mDataBase.delete(CrimeTable.NAME,"${CrimeTable.Cols.UUID} = ?", arrayOf(crime.mId.toString()))
        mCrimes.clear()
        getCrimes()
    }

    fun getPhotoFile(crime: Crime) : File{
        val filesDir = context.filesDir
        return File(filesDir,crime.getPhotoFilename())
    }

    private fun queryCrimes(whereClause : String?,whereArgs:Array<String>?): CrimeCursorWrapper {
        val cursor = mDataBase.query(CrimeTable.NAME,
            null,
            whereClause,
            whereArgs,
            null,
            null,
            null)
        return CrimeCursorWrapper(cursor)
    }

    companion object{
        private const val TAG = "Crimes's size"

        @SuppressLint("StaticFieldLeak")
        var instance : CrimeLab? = null
        fun get(context: Context):CrimeLab{
            if (instance == null){
                instance = CrimeLab(context)
            }
            return instance as CrimeLab
        }
        private fun getContentValues(crime: Crime):ContentValues{
            val values = ContentValues()
            values.put(CrimeTable.Cols.UUID,crime.mId.toString())
            values.put(CrimeTable.Cols.TITLE,crime.mTitle)
            values.put(CrimeTable.Cols.DATE,crime.mDate.time)
            values.put(CrimeTable.Cols.SOLVED,if (crime.mSolved) 1 else 0)
            values.put(CrimeTable.Cols.SUSPECT, crime.mSuspect)
            return values
        }
    }
}