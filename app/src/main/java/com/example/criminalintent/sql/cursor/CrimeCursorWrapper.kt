package com.example.criminalintent.sql.cursor

import android.database.Cursor
import android.database.CursorWrapper
import com.example.criminalintent.model.Crime
import com.example.criminalintent.model.CrimeDbSchema.CrimeTable
import java.util.*


class CrimeCursorWrapper(cursor: Cursor?) : CursorWrapper(cursor) {
    fun getCrime(): Crime {
        val uuidString = getString(getColumnIndex(CrimeTable.Cols.UUID))
        val title = getString(getColumnIndex(CrimeTable.Cols.TITLE))
        val date = getLong(getColumnIndex(CrimeTable.Cols.DATE))
        val isSolved = getInt(getColumnIndex(CrimeTable.Cols.SOLVED))
        val suspect = getString(getColumnIndex(CrimeTable.Cols.SUSPECT))

        val crime = Crime(UUID.fromString(uuidString))
        crime.mDate = Date(date)
        crime.mTitle = title
        crime.mSolved = (isSolved != 0)
        crime.mSuspect = suspect
        return crime
    }
}