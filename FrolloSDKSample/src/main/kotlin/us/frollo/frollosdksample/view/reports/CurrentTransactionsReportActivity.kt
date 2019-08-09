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

package us.frollo.frollosdksample.view.reports

import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_report_details.*
import org.jetbrains.anko.support.v4.onRefresh
import us.frollo.frollosdk.FrolloSDK
import us.frollo.frollosdk.base.Resource
import us.frollo.frollosdk.base.Result
import us.frollo.frollosdk.model.coredata.reports.ReportGrouping
import us.frollo.frollosdk.model.coredata.reports.ReportTransactionCurrentRelation
import us.frollo.frollosdk.model.display.reports.toDisplay
import us.frollo.frollosdksample.R
import us.frollo.frollosdksample.base.ARGUMENT
import us.frollo.frollosdksample.base.BaseStackActivity
import us.frollo.frollosdksample.extension.getMessage
import us.frollo.frollosdksample.utils.displayError
import us.frollo.frollosdksample.utils.observe
import us.frollo.frollosdksample.view.reports.adapters.CurrentTransactionsReportAdapter

class CurrentTransactionsReportActivity : BaseStackActivity() {

    companion object {
        private const val TAG = "CurrentTxnReport"
    }

    private val reportsAdapter = CurrentTransactionsReportAdapter()
    private var grouping = ReportGrouping.BUDGET_CATEGORY
    private var linkedId: Long = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        grouping = intent.getSerializableExtra(ARGUMENT.ARG_DATA_1) as ReportGrouping
        linkedId = intent.getLongExtra(ARGUMENT.ARG_DATA_2, 1)

        initView()
        initLiveData()
        refresh_layout.onRefresh { refreshReports() }
    }

    private fun initView() {
        recycler_reports.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(this@CurrentTransactionsReportActivity, LinearLayoutManager.VERTICAL))
            adapter = reportsAdapter
        }
    }

    private fun initLiveData() {
        FrolloSDK.reports.currentTransactionReports(grouping).observe(this) {
            when (it?.status) {
                Resource.Status.SUCCESS -> it.data?.let { reports -> loadData(reports) }
                Resource.Status.ERROR -> displayError(it.error?.localizedDescription, "Fetch Reports Failed")
            }
        }
    }

    private fun loadData(data: List<ReportTransactionCurrentRelation>) {
        val models = data.toDisplay().groupReports.filter { it.report?.linkedId == linkedId }.toList()
        reportsAdapter.replaceAll(models)
    }

    private fun refreshReports() {
        FrolloSDK.reports.refreshTransactionCurrentReports(grouping) { result ->
            refresh_layout.isRefreshing = false

            when (result.status) {
                Result.Status.SUCCESS -> Log.d(TAG, "Reports Refreshed")
                Result.Status.ERROR -> displayError(result.error?.getMessage(), "Refreshing Reports Failed")
            }
        }
    }

    override val resourceId: Int
        get() = R.layout.activity_report_details
}
