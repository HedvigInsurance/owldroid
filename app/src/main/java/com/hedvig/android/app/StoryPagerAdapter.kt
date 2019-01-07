package com.hedvig.android.app

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class StoryPageAdapter(fragmentManager: FragmentManager, val size: Int) : FragmentPagerAdapter(fragmentManager) {
    override fun getItem(position: Int): Fragment {
        return StoryFragment.newInstance(position)
    }

    override fun getCount(): Int {
        return size
    }
}
