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

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.progress_bar_full_screen.progress_bar
import kotlinx.android.synthetic.main.progress_bar_full_screen.text_progress_title
import org.jetbrains.anko.startActivity
import us.frollo.frollosdk.FrolloSDK
import us.frollo.frollosdk.base.Result
import us.frollo.frollosdksample.R
import us.frollo.frollosdksample.utils.displayError
import us.frollo.frollosdksample.utils.show
import us.frollo.frollosdksample.view.MainActivity

class LoginWebActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login_web)

        text_progress_title.text = getString(R.string.str_logging_in)
        progress_bar.show()

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
