package com.example.criminalintent.holder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.criminalintent.R
import com.example.criminalintent.model.Crime
import kotlinx.android.synthetic.main.list_item_crime_policy.view.*

class CrimeHolderPolicy(inflater: LayoutInflater,parent:ViewGroup) : BaseCrimeHolder(inflater, R.layout.list_item_crime_policy,parent) {
    override fun bind(crime: Crime) {
        super.bind(crime)
        itemView.crime_title_policy.text = mCrime!!.mTitle
        itemView.crime_date_policy.text = date
    }
}