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

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_provider_accounts.*
import org.jetbrains.anko.support.v4.onRefresh
import org.jetbrains.anko.support.v4.startActivity
import us.frollo.frollosdk.FrolloSDK
import us.frollo.frollosdk.base.Resource
import us.frollo.frollosdk.base.Result
import us.frollo.frollosdk.model.coredata.aggregation.provideraccounts.ProviderAccountRelation
import us.frollo.frollosdksample.base.ARGUMENT
import us.frollo.frollosdksample.R
import us.frollo.frollosdksample.view.aggregation.adapters.ProviderAccountsAdapter
import us.frollo.frollosdksample.base.BaseFragment
import us.frollo.frollosdksample.extension.getMessage
import us.frollo.frollosdksample.utils.displayError
import us.frollo.frollosdksample.utils.observe
import us.frollo.frollosdksample.utils.showBackNavigation

class ProviderAccountsFragment : BaseFragment() {

    companion object {
        private const val TAG = "ProviderAccounts"
    }

    private val providerAccountsAdapter = ProviderAccountsAdapter()
    private var fetchedLiveData: LiveData<Resource<List<ProviderAccountRelation>>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)

        FrolloSDK.aggregation.refreshProviders()
        FrolloSDK.aggregation.refreshAccounts()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.provider_accounts_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_add -> {
                addAccount()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun addAccount() {
        startActivity<ProvidersActivity>()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_provider_accounts, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        actionBar?.showBackNavigation(show = false)
        actionBar?.title = getString(R.string.title_provider_accounts)

        initView()
        initLiveData()
        refresh_layout?.onRefresh { refreshProviderAccounts() }
    }

    private fun initView() {
        fab_search.setOnClickListener { start(TransactionSearchFragment(), R.id.container, backStack = true) }

        recycler_provider_accounts.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
            adapter = providerAccountsAdapter.apply {
                onItemClick { model, _, _ ->
                    model?.let { showAccounts(it) }
                }
            }
        }
    }

    private fun initLiveData() {
        fetchedLiveData?.removeObservers(this)
        fetchedLiveData = FrolloSDK.aggregation.fetchProviderAccountsWithRelation()
        fetchedLiveData?.observe(this) {
            when (it?.status) {
                Resource.Status.SUCCESS -> it.data?.let { providerAccounts -> loadProviderAccounts(providerAccounts) }
                Resource.Status.ERROR -> displayError(it.error?.localizedDescription, "Fetch Provider Accounts Failed")
            }
        }
    }

    private fun loadProviderAccounts(providerAccounts: List<ProviderAccountRelation>) {
        providerAccountsAdapter.replaceAll(providerAccounts)
    }

    private fun refreshProviderAccounts() {
        FrolloSDK.aggregation.refreshProviderAccounts { result ->
            refresh_layout?.isRefreshing = false

            when (result.status) {
                Result.Status.SUCCESS -> Log.d(TAG, "Provider Accounts Refreshed")
                Result.Status.ERROR -> displayError(result.error?.getMessage(), "Refreshing Provider Accounts Failed")
            }
        }
    }

    private fun showAccounts(model: ProviderAccountRelation) {
        startActivity<AccountsActivity>(ARGUMENT.ARG_DATA_1 to model.providerAccount?.providerAccountId)
    }
}
