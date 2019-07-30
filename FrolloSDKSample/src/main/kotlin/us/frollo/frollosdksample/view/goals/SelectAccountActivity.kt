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

package us.frollo.frollosdksample.view.goals

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_select_account.recycler_accounts
import us.frollo.frollosdk.FrolloSDK
import us.frollo.frollosdk.base.Resource
import us.frollo.frollosdk.model.coredata.aggregation.accounts.Account
import us.frollo.frollosdksample.R
import us.frollo.frollosdksample.base.ARGUMENT
import us.frollo.frollosdksample.base.BaseStackActivity
import us.frollo.frollosdksample.utils.displayError
import us.frollo.frollosdksample.utils.observe
import us.frollo.frollosdksample.view.goals.adapters.SelectAccountAdapter

class SelectAccountActivity : BaseStackActivity() {

    companion object {
        private const val TAG = "SelectAccount"
    }

    private val accountsAdapter = SelectAccountAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initView()
        initLiveData()
    }

    private fun initView() {
        recycler_accounts.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(this@SelectAccountActivity, LinearLayoutManager.VERTICAL))
            adapter = accountsAdapter.apply {
                onItemClick { model, _, _ ->
                    model?.let {
                        val intent = Intent()
                        intent.putExtra(ARGUMENT.ARG_DATA_1, it.accountId)
                        intent.putExtra(ARGUMENT.ARG_DATA_2, it.nickName ?: it.accountName)
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    }
                }
            }
        }
    }

    private fun initLiveData() {
        FrolloSDK.aggregation.fetchAccounts().observe(this) {
            when (it?.status) {
                Resource.Status.SUCCESS -> it.data?.let { accounts -> loadAccounts(accounts) }
                Resource.Status.ERROR -> displayError(it.error?.localizedDescription, "Fetch Accounts Failed")
            }
        }
    }

    private fun loadAccounts(accounts: List<Account>) {
        accountsAdapter.replaceAll(accounts)
    }

    override val resourceId: Int
        get() = R.layout.activity_select_account
}
