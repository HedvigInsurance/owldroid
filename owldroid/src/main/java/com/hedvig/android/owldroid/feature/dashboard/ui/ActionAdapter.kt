package com.hedvig.android.owldroid.feature.dashboard.ui

import android.app.Activity
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.hedvig.android.owldroid.R
import com.hedvig.android.owldroid.graphql.DashboardQuery
import com.hedvig.android.owldroid.util.extensions.view.setHapticClickListener
import kotlinx.android.synthetic.main.dashboard_action.view.*

class ActionAdapter(
    val items: List<DashboardQuery.ChatAction>,
    val context: Context,
    val activity: Activity,
    val dashboardViewModel: DashboardViewModel
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
        holder.button.setHapticClickListener {
            dashboardViewModel.triggerFreeTextChat {
                navController.navigate(R.id.action_loggedInFragment_to_chatFragment)
            }
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
