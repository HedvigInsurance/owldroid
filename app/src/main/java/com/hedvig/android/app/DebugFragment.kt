package com.hedvig.android.app

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import com.hedvig.android.owldroid.util.compatSetTint
import kotlinx.android.synthetic.main.fragment_debug.*


class DebugFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_debug, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val sharedPreferences = context!!.getSharedPreferences("debug", Context.MODE_PRIVATE)
        debug_token_input.setText(sharedPreferences.getString("@hedvig:token", ""))

        debug_open_marketing.background.compatSetTint(ContextCompat.getColor(context!!, R.color.dark_purple))
        debug_open_logo.background.compatSetTint(ContextCompat.getColor(context!!, R.color.dark_purple))
        debug_open_profile.background.compatSetTint(ContextCompat.getColor(context!!, R.color.dark_purple))
        debug_save_inputs.background.compatSetTint(ContextCompat.getColor(context!!, R.color.dark_purple))

        val navigationController = activity!!.findNavController(R.id.navigationHostFragment)

        debug_open_marketing.setOnClickListener {
            navigationController.navigate(R.id.action_debugFragment_to_marketingFragment)
        }

        debug_open_logo.setOnClickListener {
            navigationController.navigate(R.id.action_debugFragment_to_logoFragment)
        }

        debug_open_profile.setOnClickListener {
            navigationController.navigate(R.id.action_debugFragment_to_profileFragment)
        }

        debug_save_inputs.setOnClickListener {
            val token = debug_token_input.text.toString()
            val editor = sharedPreferences.edit()
            editor.putString("@hedvig:token", token)
            editor.apply()
            Toast.makeText(context, "Input saved!", Toast.LENGTH_SHORT).show()
        }
    }
}
