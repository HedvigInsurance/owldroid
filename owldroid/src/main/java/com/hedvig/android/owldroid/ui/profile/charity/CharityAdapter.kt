package com.hedvig.android.owldroid.ui.profile.charity

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.hedvig.android.owldroid.R
import com.hedvig.android.owldroid.graphql.ProfileQuery

class CharityAdapter(
    val items: List<ProfileQuery.CashbackOption>,
    val context: Context,
    val clickListener: (String) -> Unit
) : RecyclerView.Adapter<CashbackOptionViewHolder>() {
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
        holder.button.text = "Välj ${item.name()}"
        holder.button.setOnClickListener {
            item.id()?.let { id ->
                clickListener(id)
            }
        }
    }
}
