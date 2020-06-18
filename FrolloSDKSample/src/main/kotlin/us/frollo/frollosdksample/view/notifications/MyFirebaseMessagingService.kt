/*
 * Copyright 2019 Frollo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package us.frollo.frollosdksample.view.notifications

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import us.frollo.frollosdk.FrolloSDK

class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "MessagingService"
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        remoteMessage.data?.let { data ->
            if (data.isNotEmpty()) {
                FrolloSDK.notifications.handlePushNotification(data)
            }

            Log.d(TAG, "**** Woohooo!! Received Notification!!")
        }
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "**** New FCM token: $token")
        token.let { FrolloSDK.notifications.registerPushNotificationToken(it) }
    }
}
