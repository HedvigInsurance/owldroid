package com.hedvig.android.owldroid.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.rx2.Rx2Apollo
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.hedvig.android.owldroid.R
import com.hedvig.android.owldroid.graphql.NewSessionMutation
import com.hedvig.android.owldroid.graphql.RegisterPushTokenMutation
import com.hedvig.android.owldroid.util.react.AsyncStorageNative
import com.hedvig.android.owldroid.util.whenApiVersion
import dagger.android.AndroidInjection
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import timber.log.Timber
import javax.inject.Inject

class PushNotificationService : FirebaseMessagingService() {

    @Inject
    lateinit var asyncStorageNative: AsyncStorageNative

    @Inject
    lateinit var apolloClient: ApolloClient

    private val disposables = CompositeDisposable()

    override fun onCreate() {
        super.onCreate()
        AndroidInjection.inject(this)
        setupNotificationChannel()
    }

    override fun onDestroy() {
        disposables.dispose()
        super.onDestroy()
    }

    override fun onNewToken(token: String?) {
        token?.let { pushToken ->
            if (!hasHedvigToken()) {
                acquireHedvigToken {
                    registerPushToken(pushToken)
                }
                return
            }
            registerPushToken(pushToken)
        }
    }

    private fun acquireHedvigToken(done: () -> Unit) {
        disposables += Rx2Apollo
            .from(apolloClient.mutate(NewSessionMutation()))
            .subscribe({ response ->
                if (response.hasErrors()) {
                    Timber.e("Failed to register a hedvig token: %s", response.errors().toString())
                    return@subscribe
                }
                response.data()?.createSessionV2()?.token()?.let { hedvigToken ->
                    asyncStorageNative.setKey(HEDVIG_TOKEN, hedvigToken)
                    done()
                } ?: Timber.e("createSession returned no token")
            }, { Timber.e(it) })
    }

    override fun onMessageReceived(p0: RemoteMessage?) {
        sendChatMessageNotification()
    }

    private fun setupNotificationChannel() = whenApiVersion(Build.VERSION_CODES.O) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        notificationManager?.createNotificationChannel(
            NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                resources.getString(R.string.NOTIFICATION_CHANNEL_NAME),
                NotificationManager.IMPORTANCE_HIGH
            ).apply { description = resources.getString(R.string.NOTIFICATION_CHANNEL_DESCRIPTION) }
        )
    }

    private fun sendChatMessageNotification() {
        val notification = NotificationCompat
            .Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_hedvig_symbol_android)
            .setContentTitle(resources.getString(R.string.NOTIFICATION_CHAT_TITLE))
            .setContentText(resources.getString(R.string.NOTIFICATION_CHAT_BODY))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setAutoCancel(true)
            .setChannelId(NOTIFICATION_CHANNEL_ID)
            // Set an action here
            .build()

        NotificationManagerCompat
            .from(this)
            .notify(NOTIFICATION_ID, notification)
    }

    private fun hasHedvigToken(): Boolean {
        try {
            asyncStorageNative.getKey("@hedvig:token")
            return true
        } catch (_: Exception) {
            return false
        }
    }

    private fun registerPushToken(pushToken: String) {
        val registerPushTokenMutation = RegisterPushTokenMutation
            .builder()
            .pushToken(pushToken)
            .build()

        disposables += Rx2Apollo
            .from(apolloClient.mutate(registerPushTokenMutation))
            .subscribe({ response ->
                if (response.hasErrors()) {
                    Timber.e("Failed to register push token: %s", response.errors().toString())
                    return@subscribe
                }
                Timber.i("Successfully registered push token")
            }, { Timber.e(it, "Failed to register push token") })
    }

    companion object {
        private const val HEDVIG_TOKEN = "@hedvig:token"
        const val NOTIFICATION_CHANNEL_ID = "hedvig-push"
        const val NOTIFICATION_ID = 1 // TODO: Better logic for this
    }
}
