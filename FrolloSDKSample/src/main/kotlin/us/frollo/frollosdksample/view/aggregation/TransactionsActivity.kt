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
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_transactions.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.onRefresh
import org.threeten.bp.LocalDate
import us.frollo.frollosdk.FrolloSDK
import us.frollo.frollosdk.base.PaginatedResult
import us.frollo.frollosdk.base.Resource
import us.frollo.frollosdk.model.coredata.aggregation.transactions.Transaction
import us.frollo.frollosdk.model.coredata.aggregation.transactions.TransactionFilter
import us.frollo.frollosdksample.R
import us.frollo.frollosdksample.base.ARGUMENT
import us.frollo.frollosdksample.base.BaseStackActivity
import us.frollo.frollosdksample.extension.getMessage
import us.frollo.frollosdksample.utils.displayError
import us.frollo.frollosdksample.utils.observe
import us.frollo.frollosdksample.utils.toString
import us.frollo.frollosdksample.view.aggregation.adapters.TransactionsAdapter

class TransactionsActivity : BaseStackActivity() {

    companion object {
        private const val TAG = "TransactionsActivity"
    }

    private val transactionsAdapter = TransactionsAdapter()
    private var accountId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        accountId = intent.getLongExtra(ARGUMENT.ARG_DATA_1, -1)

        initView()
        initLiveData()
        refresh_layout.onRefresh { refreshTransactions() }
    }

    override fun onResume() {
        super.onResume()

        refreshTransactions()
    }

    private fun initView() {
        recycler_transactions.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(this@TransactionsActivity, LinearLayoutManager.VERTICAL))
            adapter = transactionsAdapter.apply {
                onItemClick { model, _, _ ->
                    model?.let { showTransactionDetails(it) }
                }
            }
        }
    }

    private fun initLiveData() {
        FrolloSDK.aggregation.fetchTransactions(TransactionFilter(accountIds = listOf(accountId))).observe(this) {
            when (it?.status) {
                Resource.Status.SUCCESS -> it.data?.let { transactions -> loadTransactions(transactions) }
                Resource.Status.ERROR -> displayError(it.error?.localizedDescription, "Fetch Transactions Failed")
            }
        }
    }

    private fun loadTransactions(transactions: List<Transaction>) {
        transactionsAdapter.replaceAll(transactions)
    }

    private fun refreshTransactions() {
        val toDate = LocalDate.now().toString(Transaction.DATE_FORMAT_PATTERN)
        val fromDate = LocalDate.now().minusMonths(3).toString(Transaction.DATE_FORMAT_PATTERN) // 3 months ago

        FrolloSDK.aggregation.refreshTransactionsWithPagination(
            TransactionFilter(fromDate = fromDate, toDate = toDate, accountIds = listOf(accountId))
        ) { result ->
            refresh_layout.isRefreshing = false

            when (result) {
                is PaginatedResult.Success -> Log.d(TAG, "Transactions Refreshed")
                is PaginatedResult.Error -> displayError(result.error?.getMessage(), "Refreshing Transactions Failed")
            }
        }
    }

    private fun showTransactionDetails(transaction: Transaction) {
        startActivity<TransactionDetailsActivity>(ARGUMENT.ARG_DATA_1 to transaction.transactionId)
    }

    override val resourceId: Int
        get() = R.layout.activity_transactions
}
