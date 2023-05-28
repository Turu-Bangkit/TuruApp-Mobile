package com.capstone.turuappmobile.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.capstone.turuappmobile.ui.fragment.historyAnalysis.HistorySleepAnalysistFragment
import com.capstone.turuappmobile.ui.fragment.historyList.HistorySleepListFragment

class SectionsPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    var username: String = ""

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                val fragment1 = HistorySleepListFragment()
                fragment1
            }
            1 -> {
                val fragment2 = HistorySleepAnalysistFragment()
                fragment2
            }
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }

    override fun getItemCount(): Int {
        return 2
    }
}