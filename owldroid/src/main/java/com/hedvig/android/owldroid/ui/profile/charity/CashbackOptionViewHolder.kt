package com.hedvig.android.owldroid.ui.profile.charity

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Button
import android.widget.TextView
import kotlinx.android.synthetic.main.cashback_option.view.*

class CashbackOptionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val title: TextView = view.cashbackOptionTitle
    val paragraph: TextView = view.cashbackOptionParagraph
    val button: Button = view.cashbackSelect
}
