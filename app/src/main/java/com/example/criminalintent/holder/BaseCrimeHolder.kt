package com.example.criminalintent.holder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.criminalintent.activity.CrimeListActivity
import com.example.criminalintent.activity.CrimePagerActivity
import com.example.criminalintent.fragment.CrimeListFragment.Companion.REQUEST_CRIME
import com.example.criminalintent.model.Crime
import java.text.SimpleDateFormat
import java.util.*

open class BaseCrimeHolder(inflater: LayoutInflater,resourceId:Int,parent:ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(resourceId,parent,false)){

    init {
        itemView.setOnClickListener{
            //通过inflater获取持有viewHolder的context
            val intent = CrimePagerActivity.newIntent(inflater.context, mCrime!!.mId)
            //暂时只能这样获取到与CrimeListActivity绑定的fragment
            (inflater.context as CrimeListActivity).supportFragmentManager.fragments[0]!!.startActivityForResult(intent, REQUEST_CRIME)
        }
    }

    var mCrime : Crime? = null
    open var date:String? = null

    open fun bind(crime: Crime){
        mCrime = crime
        val dateFormat = SimpleDateFormat("yyyy-MM-dd E a HH:mm:ss", Locale.CHINA)
        date= dateFormat.format(mCrime!!.mDate)
    }
}