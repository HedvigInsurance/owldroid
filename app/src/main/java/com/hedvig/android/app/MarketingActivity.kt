package com.hedvig.android.app

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloCallback
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.apollographql.apollo.fetcher.ApolloResponseFetchers
import com.google.android.exoplayer2.ui.PlayerView
import com.hedvig.android.app.graphql.MarketingStoriesQuery
import com.squareup.picasso.Picasso
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

import kotlinx.android.synthetic.main.activity_marketing.*
import timber.log.Timber

class MarketingActivity : DaggerAppCompatActivity() {
    @Inject
    lateinit var apolloClient: ApolloClient

    @Inject
    lateinit var context: Context

    val uiHandler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_marketing)

        fetchMarketingStories()
    }

    val dataCallback: ApolloCall.Callback<MarketingStoriesQuery.Data> = ApolloCallback(object : ApolloCall.Callback<MarketingStoriesQuery.Data>() {

        override fun onStatusEvent(event: ApolloCall.StatusEvent) {
            Timber.e("StatusEvent: %s", event.toString())
        }

        override fun onFailure(e: ApolloException) {
            Timber.e("Failed to load marketing stories :(")
        }

        override fun onResponse(response: Response<MarketingStoriesQuery.Data>) {
            val urls = response.data()?.marketingStories()?.filter { it.asset()?.mimeType() == "image/jpeg" }?.map { it.asset()?.url().toString() }
            runOnUiThread {
                text123.visibility = TextView.GONE
                pager.adapter = StoryPageAdapter(context, urls.orEmpty())
                pager.visibility = ViewPager.VISIBLE
                marketing_proceed.visibility = Button.VISIBLE
                marketing_login.visibility = Button.VISIBLE
                marketing_proceed.setOnClickListener {
                    val max = (pager.adapter as StoryPageAdapter).count - 1
                    if (pager.currentItem == max) {
                        Timber.e("Should navigate to chat!")
                        return@setOnClickListener
                    }
                    pager.currentItem = pager.currentItem + 1
                }
                marketing_login.setOnClickListener {
                    Timber.e("Should show login with BankID!")
                }
            }
        }

    }, uiHandler)

    fun fetchMarketingStories() {
        val marketingStoriesQuery = MarketingStoriesQuery.builder()
                .build()

        apolloClient
                .query(marketingStoriesQuery)
                .enqueue(dataCallback)
        Timber.i("Loading marketing stories...")
    }
}

class StoryPageAdapter(val context: Context, val data: List<String>) : PagerAdapter() {
    @SuppressLint("ClickableViewAccessibility") // TODO Fix this later on
    fun getView(position: Int, viewPager: ViewPager): View {
        val layoutInflater = LayoutInflater.from(context)
        val view = layoutInflater.inflate(R.layout.page_marketing_story, viewPager, false) as LinearLayout
        val imageView = view.findViewById<ImageView>(R.id.story_image)

        val videoView = view.findViewById<PlayerView>(R.id.story_video)
        view.removeView(videoView)

        Picasso.get()
                .load(data[position])
                .fit()
                .centerCrop()
                .into(imageView)
        imageView.visibility = ImageView.VISIBLE
        imageView.setOnTouchListener { v, event ->
            val viewCoords = intArrayOf(0, 0)
            imageView.getLocationOnScreen(viewCoords)
            val x = event.x - viewCoords[0]
            val twentyPercent = imageView.measuredWidth * 0.25
            if (x > twentyPercent) {
                viewPager.currentItem += 1
            } else {
                viewPager.currentItem -= 1
            }
            true
        }

        return view
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val pager = container as ViewPager
        val view = getView(position, pager)
        pager.addView(view)
        return view
    }

    override fun isViewFromObject(p0: View, p1: Any): Boolean {
        return p0 == p1
    }

    override fun getCount(): Int {
        return data.size
    }
}

