package com.hedvig.android.owldroid.data.marketing

import android.content.Context
import android.net.Uri
import android.os.AsyncTask
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.CacheUtil
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.google.android.exoplayer2.util.Util
import com.hedvig.android.owldroid.graphql.MarketingStoriesQuery
import com.squareup.picasso.BuildConfig
import com.squareup.picasso.Picasso
import timber.log.Timber
import java.lang.Exception
import java.lang.ref.WeakReference

class CacheAssetTask(
    context: Context,
    val cache: SimpleCache,
    val asset: MarketingStoriesQuery.Asset,
    private val onEnd: (() -> Unit)? = null
) : AsyncTask<Unit, Unit, Unit>() {
    private val contextRef: WeakReference<Context> = WeakReference(context)

    override fun doInBackground(vararg params: Unit?) {
        try {
            val mimeType = asset.mimeType()
            val url = asset.url()
            if (mimeType == "image/jpeg") {
                // TODO Figure out how to make this block the completion of the AsyncTask
                Picasso.get().load(url).fetch()
            } else if (mimeType == "video/mp4" || mimeType == "video/quicktime") {
                contextRef.get()?.let { context ->
                    val dataSourceFactory =
                        DefaultDataSourceFactory(
                            context, Util.getUserAgent(
                                context,
                                BuildConfig.APPLICATION_ID
                            )
                        )
                    CacheUtil.cache(
                        DataSpec(Uri.parse(url)),
                        cache,
                        dataSourceFactory.createDataSource(),
                        null
                    )
                }
            }
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    override fun onPostExecute(result: Unit?) {
        onEnd?.let { it() }
    }
}
