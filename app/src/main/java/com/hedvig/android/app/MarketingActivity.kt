package com.hedvig.android.app

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.animation.doOnEnd
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.squareup.picasso.Picasso
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_marketing.*
import javax.inject.Inject

class MarketingActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

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
                                    if (newPage != nStories - 1) {
                                        marketingStoriesViewModel.nextScreen()
                                    }
                                }
                                animator.start()
                            }
                            newPage < n -> progressBar.progress = 0
                            newPage > n -> progressBar.progress = 100
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

                // Warm up cache
                it.forEach { story ->
                    val asset = story.asset().get()
                    val mimetype = asset.mimeType().get()
                    val url = asset.url()
                    if (mimetype == "image/jpeg") {
                        Picasso.get().load(url).fetch()
                    } else if (mimetype == "video/mp4" || mimetype == "video/quicktime") {
                        // Cache videos
                    }
                }
            })
    }
}

