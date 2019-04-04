package com.hedvig.android.app

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.rx2.Rx2Apollo
import com.google.firebase.firestore.FirebaseFirestore
import com.hedvig.android.owldroid.graphql.MemberIdQuery
import com.hedvig.android.owldroid.util.extensions.compatColor
import com.hedvig.android.owldroid.util.extensions.compatSetTint
import com.hedvig.android.owldroid.util.extensions.hide
import com.hedvig.android.owldroid.util.extensions.show
import dagger.android.support.AndroidSupportInjection
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_referral_acceptance.*
import timber.log.Timber
import javax.inject.Inject

class ReferralAcceptanceFragment : Fragment() {

    @Inject
    lateinit var apolloClient: ApolloClient

    private var disposable: Disposable? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        AndroidSupportInjection.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_referral_acceptance, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        submitReferral.background.compatSetTint(requireContext().compatColor(R.color.purple))

        context?.getSharedPreferences("debug", Context.MODE_PRIVATE)?.let { sharedPreferences ->
            val maybeRefereeId = sharedPreferences.getString("referee", null)
            loadingSpinner.hide()
            maybeRefereeId?.let { refereeId ->
                status.show()
                status.text = "This user was referred by $refereeId"
                disposable = Rx2Apollo.from(apolloClient.query(MemberIdQuery.builder().build()))
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ response ->
                        response.data()?.member()?.id()?.let { memberId ->
                            submitReferral.show()
                            submitReferral.setOnClickListener {
                                FirebaseFirestore
                                    .getInstance()
                                    .collection("referrals")
                                    .add(hashMapOf("memberId" to memberId, "invitedByMemberId" to refereeId))
                                    .addOnSuccessListener {
                                        Toast
                                            .makeText(
                                                requireContext(),
                                                "Successfully saved the referral!",
                                                Toast.LENGTH_LONG
                                            )
                                            .show()
                                    }
                            }
                        }
                    }, { error ->
                        Timber.e(error, "Failed to get memberId for referral")
                    })
            } ?: wasNotReferred()
        } ?: wasNotReferred()
    }

    private fun wasNotReferred() {
        status.text = "This user was not referred"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposable?.dispose()
    }
}
