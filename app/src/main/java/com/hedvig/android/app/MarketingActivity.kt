package com.hedvig.android.app

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.MotionEvent
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
import com.apollographql.apollo.api.internal.Optional
import com.apollographql.apollo.exception.ApolloException
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
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

    val dataCallback: ApolloCall.Callback<Optional<MarketingStoriesQuery.Data>> = ApolloCallback(object : ApolloCall.Callback<Optional<MarketingStoriesQuery.Data>>() {

        override fun onStatusEvent(event: ApolloCall.StatusEvent) {
            Timber.e("StatusEvent: %s", event.toString())
        }

        override fun onFailure(e: ApolloException) {
            Timber.e("Failed to load marketing stories :(")
        }

        override fun onResponse(response: Response<Optional<MarketingStoriesQuery.Data>>) {
            //val urls = response.data()?.marketingStories()?.filter { it.asset()?.mimeType() == "image/jpeg" }?.map { it.asset()?.url().toString() }
            val data = response.data()?.get()?.marketingStories()
            runOnUiThread {
                text123.visibility = TextView.GONE
                pager.adapter = StoryPageAdapter(context, data as List<MarketingStoriesQuery.MarketingStory>)
                pager.visibility = ViewPager.VISIBLE
                val pageChangeListener = object: ViewPager.OnPageChangeListener {
                    override fun onPageScrollStateChanged(p0: Int) {}

                    override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {}

                    override fun onPageSelected(p0: Int) {
                        val currentView = pager.findViewWithTag<View>(p0)
                        if (currentView is PlayerView) {
                            val player = currentView.player
                            player.seekTo(0)
                            player.playWhenReady = true
                        }
                    }
                }
                pager.addOnPageChangeListener(pageChangeListener)
                pager.post {
                    pageChangeListener.onPageSelected(0)
                }
                marketing_proceed.visibility = Button.VISIBLE
                marketing_login.visibility = Button.VISIBLE
                marketing_proceed.setOnClickListener {
                    Timber.e("Should navigate to chat!")
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

class StoryPageAdapter(val context: Context, val data: List<MarketingStoriesQuery.MarketingStory>) : PagerAdapter() {
    private fun getView(position: Int, viewPager: ViewPager): View {
        val layoutInflater = LayoutInflater.from(context)
        val view = layoutInflater.inflate(R.layout.page_marketing_story, viewPager, false) as LinearLayout
        val story = data[position].asset().get()
        val mimeType = story.mimeType().get()
        val url = story.url()
        if (mimeType == "image/jpeg") {
            val playerView = view.findViewById<PlayerView>(R.id.story_video)
            view.removeView(playerView)
            val imageView = setupImageView(view, viewPager, url)
            imageView.tag = position
        } else if (mimeType == "video/mp4" || mimeType == "video/quicktime") {
            val imageView = view.findViewById<ImageView>(R.id.story_image)
            view.removeView(imageView)
            val playerView = setupPlayerView(view, viewPager, url)
            playerView.tag = position
        }

        return view
    }

    private fun setupPlayerView(parentView: LinearLayout, viewPager: ViewPager, url: String): PlayerView {
        val playerView = parentView.findViewById<PlayerView>(R.id.story_video)
        val player = ExoPlayerFactory.newSimpleInstance(context, DefaultTrackSelector())
        playerView.player = player
        val dataSourceFactory = DefaultDataSourceFactory(context, Util.getUserAgent(context, BuildConfig.APPLICATION_ID))
        when (Util.inferContentType(url)) {
            C.TYPE_HLS -> {
                val mediaSource = HlsMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(url))
                player.prepare(mediaSource)
            }

            C.TYPE_OTHER -> {
                val mediaSource = ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(url))
                player.prepare(mediaSource)
            }
        }
        player.playWhenReady = false
        player.volume = 0f
        playerView.visibility = PlayerView.VISIBLE
        setupTouchListeners(playerView, viewPager)

        return playerView
    }

    private fun setupImageView(parentView: LinearLayout, viewPager: ViewPager, url: String): ImageView {
        val imageView = parentView.findViewById<ImageView>(R.id.story_image)

        Picasso.get()
                .load(url)
                .fit()
                .centerCrop()
                .into(imageView)
        imageView.visibility = ImageView.VISIBLE
        setupTouchListeners(imageView, viewPager)

        return imageView
    }

    private fun setupTouchListeners(view: View, pager: ViewPager) {
        view.setOnTouchListener { _, event ->
            if (event.action != MotionEvent.ACTION_UP) {
                return@setOnTouchListener true
            }
            val viewCoords = intArrayOf(0, 0)
            view.getLocationOnScreen(viewCoords)
            val x = event.x - viewCoords[0]
            val twentyPercent = view.measuredWidth * 0.25
            if (x > twentyPercent) {
                pager.currentItem += 1
            } else {
                pager.currentItem -= 1
            }
            true
        }
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val pager = container as ViewPager
        val view = getView(position, pager)
        pager.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, view: Any) {
        container.removeView(view as View)
    }

    override fun isViewFromObject(p0: View, p1: Any): Boolean {
        return p0 == p1
    }

    override fun getCount(): Int {
        return data.size
    }
}

