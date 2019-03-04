package com.hedvig.android.owldroid.ui.profile.charity

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hedvig.android.owldroid.R
import com.hedvig.android.owldroid.di.ViewModelFactory
import com.hedvig.android.owldroid.graphql.ProfileQuery
import com.hedvig.android.owldroid.ui.profile.ProfileViewModel
import com.hedvig.android.owldroid.util.extensions.compatFont
import com.hedvig.android.owldroid.util.extensions.localBroadcastManager
import com.hedvig.android.owldroid.util.extensions.remove
import com.hedvig.android.owldroid.util.extensions.show
import com.squareup.picasso.Picasso
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.app_bar.*
import kotlinx.android.synthetic.main.cashback_option.view.*
import kotlinx.android.synthetic.main.fragment_charity.*
import kotlinx.android.synthetic.main.fragment_coinsured.*
import javax.inject.Inject

class CharityFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    lateinit var profileViewModel: ProfileViewModel

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        profileViewModel = requireActivity().run {
            ViewModelProviders.of(this, viewModelFactory).get(ProfileViewModel::class.java)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_charity, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)

        collapsingToolbar.title = resources.getString(R.string.charity_title)
        collapsingToolbar.setExpandedTitleTypeface(requireContext().compatFont(R.font.circular_bold))
        collapsingToolbar.setCollapsedTitleTypeface(requireContext().compatFont(R.font.circular_bold))
        toolbar.setNavigationIcon(R.drawable.ic_back)
        toolbar.setNavigationOnClickListener {
            val intent = Intent("profileNavigation")
            intent.putExtra("action", "back")
            localBroadcastManager.sendBroadcast(intent)
        }

        loadData()
    }

    private fun loadData() {
        profileViewModel.data.observe(this, Observer { profileData ->
            loadingSpinner.remove()

            profileData?.let { data ->
                if (data.cashback() == null) {
                    data.cashbackOptions().let { cashbackOptions ->
                        showCharityPicker(cashbackOptions)
                    }
                } else {
                    showSelectedCharity(data.cashback()!!)
                }
            }

        })
    }

    private fun showSelectedCharity(cashback: ProfileQuery.Cashback) {
        selectedCharityContainer.show()
        selectCharityContainer.remove()
        Picasso
                .get()
                .load(cashback.imageUrl())
                .into(selectedCharityBanner)

        selectedCharityCardTitle.text = cashback.name()
        selectedCharityCardParagraph.text = cashback.paragraph()
    }

    private fun showCharityPicker(options: List<ProfileQuery.CashbackOption>) {
        selectCharityContainer.show()
        cashbackOptions.layoutManager = LinearLayoutManager(requireContext())
        cashbackOptions.adapter = CharityAdapter(options, requireContext()) { id ->
            profileViewModel.selectCashback(id)
        }
    }

}


class CharityAdapter(
        val items: List<ProfileQuery.CashbackOption>,
        val context: Context,
        val clickListener: (String) -> Unit
) : RecyclerView.Adapter<CashbackOptionViewHolder>() {
    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CashbackOptionViewHolder = CashbackOptionViewHolder(LayoutInflater.from(context).inflate(R.layout.cashback_option, parent, false))

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

class CashbackOptionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val title = view.cashbackOptionTitle
    val paragraph = view.cashbackOptionParagraph
    val button = view.cashbackSelect
}