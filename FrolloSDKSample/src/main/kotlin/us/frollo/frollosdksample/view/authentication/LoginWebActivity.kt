package us.frollo.frollosdksample.view.authentication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.jetbrains.anko.startActivity
import us.frollo.frollosdk.FrolloSDK
import us.frollo.frollosdk.base.Result
import us.frollo.frollosdksample.*
import us.frollo.frollosdksample.utils.displayError
import us.frollo.frollosdksample.view.MainActivity

class LoginWebActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login_web)

        FrolloSDK.authentication.handleWebLoginResponse(intent) { result ->
            when (result.status) {
                Result.Status.SUCCESS -> {
                    FrolloSDK.refreshData()
                    startActivity<MainActivity>()
                    finish()
                }

                Result.Status.ERROR -> {
                    displayError(result.error?.localizedDescription, "Login Failed") {
                        finish()
                    }
                }
            }
        }
    }
}
