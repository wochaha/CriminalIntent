package com.example.criminalintent.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.criminalintent.fragment.CrimeFragment
import com.example.criminalintent.model.CrimeLab
import kotlinx.android.synthetic.main.activity_crime_pager.*
import android.content.Intent
import android.content.Context
import android.view.View
import androidx.viewpager.widget.ViewPager
import com.example.criminalintent.R
import com.example.criminalintent.model.Crime
import com.example.criminalintent.scroller.ViewPagerScroller
import java.util.*
import kotlin.collections.ArrayList

class CrimePagerActivity : AppCompatActivity(),View.OnClickListener {
    private lateinit var mCrimes:ArrayList<Crime>

    override fun onClick(v: View?) {
        if (v != null) {
            activity_crime_view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
                override fun onPageScrollStateChanged(state: Int) {
                }

                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                }

                override fun onPageSelected(position: Int) {
                }

            })
            if (v.id == jump_to_first.id){
                activity_crime_view_pager.currentItem = 0
                jump_to_first.isClickable =false
            }else if (v.id == jump_to_last.id){
                activity_crime_view_pager.currentItem = mCrimes.size
                jump_to_last.isClickable = false
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crime_pager)
        mCrimes = CrimeLab.get(this).getCrimes()
        val id = intent.getSerializableExtra(EXTRA_CRIME_ID) as UUID
        val manager : FragmentManager = supportFragmentManager

        //使用自定义的viewpager滑动类，减少翻页动画
        val viewPagerScroller = ViewPagerScroller(this)
        viewPagerScroller.initViewPagerScroll(activity_crime_view_pager)
        activity_crime_view_pager.adapter = object : FragmentStatePagerAdapter(manager,1){
            override fun getItem(position: Int): Fragment {
                val crime = mCrimes[position]
                return CrimeFragment.newIntent(crime.mId)
            }

            override fun getCount(): Int {
                return mCrimes.size
            }

        }
        if (mCrimes.size != 0){
            activity_crime_view_pager.currentItem = CrimeLab.get(this).getCrimePosition(id)!!
        }
        jump_to_first.setOnClickListener(this)
        jump_to_last.setOnClickListener(this)
    }

    override fun onBackPressed() {
        val crimeID = mCrimes[activity_crime_view_pager.currentItem].mId
        CrimeFragment.returnResult(this,crimeID)
    }

    companion object{
        const val  EXTRA_CRIME_ID = "com.bignerdranch.android.criminalintent.crime_id"

        fun newIntent(packageContext: Context, crimeId: UUID): Intent {
            val intent = Intent(packageContext, CrimePagerActivity::class.java)
            intent.putExtra(EXTRA_CRIME_ID, crimeId)
            return intent
        }
    }
}
