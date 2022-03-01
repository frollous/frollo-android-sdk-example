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
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.startActivity
import us.frollo.frollosdk.FrolloSDK
import us.frollo.frollosdk.base.Resource
import us.frollo.frollosdksample.R
import us.frollo.frollosdksample.base.BaseStackActivity
import us.frollo.frollosdksample.display.UserCurrency
import us.frollo.frollosdksample.utils.displayError
import us.frollo.frollosdksample.utils.observe
import us.frollo.frollosdksample.view.aggregation.ProviderAccountsFragment
import us.frollo.frollosdksample.view.bills.BillsFragment
import us.frollo.frollosdksample.view.messages.MessagesFragment
import us.frollo.frollosdksample.view.others.OthersFragment
import us.frollo.frollosdksample.view.profile.ProfileActivity
import us.frollo.frollosdksample.view.reports.ReportsFragment

class MainActivity : BaseStackActivity() {

    companion object {
        private const val TAG = "MainActivity"
        private const val DEFAULT_PAGE = R.id.nav_accounts
    }

    private val messagesFragment = MessagesFragment()
    private val accountsFragment = ProviderAccountsFragment()
    private val billsFragment = BillsFragment()
    private val reportsFragment = ReportsFragment()
    private val othersFragment = OthersFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        registerPushNotification()

        navigation.setOnNavigationItemSelectedListener {
            bottomNavSelected(it.itemId)
            true
        }

        bottomNavSelected(DEFAULT_PAGE)

        fetchUserCurrency()
    }

    private fun registerPushNotification() {
        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { token ->
                token?.let { FrolloSDK.notifications.registerPushNotificationToken(it) }
            }
            .addOnFailureListener {
                it.printStackTrace()
            }
            .addOnCompleteListener(
                OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Log.e(TAG, "getToken failed ${task.exception}")
                        return@OnCompleteListener
                    }
                }
            )
    }

    private fun bottomNavSelected(itemId: Int) {
        loadFragment(itemId)
    }

    private fun loadFragment(itemId: Int) {
        val fragment = when (itemId) {
            R.id.nav_messages -> messagesFragment
            R.id.nav_accounts -> accountsFragment
            R.id.nav_bills -> billsFragment
            R.id.nav_reports -> reportsFragment
            R.id.nav_others -> othersFragment
            else -> null
        }

        fragment?.let {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, it)
                .commitAllowingStateLoss()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_profile -> {
                startActivity<ProfileActivity>()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun fetchUserCurrency() {
        FrolloSDK.userManagement.fetchUser().observe(this) { resource ->
            when (resource?.status) {
                Resource.Status.SUCCESS -> resource.data?.let { UserCurrency.currency = it.primaryCurrency }
                Resource.Status.ERROR -> displayError(resource.error?.localizedDescription, "Fetch User Failed")
            }
        }
    }

    override val resourceId: Int
        get() = R.layout.activity_main
}
