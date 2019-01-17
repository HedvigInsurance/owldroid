package com.hedvig.android.app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_debug.*

class DebugActivity : DaggerAppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_debug)

        val sharedPreferences = this.getSharedPreferences("debug", Context.MODE_PRIVATE)
        debug_token_input.setText(sharedPreferences.getString("@hedvig:token", ""))

        debug_open_marketing.setOnClickListener {
            val intent = Intent(this, MarketingActivity::class.java)
            startActivity(intent)
        }

        debug_open_logo.setOnClickListener {
            val intent = Intent(this, LogoActivity::class.java)
            startActivity(intent)
        }

        debug_open_profile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        debug_save_inputs.setOnClickListener {
            val token = debug_token_input.text.toString()
            val editor = sharedPreferences.edit()
            editor.putString("@hedvig:token", token)
            editor.apply()
            Toast.makeText(this, "Input saved!", Toast.LENGTH_SHORT).show()
        }
    }
}