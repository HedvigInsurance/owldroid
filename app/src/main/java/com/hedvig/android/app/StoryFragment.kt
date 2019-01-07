package com.hedvig.android.app

import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.squareup.picasso.Picasso

class StoryFragment : Fragment() {

    private lateinit var marketingStoriesViewModel: MarketingStoriesViewModel
    private var player: SimpleExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        marketingStoriesViewModel = activity?.run {
            ViewModelProviders.of(this).get(MarketingStoriesViewModel::class.java)
        } ?: throw Exception("Invalid activity")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val position = arguments?.getInt(POSITION_KEY) ?: throw Exception("No position provided")

        val view = inflater.inflate(R.layout.page_marketing_story, container, false) as LinearLayout
        val story = marketingStoriesViewModel.marketingStories.value?.get(position) ?: throw Exception("No data")
        val asset = story.asset().get()
        val mimeType = asset.mimeType().get()
        val url = asset.url()
        if (mimeType == "image/jpeg") {
            val playerView = view.findViewById<PlayerView>(R.id.story_video)
            view.removeView(playerView)
            val imageView = setupImageView(view, url)
            imageView.tag = position
        } else if (mimeType == "video/mp4" || mimeType == "video/quicktime") {
            val imageView = view.findViewById<ImageView>(R.id.story_image)
            view.removeView(imageView)
            val playerView = setupPlayerView(view, url, position)
            playerView.tag = position
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()

        player?.release()
    }

    private fun setupPlayerView(parentView: LinearLayout, url: String, position: Int): PlayerView {
        val playerView = parentView.findViewById<PlayerView>(R.id.story_video)
        player = ExoPlayerFactory.newSimpleInstance(context, DefaultTrackSelector())
        playerView.player = player
        val dataSourceFactory =
            DefaultDataSourceFactory(context, Util.getUserAgent(context, BuildConfig.APPLICATION_ID))
        when (Util.inferContentType(url)) {
            C.TYPE_HLS -> {
                val mediaSource = HlsMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(url))
                player?.prepare(mediaSource)
            }

            C.TYPE_OTHER -> {
                val mediaSource = ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(url))
                player?.prepare(mediaSource)
            }
        }
        player?.playWhenReady = false
        player?.volume = 0f
        playerView.visibility = PlayerView.VISIBLE
        setupTouchListeners(playerView)

        marketingStoriesViewModel.page.observe(this, Observer {
            if (it == position) {
                player?.seekTo(0)
                player?.playWhenReady = true
            } else {
                player?.playWhenReady = false
            }
        })

        marketingStoriesViewModel.paused.observe(this, Observer {
            if (marketingStoriesViewModel.page.value != position) {
                return@Observer
            }

            player?.playWhenReady = !it
        })

        return playerView
    }

    private fun setupImageView(parentView: LinearLayout, url: String): ImageView {
        val imageView = parentView.findViewById<ImageView>(R.id.story_image)

        Picasso.get()
            .load(url)
            .fit()
            .centerCrop()
            .into(imageView)
        imageView.visibility = ImageView.VISIBLE
        setupTouchListeners(imageView)

        return imageView
    }

    private fun setupTouchListeners(view: View) {
        var isHolding = false
        val handler = Handler()
        val holding = Runnable {
            isHolding = true
            marketingStoriesViewModel.pauseStory()
        }
        view.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                handler.postDelayed(holding, 150)
            }
            if (event.action != MotionEvent.ACTION_UP) {
                return@setOnTouchListener true
            }
            handler.removeCallbacks(holding)
            if (isHolding) {
                isHolding = false
                marketingStoriesViewModel.resumeStory()
                return@setOnTouchListener true
            }
            val viewCoords = intArrayOf(0, 0)
            view.getLocationOnScreen(viewCoords)
            val x = event.x - viewCoords[0]
            val oneFourth = view.measuredWidth * 0.25
            if (x > oneFourth) {
                marketingStoriesViewModel.nextScreen()
            } else {
                marketingStoriesViewModel.previousScreen()
            }
            true
        }
    }

    companion object {
        const val POSITION_KEY = "POSITION"

        fun newInstance(position: Int): StoryFragment {
            val fragment = StoryFragment()
            val args = Bundle()
            args.putInt(POSITION_KEY, position)
            fragment.arguments = args
            return fragment
        }
    }
}