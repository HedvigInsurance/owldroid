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
import com.hedvig.android.owldroid.graphql.RegisterPushTokenMutation
import com.hedvig.android.owldroid.util.react.AsyncStorageNativeReader
import com.hedvig.android.owldroid.util.whenApiVersion
import dagger.android.AndroidInjection
import timber.log.Timber
import javax.inject.Inject

class PushNotificationService: FirebaseMessagingService() {

    @Inject
    lateinit var asyncStorageNativeReader: AsyncStorageNativeReader

    @Inject
    lateinit var apolloClient: ApolloClient

    override fun onCreate() {
        super.onCreate()
        AndroidInjection.inject(this)
        setupNotificationChannel()
    }

    override fun onNewToken(token: String?) {
        token?.let { registerPushToken(it) }
    }

    override fun onMessageReceived(p0: RemoteMessage?) {
        sendChatMessageNotification()
    }

    private fun setupNotificationChannel() {
        whenApiVersion(Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            notificationManager?.createNotificationChannel(
                NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    resources.getString(R.string.NOTIFICATION_CHANNEL_NAME),
                    NotificationManager.IMPORTANCE_HIGH
                ).apply { description = resources.getString(R.string.NOTIFICATION_CHANNEL_DESCRIPTION) }
            )
        }
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

    private fun registerPushToken(token: String) {
        val hedvigToken = try {
            asyncStorageNativeReader.getKey("@hedvig:token")
        } catch (exception: Exception) {
            null
        }

        if (hedvigToken == null) {
            Timber.e("There is no Hedvig token, will not register a push token")
            // TODO: Do a HelloHedvig, either from native or from javascript
        }

        val registerPushTokenMutation = RegisterPushTokenMutation
            .builder()
            .pushToken(token)
            .build()

        val disposable = Rx2Apollo
            .from(apolloClient.mutate(registerPushTokenMutation))
            .subscribe({
                Timber.i("Successfully registered push token")
            }, { Timber.e(it, "Failed to register push token") })
    }

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "hedvig-push"
        const val NOTIFICATION_ID = 1 // TODO: Better logic for this
    }
}
