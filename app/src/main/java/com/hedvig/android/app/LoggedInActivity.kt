package com.hedvig.android.app

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.widget.FrameLayout
import androidx.navigation.findNavController
import com.hedvig.android.owldroid.util.extensions.doOnLayout

class LoggedInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = FrameLayout(this)

        view.addView(layoutInflater.inflate(R.layout.logged_in_host, view, false))
        view.doOnLayout {
            val bottomTabs = view.findViewById<BottomNavigationView>(R.id.bottomTabs)
            val navController = findNavController(R.id.loggedInNavigationHost)
            bottomTabs.setOnNavigationItemSelectedListener { tab ->
                navController.navigate(tab.itemId)
                true
            }
        }

        view.id = R.id.loggedIn
        setContentView(view)
    }
}
