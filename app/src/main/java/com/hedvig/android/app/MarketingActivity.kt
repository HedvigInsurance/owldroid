package com.hedvig.android.app

import android.animation.ValueAnimator
import android.graphics.Color
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.animation.doOnEnd
import androidx.core.graphics.drawable.DrawableCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.CacheUtil
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.google.android.exoplayer2.util.Util
import com.squareup.picasso.Picasso
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_marketing.*
import javax.inject.Inject

class MarketingActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var cache: SimpleCache

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_marketing)

        observeMarketingStories()
    }

    private fun observeMarketingStories() {
        val marketingStoriesViewModel =
            ViewModelProviders.of(this, viewModelFactory)[MarketingStoriesViewModel::class.java]
        marketingStoriesViewModel
            .marketingStories
            .observe(this, Observer {
                text123.visibility = TextView.GONE
                val nStories = it?.size ?: throw Exception("Got no stories")
                pager.adapter = StoryPageAdapter(supportFragmentManager, nStories)
                pager.visibility = ViewPager.VISIBLE
                story_progress_indicator_container.visibility = LinearLayout.VISIBLE
                val width = activity_marketing.width
                for (n in 0 until nStories) {
                    val progressBar = layoutInflater.inflate(
                        R.layout.marketing_progress_bar,
                        story_progress_indicator_container,
                        false
                    ) as ProgressBar
                    progressBar.layoutParams = ViewGroup.LayoutParams(width / nStories, 10)
                    story_progress_indicator_container.addView(progressBar)
                    val animator = ValueAnimator.ofFloat(0f, 100f).apply {
                        duration = it[n].duration().get().toLong() * 1000
                        addUpdateListener { va ->
                            progressBar.progress = (va.animatedValue as Float).toInt()
                        }
                    }

                    marketingStoriesViewModel.page.observe(this, Observer { newPage ->
                        animator.removeAllListeners()
                        animator.cancel()
                        when {
                            newPage == n -> {
                                animator.doOnEnd {
                                    marketingStoriesViewModel.nextScreen()
                                }
                                animator.start()
                            }
                            newPage < n -> progressBar.progress = 0
                            newPage > n -> progressBar.progress = 100
                        }
                    })

                    marketingStoriesViewModel.blurred.observe(this, Observer { blurred ->
                        if (blurred) {
                            blur_overlay.visibility = View.VISIBLE
                            ValueAnimator.ofFloat(0f, 230f).apply {
                                duration = 300
                                addUpdateListener { opacity ->
                                    blur_overlay.setBackgroundColor(
                                        Color.argb(
                                            (opacity.animatedValue as Float).toInt(),
                                            255,
                                            255,
                                            255
                                        )
                                    )
                                }
                                start()
                            }

/*                            val swipeListener = GestureDetector(this, object : OnSwipeListener() {
                                override fun onSwipe(direction: Direction): Boolean {
                                    Timber.e("onSwipe triggered")
                                    return when (direction) {
                                        Direction.DOWN -> {
                                            Timber.e("Swiped down!")
                                            true
                                        }
                                        else -> {
                                            Timber.e("Swiped another direction")
                                            false
                                        }
                                    }
                                }
                            })

                            blur_overlay.setOnTouchListener { _, motionEvent ->
                                Timber.e("Touched blur overlay")
                                swipeListener.onTouchEvent(motionEvent)
                            }*/

                            val currentTop = marketing_proceed.top
                            val newTop = activity_marketing.height / 2 + marketing_proceed.height / 2
                            val translation = (newTop - currentTop).toFloat()

                            ValueAnimator.ofFloat(0f, translation).apply {
                                duration = 300
                                addUpdateListener { translation ->
                                    marketing_proceed.translationY = translation.animatedValue as Float
                                    val elapsed = translation.animatedFraction
                                    val backgroundColor = Color.rgb(
                                        (255 - 154 * elapsed).toInt(),
                                        (255 - 225 * elapsed).toInt(),
                                        255
                                    )
                                    val drawableWrap =
                                        DrawableCompat.wrap(marketing_proceed.background).mutate()
                                    DrawableCompat.setTint(drawableWrap, backgroundColor)
                                    val textColor = Color.rgb(
                                        (255 * elapsed).toInt(),
                                        (255 * elapsed).toInt(),
                                        (255 * elapsed).toInt()
                                    )
                                    marketing_proceed.setTextColor(textColor)
                                }
                                start()
                            }
                        }
                    })

                    marketingStoriesViewModel.paused.observe(this, Observer { shouldPause ->
                        if (shouldPause) {
                            animator.pause()
                        } else {
                            animator.resume()
                        }
                    })
                }
                marketingStoriesViewModel.page.observe(this, Observer { newPage ->
                    pager.currentItem = newPage ?: return@Observer
                })

                it.forEach { story ->
                    val asset = story.asset().get()
                    val mimetype = asset.mimeType().get()
                    val url = asset.url()
                    if (mimetype == "image/jpeg") {
                        Picasso.get().load(url).fetch()
                    } else if (mimetype == "video/mp4" || mimetype == "video/quicktime") {
                        val dataSourceFactory =
                            DefaultDataSourceFactory(this, Util.getUserAgent(this, BuildConfig.APPLICATION_ID))
                        AsyncTask.execute {
                            CacheUtil.cache(
                                DataSpec(Uri.parse(url)),
                                cache,
                                dataSourceFactory.createDataSource(),
                                null,
                                null
                            )
                        }
                    }
                }
            })
    }
}

