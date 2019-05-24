package com.example.criminalintent.scroller

import android.content.Context
import android.widget.Scroller
import androidx.viewpager.widget.ViewPager
import java.lang.Exception
import java.lang.reflect.Field

class ViewPagerScroller(context: Context?) : Scroller(context) {
    private val mScrollerDuration = 0

    override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int) {
        super.startScroll(startX, startY, dx, dy,mScrollerDuration)
    }

    override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int, duration: Int) {
        super.startScroll(startX, startY, dx, dy,mScrollerDuration)
    }

    fun initViewPagerScroll(viewPager: ViewPager) {
        try {
            val mScroller = ViewPager::class.java.getDeclaredField("mScroller")
            mScroller.isAccessible = true
            mScroller.set(viewPager, this)
        } catch (e:Exception) {
            e.printStackTrace()
        }
    }
}