package com.hedvig.android.owldroid.ui.marketing

import android.animation.ValueAnimator
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
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
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.google.firebase.analytics.FirebaseAnalytics
import com.hedvig.android.owldroid.R
import com.hedvig.android.owldroid.di.ViewModelFactory
import com.hedvig.android.owldroid.graphql.MarketingStoriesQuery
import com.hedvig.android.owldroid.util.OnSwipeListener
import com.hedvig.android.owldroid.util.SimpleOnSwipeListener
import com.hedvig.android.owldroid.util.extensions.compatColor
import com.hedvig.android.owldroid.util.extensions.compatSetTint
import com.hedvig.android.owldroid.util.extensions.doOnEnd
import com.hedvig.android.owldroid.util.extensions.view.doOnLayout
import com.hedvig.android.owldroid.util.extensions.view.remove
import com.hedvig.android.owldroid.util.extensions.view.show
import com.hedvig.android.owldroid.util.percentageFade
import com.hedvig.android.owldroid.util.whenApiVersion
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_marketing.*
import kotlinx.android.synthetic.main.loading_spinner.*
import timber.log.Timber
import javax.inject.Inject
import com.hedvig.app.common.R as CommonR

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
    private var topHideAnimation: ValueAnimator? = null

    private val navController: NavController by lazy {
        requireActivity().findNavController(CommonR.id.rootNavigationHost)
    }

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
            getHedvig.elevation = 2f
            login.elevation = 2f
        }
        observeMarketingStories()
    }

    override fun onStop() {
        super.onStop()
        buttonsAnimator?.removeAllListeners()
        buttonsAnimator?.cancel()
        blurDismissAnimator?.removeAllListeners()
        blurDismissAnimator?.cancel()
        topHideAnimation?.removeAllListeners()
        topHideAnimation?.cancel()
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
    }

    // TODO: Refactor this function to be smaller, to be more safe (do not throw exceptions), and to
    // cancel its animations when this fragment is completed, or else it will do bad stuff
    private fun setupPager(stories: List<MarketingStoriesQuery.MarketingStory>?) {
        // FIXME Handle the zero stories case (wat do?)
        val nStories = stories?.size ?: return
        pager.adapter = StoryPagerAdapter(
            childFragmentManager,
            nStories
        )
        pager.show()
        pager.doOnLayout {
            marketingStoriesViewModel.startFirstStory()
        }
        storyProgressIndicatorContainer.show()
        val width = activity_marketing.width
        for (n in 0 until nStories) {
            val progressBar = layoutInflater.inflate(
                R.layout.marketing_progress_bar,
                storyProgressIndicatorContainer,
                false
            ) as ProgressBar
            progressBar.layoutParams = ViewGroup.LayoutParams(width / nStories, 10)
            storyProgressIndicatorContainer.addView(progressBar)
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
            try {
                pager.currentItem = newPage
            } catch (e: IllegalStateException) {
                Timber.e(e)
            }
        })
    }

    private fun setupBlurOverlay() {
        marketingStoriesViewModel.blurred.observe(this, Observer { blurred ->
            if (blurred == null || !blurred) {
                blurOverlay.remove()
                return@Observer
            }

            blurOverlay.show()
            topHideAnimation = ValueAnimator.ofFloat(1f, 0f).apply {
                duration = BLUR_ANIMATION_SHOW_DURATION
                addUpdateListener { opacity ->
                    marketing_hedvig_logo.alpha = opacity.animatedValue as Float
                    storyProgressIndicatorContainer.alpha = opacity.animatedValue as Float

                    val backgroundColor = percentageFade(
                        requireContext().compatColor(R.color.transparent_white),
                        requireContext().compatColor(R.color.blur_white),
                        opacity.animatedFraction
                    )
                    blurOverlay.setBackgroundColor(backgroundColor)
                }
                doOnEnd {
                    marketing_hedvig_logo.remove()
                    storyProgressIndicatorContainer.remove()
                }
                start()
            }

            val swipeListener = GestureDetector(context, SimpleOnSwipeListener { direction ->
                when (direction) {
                    OnSwipeListener.Direction.DOWN -> {
                        trackDismissBlurOverlay()
                        blurOverlay.setOnTouchListener(null)
                        hedvigFaceAnimation.remove()
                        sayHello.remove()
                        marketing_hedvig_logo.show()
                        storyProgressIndicatorContainer.show()

                        blurDismissAnimator = ValueAnimator.ofFloat(getHedvig.translationY, 0f).apply {
                            duration = BLUR_ANIMATION_DISMISS_DURATION
                            interpolator = FastOutSlowInInterpolator()
                            addUpdateListener { translation ->
                                getHedvig.translationY = translation.animatedValue as Float
                                val elapsed = translation.animatedFraction
                                val backgroundColor = percentageFade(
                                    requireContext().compatColor(R.color.purple),
                                    requireContext().compatColor(R.color.white),
                                    elapsed
                                )
                                getHedvig.background.compatSetTint(backgroundColor)
                                val textColor = percentageFade(
                                    requireContext().compatColor(R.color.white),
                                    requireContext().compatColor(R.color.black),
                                    elapsed
                                )
                                getHedvig.setTextColor(textColor)

                                marketing_hedvig_logo.alpha = translation.animatedFraction
                                storyProgressIndicatorContainer.alpha = translation.animatedFraction

                                val blurBackgroundColor = percentageFade(
                                    requireContext().compatColor(R.color.blur_white),
                                    requireContext().compatColor(R.color.transparent_white),
                                    translation.animatedFraction
                                )
                                blurOverlay.setBackgroundColor(blurBackgroundColor)
                            }
                            doOnEnd {
                                marketingStoriesViewModel.unblur()
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

            val currentTop = getHedvig.top
            val newTop = activity_marketing.height / 2 + getHedvig.height / 2
            val translation = (newTop - currentTop).toFloat()

            buttonsAnimator = ValueAnimator.ofFloat(0f, translation).apply {
                duration = BUTTON_ANIMATION_DURATION
                interpolator = OvershootInterpolator()
                addUpdateListener { translation ->
                    getHedvig.translationY = translation.animatedValue as Float
                    val elapsed = translation.animatedFraction
                    val backgroundColor = percentageFade(
                        requireContext().compatColor(R.color.white),
                        requireContext().compatColor(R.color.purple),
                        elapsed
                    )
                    getHedvig.background.compatSetTint(backgroundColor)
                    val textColor = percentageFade(
                        requireContext().compatColor(R.color.black),
                        requireContext().compatColor(R.color.white),
                        elapsed
                    )
                    getHedvig.setTextColor(textColor)
                }
                doOnEnd {
                    sayHello.translationY = translation
                    sayHello.show()
                    hedvigFaceAnimation.useHardwareAcceleration(true)
                    hedvigFaceAnimation.show()
                    hedvigFaceAnimation.translationY = translation
                    hedvigFaceAnimation.playAnimation()
                    blurOverlay.setOnTouchListener { _, motionEvent ->
                        swipeListener.onTouchEvent(motionEvent)
                        true
                    }
                }
                start()
            }
        })
    }

    private fun setupButtons() {
        login.show()
        getHedvig.show()

        login.setOnClickListener { view ->
            trackClickLogin()
            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            restoreStatusBar()
            val args = Bundle()
            args.putString("intent", "login")
            args.putBoolean("show_restart", true)
            navController.navigate(CommonR.id.action_marketingFragment_to_chatFragment, args)
        }

        getHedvig.setOnClickListener { view ->
            trackClickGetHedvig()
            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            restoreStatusBar()
            val args = Bundle()
            args.putString("intent", "onboarding")
            args.putBoolean("show_restart", true)
            navController.navigate(CommonR.id.action_marketingFragment_to_chatFragment, args)
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

    companion object {
        const val BUTTON_ANIMATION_DURATION = 500L
        const val BLUR_ANIMATION_SHOW_DURATION = 300L
        const val BLUR_ANIMATION_DISMISS_DURATION = 200L
    }
}
