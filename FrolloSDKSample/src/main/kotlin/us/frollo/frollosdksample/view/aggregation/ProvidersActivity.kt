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

package us.frollo.frollosdksample.view.aggregation

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_providers.*
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.support.v4.onRefresh
import us.frollo.frollosdk.FrolloSDK
import us.frollo.frollosdk.base.Resource
import us.frollo.frollosdk.base.Result
import us.frollo.frollosdk.model.coredata.aggregation.providers.Provider
import us.frollo.frollosdksample.R
import us.frollo.frollosdksample.base.ARGUMENT
import us.frollo.frollosdksample.base.BaseStackActivity
import us.frollo.frollosdksample.extension.getMessage
import us.frollo.frollosdksample.utils.displayError
import us.frollo.frollosdksample.utils.observe
import us.frollo.frollosdksample.view.aggregation.adapters.ProvidersAdapter

class ProvidersActivity : BaseStackActivity() {

    companion object {
        private const val TAG = "ProvidersActivity"
        private const val REQUEST_ADD_ACCOUNT = 100
    }

    private val providersAdapter = ProvidersAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initView()
        initLiveData()
        refresh_layout.onRefresh { refreshProviders() }
    }

    private fun initView() {
        recycler_providers.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(this@ProvidersActivity, LinearLayoutManager.VERTICAL))
            adapter = providersAdapter.apply {
                onItemClick { model, _, _ ->
                    model?.let { showProviderLoginForm(it) }
                }
            }
        }
    }

    private fun initLiveData() {
        FrolloSDK.aggregation.fetchProviders().observe(this) {
            when (it?.status) {
                Resource.Status.SUCCESS -> it.data?.let { providers -> loadProviders(providers) }
                Resource.Status.ERROR -> displayError(it.error?.localizedDescription, "Fetch Providers Failed")
            }
        }
    }

    private fun loadProviders(providers: List<Provider>) {
        providersAdapter.replaceAll(providers)
    }

    private fun refreshProviders() {
        FrolloSDK.aggregation.refreshProviders { result ->
            refresh_layout.isRefreshing = false

            when (result.status) {
                Result.Status.SUCCESS -> Log.d(TAG, "Providers Refreshed")
                Result.Status.ERROR -> displayError(result.error?.getMessage(), "Refreshing Providers Failed")
            }
        }
    }

    private fun showProviderLoginForm(provider: Provider) {
        startActivityForResult<AddProviderAccountActivity>(REQUEST_ADD_ACCOUNT, ARGUMENT.ARG_DATA_1 to provider.providerId)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_ADD_ACCOUNT && resultCode == Activity.RESULT_OK) {
            finish()
        }
    }

    override val resourceId: Int
        get() = R.layout.activity_providers
}
