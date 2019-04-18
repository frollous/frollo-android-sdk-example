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
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_transaction_search.*
import org.jetbrains.anko.support.v4.startActivity
import us.frollo.frollosdk.FrolloSDK
import us.frollo.frollosdk.base.Resource
import us.frollo.frollosdk.model.coredata.aggregation.transactions.Transaction
import us.frollo.frollosdksample.base.ARGUMENT
import us.frollo.frollosdksample.R
import us.frollo.frollosdksample.base.BaseFragment
import us.frollo.frollosdksample.utils.*
import us.frollo.frollosdksample.view.aggregation.adapters.TransactionsAdapter

class TransactionSearchFragment : BaseFragment() {

    companion object {
        private const val TAG = "TransactionSearch"
    }

    private var fetchedLiveData: LiveData<Resource<List<Transaction>>>? = null
    private val transactionsAdapter = TransactionsAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_transaction_search, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        actionBar?.showBackNavigation(show = true)
        actionBar?.title = getString(R.string.title_search_transactions)

        initView()
    }

    private fun initView() {
        search_view.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                if (query.isNotBlank())
                    searchTransactions(query)
                search_view.clearFocus()
                return true
            }
            override fun onQueryTextChange(newText: String): Boolean = false
        })

        recycler_transactions.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
            adapter = transactionsAdapter.apply {
                onItemClick { model, _, _ ->
                    model?.let { showDetails(it) }
                }
            }
        }
    }

    private fun searchTransactions(searchTerm: String) {
        progress.show()

        FrolloSDK.aggregation.transactionSearch(searchTerm) { resource ->
            progress.hide()

            when(resource.status) {
                Resource.Status.ERROR -> {
                    displayError(resource.error?.localizedDescription, "Search Transactions Failed")
                }
                Resource.Status.SUCCESS -> {
                    resource.data?.let { fetchTransactions(it) }
                }
            }
        }
    }

    private fun fetchTransactions(transactionIds: LongArray) {
        fetchedLiveData?.removeObservers(this)

        fetchedLiveData = FrolloSDK.aggregation.fetchTransactions(transactionIds)
        fetchedLiveData?.observe(this) {
            when (it?.status) {
                Resource.Status.SUCCESS -> it.data?.let { models -> loadData(models) }
                Resource.Status.ERROR -> displayError(it.error?.localizedDescription, "Fetch Transactions Failed")
            }
        }
    }

    private fun loadData(transactions: List<Transaction>) {
        transactionsAdapter.replaceAll(transactions)
    }

    private fun showDetails(transaction: Transaction) {
        startActivity<TransactionDetailsActivity>(ARGUMENT.ARG_DATA_1 to transaction.transactionId)
    }
}
