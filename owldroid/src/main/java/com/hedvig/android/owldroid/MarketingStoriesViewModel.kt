package com.hedvig.android.owldroid

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.os.Handler
import android.os.Looper
import com.hedvig.android.owldroid.graphql.MarketingStoriesQuery
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.multibindings.IntoMap
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton
import kotlin.reflect.KClass

class MarketingStoriesViewModel @Inject constructor(private val marketingStoriesRepository: MarketingStoriesRepository) :
    ViewModel() {

    val marketingStories = MutableLiveData<List<MarketingStoriesQuery.MarketingStory>>()
    val page = MutableLiveData<Int>()
    val paused = MutableLiveData<Boolean>()
    val blurred = MutableLiveData<Boolean>()

    init {
        loadMarketingStories()
        startFirstStory()
    }

    private fun startFirstStory() {
        page.value = 0
    }

    private fun loadMarketingStories() {
        marketingStoriesRepository.fetchMarketingStories {
            val handler = Handler(Looper.getMainLooper())
            handler.post {
                marketingStories.value = it
            }
        }
    }

    fun nextScreen() {
        val currentStoryIndex = page.value ?: 0
        val nScreens = marketingStories.value?.size ?: return
        if (currentStoryIndex + 1 > nScreens) {
            return
        } else if (currentStoryIndex + 1 == nScreens) {
            blurred.value = true
        }
        page.value = currentStoryIndex + 1
    }

    fun previousScreen() {
        val currentStoryIndex = page.value ?: 0
        if (currentStoryIndex - 1 < 0) {
            return
        }
        page.value = currentStoryIndex - 1
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

@Singleton
class ViewModelFactory @Inject constructor(private val viewModels: MutableMap<Class<out ViewModel>, Provider<ViewModel>>) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = viewModels[modelClass]?.get() as T
}

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@Retention(AnnotationRetention.RUNTIME)
@MapKey
annotation class ViewModelKey(val value: KClass<out ViewModel>)

@Module
abstract class ViewModelModule {
    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(MarketingStoriesViewModel::class)
    internal abstract fun marketingStoriesViewModel(viewModel: MarketingStoriesViewModel): ViewModel
}

