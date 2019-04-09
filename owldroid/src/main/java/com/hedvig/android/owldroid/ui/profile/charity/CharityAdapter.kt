package com.hedvig.android.owldroid.ui.profile.charity

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.hedvig.android.owldroid.R
import com.hedvig.android.owldroid.graphql.ProfileQuery
import com.hedvig.android.owldroid.util.interpolateTextKey
import kotlinx.android.synthetic.main.cashback_option.view.*

class CharityAdapter(
    val items: List<ProfileQuery.CashbackOption>,
    val context: Context,
    val clickListener: (String) -> Unit
) : RecyclerView.Adapter<CharityAdapter.CashbackOptionViewHolder>() {
    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CashbackOptionViewHolder =
        CashbackOptionViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.cashback_option,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: CashbackOptionViewHolder, position: Int) {
        val item = items[position]
        holder.title.text = item.name()
        holder.paragraph.text = item.paragraph()

        holder.button.text = interpolateTextKey(
            holder.itemView.resources.getString(R.string.PROFILE_CHARITY_SELECT_BUTTON),
            "CHARITY" to item.name()
        )
        holder.button.setOnClickListener {
            item.id()?.let { id ->
                clickListener(id)
            }
        }
    }

    class CashbackOptionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.cashbackOptionTitle
        val paragraph: TextView = view.cashbackOptionParagraph
        val button: Button = view.cashbackSelect
    }
}
