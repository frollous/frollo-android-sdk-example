package us.frollo.frollosdksample.view.authentication

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.startActivity
import us.frollo.frollosdk.FrolloSDK
import us.frollo.frollosdk.base.Result
import us.frollo.frollosdksample.*
import us.frollo.frollosdksample.utils.displayError
import us.frollo.frollosdksample.utils.hide
import us.frollo.frollosdksample.utils.show
import us.frollo.frollosdksample.view.MainActivity

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        btn_login.setOnClickListener { attemptLogin() }
        btn_login_web.setOnClickListener { startAuthorizationCodeFlow() }
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
        val intent = Intent(this, LoginWebActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
        FrolloSDK.authentication.loginUserUsingWeb(this, pendingIntent)
        finish()
    }
}
