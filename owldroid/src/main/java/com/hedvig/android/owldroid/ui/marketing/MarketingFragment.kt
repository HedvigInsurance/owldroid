package com.hedvig.android.owldroid.ui.marketing

import android.animation.ValueAnimator
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.view.ViewPager
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.support.v7.widget.AppCompatButton
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.OvershootInterpolator
import android.widget.LinearLayout
import android.widget.ProgressBar
import com.hedvig.android.owldroid.R
import com.hedvig.android.owldroid.di.ViewModelFactory
import com.hedvig.android.owldroid.graphql.MarketingStoriesQuery
import com.hedvig.android.owldroid.util.OnSwipeListener
import com.hedvig.android.owldroid.util.SimpleOnSwipeListener
import com.hedvig.android.owldroid.util.compatSetTint
import com.hedvig.android.owldroid.util.doOnEnd
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.activity_marketing.*
import javax.inject.Inject

class MarketingFragment : Fragment() {

    enum class MarketingResult {
        ONBOARD,
        LOGIN;

        override fun toString(): String {
            return when (this) {
                ONBOARD -> "onboard"
                LOGIN -> "login"
            }
        }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var marketingStoriesViewModel: MarketingStoriesViewModel

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        marketingStoriesViewModel = activity?.run {
            ViewModelProviders.of(this, viewModelFactory).get(MarketingStoriesViewModel::class.java)
        } ?: throw Exception("No Activity")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.activity_marketing, container, false) as ConstraintLayout
        observeMarketingStories()
        return view
    }

    private fun observeMarketingStories() {
        marketingStoriesViewModel
            .marketingStories
            .observe(this, Observer {
                setupButtons()
                setupPager(it)
                setupBlurOverlay()
            })
    }

    private fun setupPager(stories: List<MarketingStoriesQuery.MarketingStory>?) {
        val nStories = stories?.size ?: throw Exception("Got no stories")
        pager.adapter = StoryPageAdapter(
            activity?.supportFragmentManager ?: throw Error("Could not find fragment manager"),
            nStories
        )
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
                duration = stories[n].duration().get().toLong() * 1000
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
                    newPage!! < n -> progressBar.progress = 0
                    newPage > n -> progressBar.progress = 100
                }
            })


            marketingStoriesViewModel.paused.observe(this, Observer { shouldPause ->
                if (shouldPause!!) {
                    animator.pause()
                } else {
                    animator.resume()
                }
            })
        }
        marketingStoriesViewModel.page.observe(this, Observer { newPage ->
            pager.currentItem = newPage ?: return@Observer
        })
    }

    private fun setupBlurOverlay() {
        marketingStoriesViewModel.blurred.observe(this, Observer { blurred ->
            if (!blurred!!) {
                blur_overlay.visibility = View.GONE
                return@Observer
            }

            blur_overlay.visibility = View.VISIBLE
            ValueAnimator.ofInt(0, 230).apply {
                duration = 300
                addUpdateListener { opacity ->
                    blur_overlay.setBackgroundColor(
                        Color.argb(
                            opacity.animatedValue as Int,
                            255,
                            255,
                            255
                        )
                    )
                }
                start()
            }

            val swipeListener = GestureDetector(context, SimpleOnSwipeListener {
                when (it) {
                    OnSwipeListener.Direction.DOWN -> {
                        blur_overlay.setOnTouchListener(null)
                        ValueAnimator.ofFloat(marketing_proceed.translationY, 0f).apply {
                            duration = 200
                            interpolator = FastOutSlowInInterpolator()
                            addUpdateListener { translation ->
                                marketing_proceed.translationY = translation.animatedValue as Float
                                val elapsed = translation.animatedFraction
                                val backgroundColor = Color.rgb(
                                    (101 + 154 * elapsed).toInt(),
                                    (30 + 225 * elapsed).toInt(),
                                    255
                                )
                                marketing_proceed.background.compatSetTint(backgroundColor)
                                val textColor = Color.rgb(
                                    (255 - 255 * elapsed).toInt(),
                                    (255 - 255 * elapsed).toInt(),
                                    (255 - 255 * elapsed).toInt()
                                )
                                marketing_proceed.setTextColor(textColor)
                            }
                            doOnEnd {
                                marketingStoriesViewModel.unblur()
                            }
                            start()
                        }
                        ValueAnimator.ofInt(230, 0).apply {
                            duration = 200
                            addUpdateListener { opacity ->
                                blur_overlay.setBackgroundColor(
                                    Color.argb(
                                        opacity.animatedValue as Int,
                                        255,
                                        255,
                                        255
                                    )
                                )
                            }
                            start()
                        }
                        true
                    }
                    else -> {
                        false
                    }
                }
            })

            blur_overlay.setOnTouchListener { _, motionEvent ->
                swipeListener.onTouchEvent(motionEvent)
                true
            }

            val currentTop = marketing_proceed.top
            val newTop = activity_marketing.height / 2 + marketing_proceed.height / 2
            val translation = (newTop - currentTop).toFloat()

            ValueAnimator.ofFloat(0f, translation).apply {
                duration = 500
                interpolator = OvershootInterpolator()
                addUpdateListener { translation ->
                    marketing_proceed.translationY = translation.animatedValue as Float
                    val elapsed = translation.animatedFraction
                    val backgroundColor = Color.rgb(
                        minOf((255 - 154 * elapsed).toInt(), 255),
                        minOf((255 - 225 * elapsed).toInt(), 255),
                        255
                    )
                    marketing_proceed.background.compatSetTint(backgroundColor)
                    val textColor = Color.rgb(
                        minOf((255 * elapsed).toInt(), 255),
                        minOf((255 * elapsed).toInt(), 255),
                        minOf((255 * elapsed).toInt(), 255)
                    )
                    marketing_proceed.setTextColor(textColor)
                }
                start()
            }
        })
    }

    private fun setupButtons() {
        val localBroadcastManager = LocalBroadcastManager.getInstance(context!!)
        marketing_login.visibility = AppCompatButton.VISIBLE
        marketing_proceed.visibility = AppCompatButton.VISIBLE

        marketing_login.setOnClickListener {
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            val intent = Intent("marketingResult")
            intent.putExtra("type", MarketingResult.LOGIN)
            localBroadcastManager.sendBroadcast(intent)
        }

        marketing_proceed.setOnClickListener {
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            val intent = Intent("marketingResult")
            intent.putExtra("type", MarketingResult.ONBOARD)
            localBroadcastManager.sendBroadcast(intent)
        }
    }
}