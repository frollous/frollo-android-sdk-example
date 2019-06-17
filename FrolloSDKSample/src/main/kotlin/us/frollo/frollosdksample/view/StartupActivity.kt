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

package us.frollo.frollosdksample.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_startup.*
import org.jetbrains.anko.startActivity
import us.frollo.frollosdk.FrolloSDK
import us.frollo.frollosdksample.R
import us.frollo.frollosdksample.managers.SetupManager
import us.frollo.frollosdksample.utils.hide
import us.frollo.frollosdksample.utils.show
import us.frollo.frollosdksample.view.authentication.LoginActivity

class StartupActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "StartupActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_startup)

        progress_bar.show()

        SetupManager.setup(application) {
            completeStartup()
        }
    }

    private fun completeStartup() {
        progress_bar.hide()

        if (FrolloSDK.authentication.loggedIn) {
            handleNotification()
            FrolloSDK.refreshData()
            startActivity<MainActivity>()
        } else {
            startActivity<LoginActivity>()
        }

        finish()
    }

    private fun handleNotification() {
        intent.extras?.let {
            FrolloSDK.notifications.handlePushNotification(it)
        }
    }
}
