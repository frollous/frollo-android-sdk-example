package us.frollo.frollosdksample.view.aggregation

import android.os.Bundle
import android.util.Log
import android.view.*
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
import us.frollo.frollosdksample.utils.displayError
import us.frollo.frollosdksample.utils.observe

class ProviderAccountsFragment : BaseFragment() {

    companion object {
        private const val TAG = "ProviderAccounts"
    }

    private val providerAccountsAdapter = ProviderAccountsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
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

        actionBar?.title = getString(R.string.title_provider_accounts)

        initView()
        initLiveData()
        refresh_layout.onRefresh { refreshProviderAccounts() }
    }

    private fun initView() {
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
        FrolloSDK.aggregation.fetchProviderAccountsWithRelation().observe(this) {
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
            refresh_layout.isRefreshing = false

            when (result.status) {
                Result.Status.SUCCESS -> Log.d(TAG, "Provider Accounts Refreshed")
                Result.Status.ERROR -> displayError(result.error?.localizedDescription, "Refreshing Provider Accounts Failed")
            }
        }
    }

    private fun showAccounts(model: ProviderAccountRelation) {
        startActivity<AccountsActivity>(ARGUMENT.ARG_DATA_1 to model.providerAccount?.providerAccountId)
    }
}
