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

package us.frollo.frollosdksample.view.profile

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_profile.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.startActivity
import us.frollo.frollosdk.FrolloSDK
import us.frollo.frollosdk.base.Resource
import us.frollo.frollosdksample.R
import us.frollo.frollosdksample.base.BaseStackActivity
import us.frollo.frollosdksample.utils.displayError
import us.frollo.frollosdksample.utils.observe
import us.frollo.frollosdksample.utils.showThemed
import us.frollo.frollosdksample.view.authentication.LoginActivity

class ProfileActivity : BaseStackActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        btn_logout.setOnClickListener { logout() }
        initLiveData()
    }

    private fun initLiveData() {
        FrolloSDK.userManagement.fetchUser().observe(this) {
            when (it?.status) {
                Resource.Status.SUCCESS -> it.data?.let { user ->
                    name.text = "${ user.firstName } ${ user.lastName }"
                    email.text = user.email
                }
                Resource.Status.ERROR -> displayError(it.error?.localizedDescription, "Fetch User Failed")
            }
        }
    }

    private fun logout() {
        alert("Are you sure you want to logout?", "Logout") {
            positiveButton("Yes") {
                if (app.setupManager?.useV1Auth == true) {
                    FrolloSDK.reset()
                    app.setupManager?.customAuthentication?.logout()
                } else {
                    FrolloSDK.oAuth2Authentication?.logout()
                }
                startActivity<LoginActivity>()
                finishAffinity()
            }
            negativeButton("No") {}
        }.showThemed()
    }

    override val resourceId: Int
        get() = R.layout.activity_profile
}
