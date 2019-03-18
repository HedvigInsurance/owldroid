package com.hedvig.android.owldroid.ui.marketing

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.hedvig.android.owldroid.data.marketing.MarketingStoriesRepository
import com.hedvig.android.owldroid.graphql.MarketingStoriesQuery
import javax.inject.Inject

class MarketingStoriesViewModel @Inject constructor(private val marketingStoriesRepository: MarketingStoriesRepository) :
        ViewModel() {

    val marketingStories = MutableLiveData<List<MarketingStoriesQuery.MarketingStory>>()
    val page = MutableLiveData<Int>()
    val paused = MutableLiveData<Boolean>()
    val blurred = MutableLiveData<Boolean>()

    fun loadAndStart() {
        loadMarketingStories()
        startFirstStory()
    }

    private fun startFirstStory() {
        page.value = 0
    }

    private fun loadMarketingStories() {
        marketingStoriesRepository.fetchMarketingStories { stories ->
            marketingStories.postValue(stories)
        }
    }

    fun nextScreen(): Boolean {
        val currentStoryIndex = page.value ?: 0
        val nScreens = marketingStories.value?.size ?: return false
        if (currentStoryIndex + 1 > nScreens) {
            return false
        } else if (currentStoryIndex + 1 == nScreens) {
            blurred.value = true
        }
        page.value = currentStoryIndex + 1
        return true
    }

    fun previousScreen(): Boolean {
        val currentStoryIndex = page.value ?: 0
        if (currentStoryIndex - 1 < 0) {
            return false
        }
        page.value = currentStoryIndex - 1
        return true
    }

    fun pauseStory() {
        paused.value = true
    }

    fun resumeStory() {
        paused.value = false
    }

    fun unblur() {
        page.value = 0
        blurred.value = false
    }
}

