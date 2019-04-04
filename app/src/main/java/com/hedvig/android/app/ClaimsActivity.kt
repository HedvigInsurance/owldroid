package com.hedvig.android.app

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.FrameLayout
import com.hedvig.android.owldroid.ui.claims.ClaimsFragment

class ClaimsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = FrameLayout(this)

        view.addView(layoutInflater.inflate(R.layout.claims_navigation_host, view, false))

        view.id = R.id.claimsNavigation
        setContentView(view)
    }
}
