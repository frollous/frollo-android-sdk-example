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
import kotlinx.android.synthetic.main.activity_report_grouping.recycler_groups
import kotlinx.android.synthetic.main.activity_report_grouping.refresh_layout
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.onRefresh
import org.threeten.bp.LocalDate
import us.frollo.frollosdk.FrolloSDK
import us.frollo.frollosdk.base.Resource
import us.frollo.frollosdk.base.Result
import us.frollo.frollosdk.model.coredata.reports.ReportDateFormat
import us.frollo.frollosdk.model.coredata.reports.ReportGroupTransactionHistoryRelation
import us.frollo.frollosdk.model.coredata.reports.ReportGrouping
import us.frollo.frollosdk.model.coredata.reports.ReportPeriod
import us.frollo.frollosdk.model.coredata.reports.ReportTransactionHistoryRelation
import us.frollo.frollosdksample.R
import us.frollo.frollosdksample.base.ARGUMENT
import us.frollo.frollosdksample.base.BaseStackActivity
import us.frollo.frollosdksample.display.GroupModel
import us.frollo.frollosdksample.mapping.toGroupModel
import us.frollo.frollosdksample.utils.displayError
import us.frollo.frollosdksample.utils.observe
import us.frollo.frollosdksample.utils.toString
import us.frollo.frollosdksample.view.reports.adapters.ReportGroupsAdapter

class HistoryTransactionsReportGroupingActivity : BaseStackActivity() {

    companion object {
        private const val TAG = "HistoryReportGroup"
    }

    private val groupsAdapter = ReportGroupsAdapter()
    private var grouping = ReportGrouping.BUDGET_CATEGORY
    private lateinit var fromDate: String
    private lateinit var toDate: String
    private val period = ReportPeriod.MONTH

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        grouping = intent.getSerializableExtra(ARGUMENT.ARG_DATA_1) as ReportGrouping

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

    override fun onResume() {
        super.onResume()

        refreshReports()
    }

    private fun initView() {
        recycler_groups.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(this@HistoryTransactionsReportGroupingActivity, LinearLayoutManager.VERTICAL))
            adapter = groupsAdapter.apply {
                onItemClick { model, _, _ ->
                    model?.let { showDetails(it) }
                }
            }
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

    private fun loadData(data: List<ReportTransactionHistoryRelation>) {
        val allGroups = mutableListOf<ReportGroupTransactionHistoryRelation>()
        data.forEach {
            it.groups?.let { groups -> allGroups.addAll(groups) }
        }
        val models = allGroups.mapNotNull { it.toGroupModel() }.toSet()
        groupsAdapter.replaceAll(models.toList())
    }

    private fun showDetails(model: GroupModel) {
        startActivity<HistoryTransactionsReportActivity>(ARGUMENT.ARG_DATA_1 to grouping, ARGUMENT.ARG_DATA_2 to model.id)
    }

    private fun refreshReports() {
        FrolloSDK.reports.refreshTransactionHistoryReports(grouping = grouping, period = period, fromDate = fromDate, toDate = toDate) { result ->
            refresh_layout.isRefreshing = false

            when (result.status) {
                Result.Status.SUCCESS -> Log.d(TAG, "Reports Refreshed")
                Result.Status.ERROR -> displayError(result.error?.localizedDescription, "Refreshing Reports Failed")
            }
        }
    }

    override val resourceId: Int
        get() = R.layout.activity_report_grouping
}
