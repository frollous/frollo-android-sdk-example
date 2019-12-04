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

import us.frollo.frollosdksample.R
import us.frollo.frollosdksample.base.BaseStackActivity

// TODO: Refactor to use new reports API methods
class HistoryTransactionsReportActivity : BaseStackActivity() {

    /*companion object {
        private const val TAG = "HistoryTxnReport"
    }

    private val reportsAdapter = HistoryTransactionsReportAdapter()
    private var grouping = ReportGrouping.BUDGET_CATEGORY
    private var linkedId: Long = 1
    private lateinit var fromDate: String
    private lateinit var toDate: String
    private val period = ReportPeriod.MONTH

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        grouping = intent.getSerializableExtra(ARGUMENT.ARG_DATA_1) as ReportGrouping
        linkedId = intent.getLongExtra(ARGUMENT.ARG_DATA_2, 1)

        updateDates()
        initView()
        initLiveData()
        refresh_layout.onRefresh { refreshReports() }
    }

    private fun updateDates() {
        val now = LocalDate.now()
        toDate = now.toString(ReportDateFormat.DATE_PATTERN_FOR_REQUEST)
        fromDate = now.minusMonths(12).toString(ReportDateFormat.DATE_PATTERN_FOR_REQUEST)
    }

    private fun initView() {
        recycler_reports.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(this@HistoryTransactionsReportActivity, LinearLayoutManager.VERTICAL))
            adapter = reportsAdapter
        }
    }

    private fun initLiveData() {
        FrolloSDK.reports.historyTransactionReports(grouping = grouping, period = period, fromDate = fromDate, toDate = toDate).observe(this) {
            when (it?.status) {
                Resource.Status.SUCCESS -> it.data?.let { reports -> loadData(reports) }
                Resource.Status.ERROR -> displayError(it.error?.localizedDescription, "Fetch Reports Failed")
            }
        }
    }

    private fun loadData(data: List<ReportTransactionRelation>) {
        val allGroups = mutableListOf<ReportGroupTransactionRelation>()
        data.forEach {
            it.groups?.let { groups -> allGroups.addAll(groups) }
        }
        val models = allGroups.filter { it.groupReport?.linkedId == linkedId }.toList()
        reportsAdapter.replaceAll(models)
    }

    private fun refreshReports() {
        FrolloSDK.reports.refreshTransactionHistoryReports(grouping = grouping, period = period, fromDate = fromDate, toDate = toDate) { result ->
            refresh_layout.isRefreshing = false

            when (result.status) {
                Result.Status.SUCCESS -> Log.d(TAG, "Reports Refreshed")
                Result.Status.ERROR -> displayError(result.error?.getMessage(), "Refreshing Reports Failed")
            }
        }
    }*/

    override val resourceId: Int
        get() = R.layout.activity_report_details
}
