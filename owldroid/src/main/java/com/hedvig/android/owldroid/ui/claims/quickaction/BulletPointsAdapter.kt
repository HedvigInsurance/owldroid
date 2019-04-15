package com.hedvig.android.owldroid.ui.claims.quickaction

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.hedvig.android.owldroid.R
import com.hedvig.android.owldroid.graphql.CommonClaimQuery
import com.hedvig.android.owldroid.util.extensions.compatDrawable
import kotlinx.android.synthetic.main.claim_bulletpoint_row.view.*
import org.jetbrains.annotations.NotNull

class BulletPointsAdapter(private val bulletPoints: @NotNull MutableList<CommonClaimQuery.BulletPoint>): RecyclerView.Adapter<BulletPointsAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.claim_bulletpoint_row,
                parent,
                false))

    override fun getItemCount(): Int = bulletPoints.size

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val bulletPoint = bulletPoints[position]
        viewHolder.apply {
            bulletPointIcon.setImageDrawable(bulletPointIcon.context.compatDrawable(R.drawable.icon_failure))
            bulletPointTitle.text = bulletPoint.title()
            bulletPointDescription.text = bulletPoint.description()
        }
    }

    inner class ViewHolder(itemView : View): RecyclerView.ViewHolder(itemView) {
        val bulletPointIcon: ImageView = itemView.bulletPointIcon
        val bulletPointTitle: TextView = itemView.bulletPointTitle
        val bulletPointDescription: TextView = itemView.bulletPointDescription
    }
}
