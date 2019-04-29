package com.hedvig.android.owldroid.ui.dashboard

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.widget.RecyclerView
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.hedvig.android.owldroid.R
import com.hedvig.android.owldroid.graphql.DashboardQuery
import kotlinx.android.synthetic.main.dashboard_action.view.*

class ActionAdapter(
    val items: List<DashboardQuery.ChatAction>,
    val context: Context,
    val activity: Activity
) : RecyclerView.Adapter<ActionAdapter.ActionViewHolder>() {

    val navController by lazy { activity.findNavController(R.id.rootNavigationHost) }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ActionViewHolder(
            LayoutInflater
                .from(context)
                .inflate(R.layout.dashboard_action, parent, false)
        )

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ActionViewHolder, position: Int) {
        holder.button.text = items[position].text()
        holder.button.setOnClickListener {
            holder.button.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)

            navController.navigate(R.id.action_loggedInFragment_to_chatFragment)

            // LocalBroadcastManager
            //     .getInstance(context)
            //     .sendBroadcast(Intent(DASHBOARD_NAVIGATION).also { intent ->
            //         intent.putExtra(ACTION, OPEN_CHAT)
            //         intent.putExtra(CHAT_ACTION_URL, items[position].triggerUrl())
            //     })
        }
    }

    class ActionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val button = view.dashboardChatAction
    }

    companion object {
        const val DASHBOARD_NAVIGATION = "dashboardNavigation"
        const val ACTION = "action"
        const val OPEN_CHAT = "openChat"
        const val CHAT_ACTION_URL = "chatAction"
    }
}
