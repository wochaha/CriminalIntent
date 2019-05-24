package com.example.criminalintent.activity

import androidx.fragment.app.Fragment
import com.example.criminalintent.fragment.CrimeListFragment

class CrimeListActivity : SingleFragmentActivity() {
    override fun createFragment(): Fragment {
        return CrimeListFragment()
    }
}
