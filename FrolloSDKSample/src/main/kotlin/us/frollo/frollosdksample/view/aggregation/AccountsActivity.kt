package us.frollo.frollosdksample.view.aggregation

import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_accounts.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.onRefresh
import us.frollo.frollosdk.FrolloSDK
import us.frollo.frollosdk.base.Resource
import us.frollo.frollosdk.model.coredata.aggregation.accounts.Account
import us.frollo.frollosdksample.base.ARGUMENT
import us.frollo.frollosdksample.R
import us.frollo.frollosdksample.base.BaseStackActivity
import us.frollo.frollosdksample.utils.displayError
import us.frollo.frollosdksample.utils.observe
import us.frollo.frollosdksample.view.aggregation.adapters.AccountsAdapter

class AccountsActivity : BaseStackActivity() {

    companion object {
        private const val TAG = "AccountsActivity"
    }

    private val accountsAdapter = AccountsAdapter()
    private var providerAccountId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_accounts)

        providerAccountId = intent.getLongExtra(ARGUMENT.ARG_GENERIC, -1)

        initView()
        initLiveData()
        refresh_layout.onRefresh { refreshAccounts() }
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

    private fun initLiveData() {
        FrolloSDK.aggregation.fetchAccountsByProviderAccountId(providerAccountId).observe(this) {
            when (it?.status) {
                Resource.Status.SUCCESS -> it.data?.let { accounts -> loadAccounts(accounts) }
                Resource.Status.ERROR -> displayError(it.error?.localizedDescription, "Fetch Accounts Failed")
                Resource.Status.LOADING -> Log.d(TAG, "Loading Accounts...")
            }
        }
    }

    private fun loadAccounts(accounts: List<Account>) {
        accountsAdapter.replaceAll(accounts)
    }

    private fun refreshAccounts() {
        FrolloSDK.aggregation.refreshAccounts { error ->
            refresh_layout.isRefreshing = false
            if (error != null)
                displayError(error.localizedDescription, "Refreshing Accounts Failed")
        }
    }

    private fun showTransactions(account: Account) {
        startActivity<TransactionsActivity>(ARGUMENT.ARG_GENERIC to account.accountId)
    }
}
