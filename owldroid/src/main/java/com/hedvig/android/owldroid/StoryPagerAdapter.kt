package com.hedvig.android.owldroid

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

class StoryPageAdapter(fragmentManager: FragmentManager, val size: Int) : FragmentPagerAdapter(fragmentManager) {
    override fun getItem(position: Int): Fragment {
        return StoryFragment.newInstance(position)
    }

    override fun getCount(): Int {
        return size
    }
}
