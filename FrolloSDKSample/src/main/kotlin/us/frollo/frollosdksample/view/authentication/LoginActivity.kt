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

package us.frollo.frollosdksample.view.authentication

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import us.frollo.frollosdk.FrolloSDK
import us.frollo.frollosdk.base.Result
import us.frollo.frollosdksample.*
import us.frollo.frollosdksample.utils.displayError
import us.frollo.frollosdksample.utils.hide
import us.frollo.frollosdksample.utils.show
import us.frollo.frollosdksample.view.MainActivity

class LoginActivity : AppCompatActivity() {

    companion object {
        private const val EXTRA_FAILED = "failed"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        btn_login.setOnClickListener { attemptLogin() }
        btn_login_web.setOnClickListener { startAuthorizationCodeFlow() }

        if (intent.getBooleanExtra(EXTRA_FAILED, false)) {
            toast("Authorization cancelled")
        }
    }

    private fun attemptLogin() {
        val email = input_email.text.toString()
        val password = input_password.text.toString()

        if (email.isBlank() || password.isBlank())
            return

        btn_login.hide()
        btn_login_web.hide()
        progress_bar.show()

        FrolloSDK.authentication.loginUser(email = email, password = password) { result ->
            progress_bar.hide()

            when (result.status) {
                Result.Status.SUCCESS -> {
                    FrolloSDK.refreshData()
                    startActivity<MainActivity>()
                    finish()
                }
                Result.Status.ERROR -> {
                    btn_login.show()
                    btn_login_web.show()
                    displayError(result.error?.localizedDescription, "Login Failed")
                }
            }
        }
    }

    private fun startAuthorizationCodeFlow() {
        val completionIntent = Intent(this, LoginWebActivity::class.java)
        val cancelIntent = Intent(this, LoginActivity::class.java)
        cancelIntent.putExtra(EXTRA_FAILED, true)
        cancelIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

        FrolloSDK.authentication.loginUserUsingWeb(
                activity = this,
                completedIntent = PendingIntent.getActivity(this, 0, completionIntent, 0),
                cancelledIntent = PendingIntent.getActivity(this, 0, cancelIntent, 0),
                toolBarColor = resources.getColor(R.color.colorPrimary, null))
    }
}
