package com.hedvig.android.owldroid.ui.claims.quickaction

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.hedvig.android.owldroid.R
import com.hedvig.android.owldroid.graphql.CommonClaimQuery
import kotlinx.android.synthetic.main.claims_quick_action_cell.view.*
import org.jetbrains.annotations.NotNull

class QuickActionsAdapter(private val commonClaims: @NotNull MutableList<CommonClaimQuery.CommonClaim>, private val openQuickActionFragment: () -> Unit): RecyclerView.Adapter<QuickActionsAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.claims_quick_action_cell,
                parent,
                false))

    override fun getItemCount(): Int = commonClaims.size

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val commonClaim = commonClaims[position]
        viewHolder.apply {
            view.setOnClickListener { openQuickActionFragment() }
//            quickActionIcon.setImageDrawable(quickActionIcon.context.compatDrawable(R.drawable.icon_failure))
            quickActionTitle.text = commonClaim.title()
//            quickActionSubtitle.text = quickAction.subtitle
        }
    }

    inner class ViewHolder(itemView : View): RecyclerView.ViewHolder(itemView) {
        val view = itemView
        val quickActionIcon: ImageView = itemView.claimsQuickActionIcon
        val quickActionTitle: TextView = itemView.claimsQuickActionTitle
        val quickActionSubtitle: TextView = itemView.claimsQuickActionParagraph
    }
}
