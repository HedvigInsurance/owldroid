package com.hedvig.android.owldroid.ui.marketing

import android.animation.ValueAnimator
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.view.ViewPager
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.support.v7.widget.AppCompatButton
import android.view.GestureDetector
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.OvershootInterpolator
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.airbnb.lottie.LottieAnimationView
import com.hedvig.android.owldroid.R
import com.hedvig.android.owldroid.di.ViewModelFactory
import com.hedvig.android.owldroid.graphql.MarketingStoriesQuery
import com.hedvig.android.owldroid.util.OnSwipeListener
import com.hedvig.android.owldroid.util.SimpleOnSwipeListener
import com.hedvig.android.owldroid.util.compatSetTint
import com.hedvig.android.owldroid.util.doOnEnd
import com.hedvig.android.owldroid.util.percentageFade
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
        val view = inflater.inflate(
            R.layout.activity_marketing,
            container,
            false
        ) as ConstraintLayout
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
            if (blurred == null || !blurred) {
                blur_overlay.visibility = View.GONE
                return@Observer
            }

            blur_overlay.visibility = View.VISIBLE
            ValueAnimator.ofInt(0, 100).apply {
                duration = 300
                addUpdateListener { opacity ->
                    val backgroundColor = percentageFade(
                        resources.getColor(R.color.transparent_white),
                        resources.getColor(R.color.blur_white),
                        opacity.animatedFraction
                    )
                    blur_overlay.setBackgroundColor(backgroundColor)
                }
                start()
            }

            val swipeListener = GestureDetector(context, SimpleOnSwipeListener {
                when (it) {
                    OnSwipeListener.Direction.DOWN -> {
                        blur_overlay.setOnTouchListener(null)
                        hedvig_face_animation.visibility = LottieAnimationView.GONE
                        marketing_say_hello.visibility = TextView.GONE
                        ValueAnimator.ofFloat(marketing_proceed.translationY, 0f).apply {
                            duration = 200
                            interpolator = FastOutSlowInInterpolator()
                            addUpdateListener { translation ->
                                marketing_proceed.translationY = translation.animatedValue as Float
                                val elapsed = translation.animatedFraction
                                val backgroundColor = percentageFade(
                                    resources.getColor(R.color.purple),
                                    resources.getColor(R.color.white),
                                    elapsed
                                )
                                marketing_proceed.background.compatSetTint(backgroundColor)
                                val textColor = percentageFade(
                                    resources.getColor(R.color.white),
                                    resources.getColor(R.color.black),
                                    elapsed
                                )
                                marketing_proceed.setTextColor(textColor)
                            }
                            doOnEnd {
                                marketingStoriesViewModel.unblur()
                            }
                            start()
                        }
                        ValueAnimator.ofInt(0, 100).apply {
                            duration = 200
                            addUpdateListener { opacity ->
                                val backgroundColor = percentageFade(
                                    resources.getColor(R.color.blur_white),
                                    resources.getColor(R.color.transparent_white),
                                    opacity.animatedFraction
                                )
                                blur_overlay.setBackgroundColor(backgroundColor)
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
                    val backgroundColor = percentageFade(
                        resources.getColor(R.color.white),
                        resources.getColor(R.color.purple),
                        elapsed
                    )
                    marketing_proceed.background.compatSetTint(backgroundColor)
                    val textColor = percentageFade(
                        resources.getColor(R.color.black),
                        resources.getColor(R.color.white),
                        elapsed
                    )
                    marketing_proceed.setTextColor(textColor)
                }
                doOnEnd {
                    marketing_say_hello.translationY = translation
                    marketing_say_hello.visibility = TextView.VISIBLE
                    hedvig_face_animation.useHardwareAcceleration(true)
                    hedvig_face_animation.visibility = LottieAnimationView.VISIBLE
                    hedvig_face_animation.translationY = translation
                    hedvig_face_animation.playAnimation()
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
            it.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            val intent = Intent("marketingResult")
            intent.putExtra("type", MarketingResult.ONBOARD)
            localBroadcastManager.sendBroadcast(intent)
        }
    }
}