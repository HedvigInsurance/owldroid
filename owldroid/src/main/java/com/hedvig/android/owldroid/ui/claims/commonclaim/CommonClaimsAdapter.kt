package com.hedvig.android.owldroid.ui.claims.commonclaim

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.hedvig.android.owldroid.R
import com.hedvig.android.owldroid.graphql.CommonClaimQuery
import com.hedvig.android.owldroid.service.remotevectordrawable.RemoteVectorDrawable
import kotlinx.android.synthetic.main.claims_common_claim_cell.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.annotations.NotNull
import timber.log.Timber
import java.net.URL

class CommonClaimsAdapter(
    private val commonClaims: @NotNull MutableList<CommonClaimQuery.CommonClaim>,
    private val navigateToCommonClaimFragment: (CommonClaimQuery.AsTitleAndBulletPoints) -> Unit,
    private val navigateToEmergencyFragment: (CommonClaimQuery.AsEmergency) -> Unit,
    private val remoteVectorDrawable: RemoteVectorDrawable,
    private val giraffeUrl: String
) : RecyclerView.Adapter<CommonClaimsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.claims_common_claim_cell,
                parent,
                false
            )
        )

    override fun getItemCount(): Int = commonClaims.size

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.apply {
            val commonClaim = commonClaims[position]

            when (val layout = commonClaim.layout()) {
                is CommonClaimQuery.AsTitleAndBulletPoints ->
                    view.setOnClickListener { navigateToCommonClaimFragment.invoke(layout) }
                is CommonClaimQuery.AsEmergency ->
                    view.setOnClickListener { navigateToEmergencyFragment.invoke(layout) }
                else ->
                    view.setOnClickListener { Timber.i("Not a recognized view") }
            }

            //commonClaimIcon.setImageDrawable(commonClaimIcon.context.compatDrawable(R.drawable.icon_failure)) //todo
            GlobalScope.launch(Dispatchers.IO) {
                val drawable = remoteVectorDrawable.downloadVectorDrawable(
                    URL(giraffeUrl + commonClaim.icon().vectorDrawableUrl())
                )
                GlobalScope.launch(Dispatchers.Main) {
                    commonClaimIcon.post {
                        commonClaimIcon.setImageDrawable(drawable)
                    }
                }
            }
            commonClaimLabel.text = commonClaim.title()
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val view = itemView
        val commonClaimIcon: ImageView = itemView.commonClaimCellIcon
        val commonClaimLabel: TextView = itemView.commonClaimCellLabel
    }
}
