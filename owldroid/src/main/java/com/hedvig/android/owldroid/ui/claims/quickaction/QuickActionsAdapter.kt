package com.hedvig.android.owldroid.ui.claims.quickaction

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.hedvig.android.owldroid.R
import com.hedvig.android.owldroid.data.claims.ClaimsQuickAction
import com.hedvig.android.owldroid.util.extensions.compatDrawable
import kotlinx.android.synthetic.main.claims_quick_action_cell.view.*

class QuickActionsAdapter(private val quickActions: List<ClaimsQuickAction>, private val openQuickActionFragment: () -> Unit): RecyclerView.Adapter<QuickActionsAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.claims_quick_action_cell,
                parent,
                false))

    override fun getItemCount(): Int = quickActions.size

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val quickAction = quickActions[position]
        viewHolder.apply {
            view.setOnClickListener { openQuickActionFragment() }
            quickActionIcon.setImageDrawable(quickActionIcon.context.compatDrawable(R.drawable.icon_failure))
            quickActionTitle.text = quickAction.title
            quickActionSubtitle.text = quickAction.subtitle
        }
    }

    inner class ViewHolder(itemView : View): RecyclerView.ViewHolder(itemView) {
        val view = itemView
        val quickActionIcon: ImageView = itemView.claimsQuickActionIcon
        val quickActionTitle: TextView = itemView.claimsQuickActionTitle
        val quickActionSubtitle: TextView = itemView.claimsQuickActionParagraph
    }
}
