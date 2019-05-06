package com.hedvig.android.owldroid.ui.loggedin

import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.hedvig.android.owldroid.R
import com.hedvig.android.owldroid.feature.claims.ui.ClaimsFragment
import com.hedvig.android.owldroid.feature.dashboard.ui.DashboardFragment
import com.hedvig.android.owldroid.feature.profile.ui.ProfileFragment
import com.hedvig.android.owldroid.util.extensions.byOrdinal

class TabPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {
    override fun getItem(page: Int): Fragment = when (byOrdinal<LoggedInTabs>(page)) {
        LoggedInTabs.DASHBOARD -> DashboardFragment()
        LoggedInTabs.CLAIMS -> ClaimsFragment()
        LoggedInTabs.PROFILE -> ProfileFragment()
    }

    override fun getCount() = 3
}

enum class LoggedInTabs {
    DASHBOARD,
    CLAIMS,
    PROFILE;

    companion object {
        fun fromId(@IdRes id: Int) = when (id) {
            R.id.dashboard -> DASHBOARD
            R.id.claims -> CLAIMS
            R.id.profile -> PROFILE
            else -> throw Error("Invalid Menu ID")
        }
    }
}
