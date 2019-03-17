package com.hedvig.android.owldroid.ui.marketing

import android.animation.ValueAnimator
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.view.GestureDetector
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.OvershootInterpolator
import android.widget.ProgressBar
import com.google.firebase.analytics.FirebaseAnalytics
import com.hedvig.android.owldroid.R
import com.hedvig.android.owldroid.di.ViewModelFactory
import com.hedvig.android.owldroid.graphql.MarketingStoriesQuery
import com.hedvig.android.owldroid.util.OnSwipeListener
import com.hedvig.android.owldroid.util.SimpleOnSwipeListener
import com.hedvig.android.owldroid.util.extensions.compatColor
import com.hedvig.android.owldroid.util.extensions.compatSetTint
import com.hedvig.android.owldroid.util.extensions.doOnEnd
import com.hedvig.android.owldroid.util.extensions.localBroadcastManager
import com.hedvig.android.owldroid.util.extensions.remove
import com.hedvig.android.owldroid.util.extensions.show
import com.hedvig.android.owldroid.util.percentageFade
import com.hedvig.android.owldroid.util.whenApiVersion
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_marketing.*
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
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private var buttonsAnimator: ValueAnimator? = null
    private var blurDismissAnimator: ValueAnimator? = null
    private var blurShowAnimator: ValueAnimator? = null
    private var topHideAnimation: ValueAnimator? = null
    private var topShowAnimation: ValueAnimator? = null

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideStatusBar()
        marketingStoriesViewModel = requireActivity().run {
            ViewModelProviders.of(this, viewModelFactory).get(MarketingStoriesViewModel::class.java)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_marketing, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        whenApiVersion(Build.VERSION_CODES.LOLLIPOP) {
            marketing_proceed.elevation = 2f
            marketing_login.elevation = 2f
        }
        observeMarketingStories()
    }

    override fun onStop() {
        super.onStop()
        buttonsAnimator?.removeAllListeners()
        buttonsAnimator?.cancel()
        blurDismissAnimator?.removeAllListeners()
        blurDismissAnimator?.cancel()
        blurShowAnimator?.removeAllListeners()
        blurShowAnimator?.cancel()
        topHideAnimation?.removeAllListeners()
        topHideAnimation?.cancel()
        topShowAnimation?.removeAllListeners()
        topShowAnimation?.cancel()
    }

    private fun observeMarketingStories() {
        marketingStoriesViewModel
            .marketingStories
            .observe(this, Observer {
                loadingSpinner.remove()
                setupButtons()
                setupPager(it)
                setupBlurOverlay()
            })
        marketingStoriesViewModel.loadAndStart()
    }

    // TODO: Refactor this function to be smaller, to be more safe (do not throw exceptions), and to
    // cancel its animations when this fragment is completed, or else it will do bad stuff
    private fun setupPager(stories: List<MarketingStoriesQuery.MarketingStory>?) {
        val nStories = stories?.size ?: throw Exception("Got no stories")
        pager.adapter = StoryPagerAdapter(
            activity?.supportFragmentManager ?: throw Error("Could not find fragment manager"),
            nStories
        )
        pager.show()
        story_progress_indicator_container.show()
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
                duration = (stories[n].duration()?.toLong() ?: 3) * 1000
                addUpdateListener { va ->
                    progressBar.progress = (va.animatedValue as Float).toInt()
                }
            }

            marketingStoriesViewModel.page.observe(this, Observer { newPage ->
                animator.removeAllListeners()
                animator.cancel()
                newPage?.let {
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
                }
            })

            marketingStoriesViewModel.paused.observe(this, Observer { shouldPause ->
                shouldPause?.let {
                    if (shouldPause) {
                        animator.pause()
                    } else {
                        animator.resume()
                    }
                }
            })
        }
        marketingStoriesViewModel.page.observe(this, Observer { newPage ->
            if (newPage == null) {
                return@Observer
            }
            trackViewedStory(newPage)
            pager.currentItem = newPage
        })
    }


    private fun setupBlurOverlay() {
        marketingStoriesViewModel.blurred.observe(this, Observer { blurred ->
            if (blurred == null || !blurred) {
                blur_overlay.remove()
                return@Observer
            }

            blur_overlay.show()
            topHideAnimation = ValueAnimator.ofFloat(1f, 0f).apply {
                duration = 300
                addUpdateListener { opacity ->
                    marketing_hedvig_logo.alpha = opacity.animatedValue as Float
                    story_progress_indicator_container.alpha = opacity.animatedValue as Float
                }
                doOnEnd {
                    marketing_hedvig_logo.remove()
                    story_progress_indicator_container.remove()
                }
                start()
            }

            blurShowAnimator = ValueAnimator.ofInt(0, 100).apply {
                duration = 300
                addUpdateListener { opacity ->
                    val backgroundColor = percentageFade(
                        requireContext().compatColor(R.color.transparent_white),
                        requireContext().compatColor(R.color.blur_white),
                        opacity.animatedFraction
                    )
                    blur_overlay.setBackgroundColor(backgroundColor)
                }
                start()
            }

            val swipeListener = GestureDetector(context, SimpleOnSwipeListener {
                when (it) {
                    OnSwipeListener.Direction.DOWN -> {
                        trackDismissBlurOverlay()
                        blur_overlay.setOnTouchListener(null)
                        hedvig_face_animation.remove()
                        marketing_say_hello.remove()
                        marketing_hedvig_logo.show()
                        story_progress_indicator_container.show()

                        blurDismissAnimator = ValueAnimator.ofFloat(marketing_proceed.translationY, 0f).apply {
                            duration = 200
                            interpolator = FastOutSlowInInterpolator()
                            addUpdateListener { translation ->
                                marketing_proceed.translationY = translation.animatedValue as Float
                                val elapsed = translation.animatedFraction
                                val backgroundColor = percentageFade(
                                    requireContext().compatColor(R.color.purple),
                                    requireContext().compatColor(R.color.white),
                                    elapsed
                                )
                                marketing_proceed.background.compatSetTint(backgroundColor)
                                val textColor = percentageFade(
                                    requireContext().compatColor(R.color.white),
                                    requireContext().compatColor(R.color.black),
                                    elapsed
                                )
                                marketing_proceed.setTextColor(textColor)
                            }
                            doOnEnd {
                                marketingStoriesViewModel.unblur()
                            }
                            start()
                        }
                        topShowAnimation = ValueAnimator.ofInt(0, 100).apply {
                            duration = 200
                            addUpdateListener { opacity ->
                                marketing_hedvig_logo.alpha = opacity.animatedFraction
                                story_progress_indicator_container.alpha = opacity.animatedFraction

                                val backgroundColor = percentageFade(
                                    requireContext().compatColor(R.color.blur_white),
                                    requireContext().compatColor(R.color.transparent_white),
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


            val currentTop = marketing_proceed.top
            val newTop = activity_marketing.height / 2 + marketing_proceed.height / 2
            val translation = (newTop - currentTop).toFloat()

            buttonsAnimator = ValueAnimator.ofFloat(0f, translation).apply {
                duration = 500
                interpolator = OvershootInterpolator()
                addUpdateListener { translation ->
                    marketing_proceed.translationY = translation.animatedValue as Float
                    val elapsed = translation.animatedFraction
                    val backgroundColor = percentageFade(
                        requireContext().compatColor(R.color.white),
                        requireContext().compatColor(R.color.purple),
                        elapsed
                    )
                    marketing_proceed.background.compatSetTint(backgroundColor)
                    val textColor = percentageFade(
                        requireContext().compatColor(R.color.black),
                        requireContext().compatColor(R.color.white),
                        elapsed
                    )
                    marketing_proceed.setTextColor(textColor)
                }
                doOnEnd {
                    marketing_say_hello.translationY = translation
                    marketing_say_hello.show()
                    hedvig_face_animation.useHardwareAcceleration(true)
                    hedvig_face_animation.show()
                    hedvig_face_animation.translationY = translation
                    hedvig_face_animation.playAnimation()
                    blur_overlay.setOnTouchListener { _, motionEvent ->
                        swipeListener.onTouchEvent(motionEvent)
                        true
                    }
                }
                start()
            }
        })
    }

    private fun setupButtons() {
        marketing_login.show()
        marketing_proceed.show()

        marketing_login.setOnClickListener {
            trackClickLogin()
            it.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            restoreStatusBar()
            val intent = Intent("marketingResult")
            intent.putExtra("type", MarketingResult.LOGIN)
            localBroadcastManager.sendBroadcast(intent)
        }

        marketing_proceed.setOnClickListener {
            trackClickGetHedvig()
            it.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            restoreStatusBar()
            val intent = Intent("marketingResult")
            intent.putExtra("type", MarketingResult.ONBOARD)
            localBroadcastManager.sendBroadcast(intent)
        }
    }

    private fun hideStatusBar() {
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    private fun restoreStatusBar() {
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    private fun trackViewedStory(storyIndex: Int) {
        val bundle = Bundle()
        bundle.putInt("story_number", storyIndex + 1)
        firebaseAnalytics.logEvent("viewed_story", bundle)
    }

    private fun trackClickGetHedvig() {
        val bundle = Bundle()
        marketingStoriesViewModel.page.value?.let { bundle.putInt("story_number", it + 1) }
        bundle.putBoolean("final_screen_active", marketingStoriesViewModel.blurred.value ?: false)
        firebaseAnalytics.logEvent("click_get_hedvig", bundle)
    }

    private fun trackClickLogin() {
        val bundle = Bundle()
        marketingStoriesViewModel.page.value?.let { bundle.putInt("story_number", it + 1) }
        bundle.putBoolean("final_screen_active", marketingStoriesViewModel.blurred.value ?: false)
        firebaseAnalytics.logEvent("click_login", bundle)
    }

    private fun trackDismissBlurOverlay() {
        firebaseAnalytics.logEvent("dismiss_blur_overlay", null)
    }
}
