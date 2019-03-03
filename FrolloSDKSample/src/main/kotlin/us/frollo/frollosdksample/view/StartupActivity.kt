package us.frollo.frollosdksample.view

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_startup.*
import org.jetbrains.anko.startActivity
import us.frollo.frollosdk.FrolloSDK
import us.frollo.frollosdk.base.Result
import us.frollo.frollosdk.core.FrolloSDKConfiguration
import us.frollo.frollosdk.logging.LogLevel
import us.frollo.frollosdksample.R
import us.frollo.frollosdksample.utils.hide
import us.frollo.frollosdksample.utils.show
import us.frollo.frollosdksample.view.authentication.LoginAuthorizationCodeActivity
import us.frollo.frollosdksample.view.authentication.LoginROPCActivity
import us.frollo.frollosdksample.view.authentication.LoginType

class StartupActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "StartupActivity"
    }

    // Every time you change this make sure to clear App Data in Settings for the change to take effect
    private val loginType = LoginType.ROPC

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_startup)

        val configuration = when (loginType) {
            LoginType.AUTHORIZATION_CODE -> FrolloSDKConfiguration(
                    clientId = "PzlborkOwZf42SJ2b6Fdj6JTi9lcqiNi",
                    redirectUrl = "frollo-sdk-example://authorize",
                    authorizationUrl = "https://frollo-test.au.auth0.com/authorize/",
                    tokenUrl = "https://frollo-test.au.auth0.com/oauth/token/",
                    serverUrl = "https://api-sandbox.frollo.us/api/v2/",
                    logLevel = LogLevel.DEBUG)

            LoginType.ROPC -> FrolloSDKConfiguration(
                    clientId = "243ffc404803ee5a567d93e1f2dd322a0df911557a5283dd3dd7ebed3258ddeb",
                    redirectUrl = "frollo-sdk-example://authorize",
                    authorizationUrl = "https://id-sandbox.frollo.us/oauth/authorize/",
                    tokenUrl = "https://id-sandbox.frollo.us/oauth/token/",
                    serverUrl = "https://api-sandbox.frollo.us/api/v2/",
                    logLevel = LogLevel.DEBUG)
        }

        setupSdk(configuration)
    }

    private fun setupSdk(configuration: FrolloSDKConfiguration) {
        progress_bar.show()

        if (FrolloSDK.isSetup) {
            completeStartup()
        } else {
            FrolloSDK.setup(application, configuration = configuration) { result ->
                when (result.status) {
                    Result.Status.SUCCESS -> completeStartup()
                    Result.Status.ERROR -> Log.e(TAG, result.error?.localizedDescription)
                }
            }
        }
    }

    private fun completeStartup() {
        progress_bar.hide()

        if (FrolloSDK.authentication.loggedIn) {
            handleNotification()
            FrolloSDK.refreshData()
            startActivity<MainActivity>()
        } else {
            when (loginType) {
                LoginType.AUTHORIZATION_CODE -> startAuthorizationCodeFlow()
                LoginType.ROPC -> startActivity<LoginROPCActivity>()
            }
        }

        finish()
    }

    private fun startAuthorizationCodeFlow() {
        val intent = Intent(this, LoginAuthorizationCodeActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
        FrolloSDK.authentication.loginUserUsingWeb(this, pendingIntent)
    }

    private fun handleNotification() {
        intent.extras?.let {
            FrolloSDK.notifications.handlePushNotification(it)
        }
    }
}
