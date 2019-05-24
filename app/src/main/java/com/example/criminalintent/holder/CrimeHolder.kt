package com.example.criminalintent.holder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.criminalintent.R
import com.example.criminalintent.model.Crime
import kotlinx.android.synthetic.main.list_item_crime.view.*

class CrimeHolder(inflater: LayoutInflater,parent:ViewGroup) : BaseCrimeHolder(inflater, R.layout.list_item_crime,parent) {
    override fun bind(crime: Crime) {
        super.bind(crime)
        itemView.crime_title.text = mCrime!!.mTitle
        itemView.crime_date.text = date
    }
}