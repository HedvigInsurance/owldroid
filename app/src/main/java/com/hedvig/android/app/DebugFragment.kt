package com.hedvig.android.app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.apollographql.apollo.ApolloClient
import com.hedvig.android.owldroid.util.extensions.compatColor
import com.hedvig.android.owldroid.util.extensions.compatSetTint
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_debug.*
import javax.inject.Inject

class DebugFragment : Fragment() {
    @Inject
    lateinit var apolloClient: ApolloClient

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_debug, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val sharedPreferences = requireContext().getSharedPreferences("debug", Context.MODE_PRIVATE)
        debug_token_input.setText(sharedPreferences.getString("@hedvig:token", ""))

        debug_open_marketing.background.compatSetTint(requireContext().compatColor(R.color.dark_purple))
        debug_open_logo.background.compatSetTint(requireContext().compatColor(R.color.dark_purple))
        debug_open_profile.background.compatSetTint(requireContext().compatColor(R.color.dark_purple))
        debugOpenReferralAcceptance.background.compatSetTint(requireContext().compatColor(R.color.dark_purple))
        debug_save_inputs.background.compatSetTint(requireContext().compatColor(R.color.dark_purple))
        openClaimsScreen.background.compatSetTint(requireContext().compatColor(R.color.dark_purple))

        debug_open_marketing.setOnClickListener {
            startActivity(Intent(requireContext(), MarketingActivity::class.java))
        }

        debug_open_logo.setOnClickListener {
            startActivity(Intent(requireContext(), LogoActivity::class.java))
        }

        debug_open_profile.setOnClickListener {
            startActivity(Intent(requireContext(), ProfileActivity::class.java))
        }

        openClaimsScreen.setOnClickListener {
            startActivity(Intent(requireContext(), ClaimsActivity::class.java))
        }

        debugOpenReferralAcceptance.setOnClickListener {
            startActivity(Intent(requireContext(), ReferralAcceptanceActivity::class.java))
        }

        debug_save_inputs.setOnClickListener {
            val token = debug_token_input.text.toString()
            val editor = sharedPreferences.edit()
            editor.putString("@hedvig:token", token)
            editor.apply()
            apolloClient
                .apolloStore()
                .clearAll()
                .execute()

            Toast.makeText(context, "Input saved!", Toast.LENGTH_SHORT).show()
        }
    }
}
