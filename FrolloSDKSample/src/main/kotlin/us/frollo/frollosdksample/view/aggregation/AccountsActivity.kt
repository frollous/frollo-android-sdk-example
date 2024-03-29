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
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_accounts.recycler_accounts
import kotlinx.android.synthetic.main.activity_accounts.refresh_layout
import kotlinx.android.synthetic.main.progress_bar_full_screen.progress_bar_layout
import kotlinx.android.synthetic.main.progress_bar_full_screen.text_progress_title
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.onRefresh
import org.jetbrains.anko.toast
import us.frollo.frollosdk.FrolloSDK
import us.frollo.frollosdk.aggregation.fetchAccountsRx
import us.frollo.frollosdk.base.Result
import us.frollo.frollosdk.model.coredata.aggregation.accounts.Account
import us.frollo.frollosdksample.R
import us.frollo.frollosdksample.base.ARGUMENT
import us.frollo.frollosdksample.base.BaseStackActivity
import us.frollo.frollosdksample.extension.getMessage
import us.frollo.frollosdksample.utils.displayError
import us.frollo.frollosdksample.utils.hide
import us.frollo.frollosdksample.utils.show
import us.frollo.frollosdksample.view.aggregation.adapters.AccountsAdapter

class AccountsActivity : BaseStackActivity() {

    companion object {
        private const val TAG = "AccountsActivity"
    }

    private val accountsAdapter = AccountsAdapter()
    private var providerAccountId: Long = -1
    private var menuDelete: MenuItem? = null
    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        providerAccountId = intent.getLongExtra(ARGUMENT.ARG_DATA_1, -1)

        initView()
        subscribeToData()
        refresh_layout.onRefresh { refreshAccounts() }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.accounts_menu, menu)
        menuDelete = menu?.findItem(R.id.menu_delete_account)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_delete_account -> {
                deleteAccount()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initView() {
        recycler_accounts.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(this@AccountsActivity, LinearLayoutManager.VERTICAL))
            adapter = accountsAdapter.apply {
                onItemClick { model, _, _ ->
                    model?.let { showTransactions(it) }
                }
            }
        }
    }

    /**
     * Example fetch of data using methods returning Rx Observable
     */
    private fun subscribeToData() {
        val disposable = FrolloSDK.aggregation.fetchAccountsRx(providerAccountId = providerAccountId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe { accounts ->
                loadAccounts(accounts)
            }
        compositeDisposable.add(disposable)
    }

    private fun loadAccounts(accounts: List<Account>) {
        accountsAdapter.replaceAll(accounts)
    }

    private fun refreshAccounts() {
        FrolloSDK.aggregation.refreshAccounts { result ->
            refresh_layout.isRefreshing = false

            when (result.status) {
                Result.Status.SUCCESS -> Log.d(TAG, "Accounts Refreshed")
                Result.Status.ERROR -> displayError(result.error?.getMessage(), "Refreshing Accounts Failed")
            }
        }
    }

    private fun showTransactions(account: Account) {
        startActivity<TransactionsActivity>(ARGUMENT.ARG_DATA_1 to account.accountId)
    }

    private fun deleteAccount() {
        menuDelete?.isEnabled = false
        text_progress_title.text = getString(R.string.str_deleting_account)
        progress_bar_layout.show()

        FrolloSDK.aggregation.deleteProviderAccount(providerAccountId = providerAccountId) { result ->
            progress_bar_layout.hide()

            when (result.status) {
                Result.Status.SUCCESS -> {
                    toast("Account Deleted!")
                    finish()
                }
                Result.Status.ERROR -> {
                    menuDelete?.isEnabled = true
                    displayError(result.error?.getMessage(), "Deleting Account Failed")
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        compositeDisposable.dispose()
    }

    override val resourceId: Int
        get() = R.layout.activity_accounts
}
