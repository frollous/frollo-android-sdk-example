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

package us.frollo.frollosdksample.view.bills

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_bills.*
import org.jetbrains.anko.support.v4.onRefresh
import org.jetbrains.anko.support.v4.startActivity
import us.frollo.frollosdk.FrolloSDK
import us.frollo.frollosdk.base.Resource
import us.frollo.frollosdk.base.Result
import us.frollo.frollosdk.model.coredata.bills.BillRelation
import us.frollo.frollosdksample.R
import us.frollo.frollosdksample.base.ARGUMENT
import us.frollo.frollosdksample.base.BaseFragment
import us.frollo.frollosdksample.utils.displayError
import us.frollo.frollosdksample.utils.observe
import us.frollo.frollosdksample.utils.showBackNavigation
import us.frollo.frollosdksample.view.bills.adapters.BillsAdapter

class BillsFragment : BaseFragment() {

    companion object {
        private const val TAG = "BillsFragment"
    }

    private val mAdapter = BillsAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_bills, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        actionBar?.showBackNavigation(show = false)
        actionBar?.title = getString(R.string.title_bills)

        initView()
        initLiveData()
        refresh_layout.onRefresh { refreshData() }
    }

    private fun initView() {
        recycler_bills.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
            adapter = mAdapter.apply {
                onItemClick { model, _, _ ->
                    model?.let { showDetails(it) }
                }
            }
        }
    }

    private fun initLiveData() {
        FrolloSDK.bills.fetchBillsWithRelation().observe(this) {
            when (it?.status) {
                Resource.Status.SUCCESS -> it.data?.let { models -> loadData(models) }
                Resource.Status.ERROR -> displayError(it.error?.localizedDescription, "Fetch Bills Failed")
            }
        }
    }

    private fun loadData(models: List<BillRelation>) {
        mAdapter.replaceAll(models)
    }

    private fun refreshData() {
        FrolloSDK.aggregation.refreshProviderAccounts { result ->
            refresh_layout.isRefreshing = false

            when (result.status) {
                Result.Status.SUCCESS -> Log.d(TAG, "Bills Refreshed")
                Result.Status.ERROR -> displayError(result.error?.localizedDescription, "Bills Refresh Failed")
            }
        }
    }

    private fun showDetails(model: BillRelation) {
        startActivity<BillPaymentsActivity>(ARGUMENT.ARG_DATA_1 to model.bill?.billId)
    }
}
