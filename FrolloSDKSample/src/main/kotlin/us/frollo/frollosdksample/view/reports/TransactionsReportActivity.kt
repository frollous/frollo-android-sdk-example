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
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_report_details.recycler_reports
import kotlinx.android.synthetic.main.activity_report_details.refresh_layout
import org.jetbrains.anko.support.v4.onRefresh
import org.threeten.bp.LocalDate
import us.frollo.frollosdk.FrolloSDK
import us.frollo.frollosdk.base.Resource
import us.frollo.frollosdk.model.coredata.reports.GroupReport
import us.frollo.frollosdk.model.coredata.reports.Report
import us.frollo.frollosdk.model.coredata.reports.ReportDateFormat
import us.frollo.frollosdk.model.coredata.reports.ReportGrouping
import us.frollo.frollosdk.model.coredata.reports.TransactionReportPeriod
import us.frollo.frollosdk.model.coredata.shared.BudgetCategory
import us.frollo.frollosdksample.R
import us.frollo.frollosdksample.base.BaseStackActivity
import us.frollo.frollosdksample.extension.getMessage
import us.frollo.frollosdksample.utils.displayError
import us.frollo.frollosdksample.utils.toString
import us.frollo.frollosdksample.view.reports.adapters.TransactionsReportAdapter

class TransactionsReportActivity : BaseStackActivity() {

    companion object {
        private const val TAG = "HistoryTxnReport"
    }

    private val reportsAdapter = TransactionsReportAdapter()
    private lateinit var reportType: ReportType
    private lateinit var reportPeriod: TransactionReportPeriod
    private var filterId: Long? = null
    private var filterTag: String? = null
    private var grouping: ReportGrouping? = null
    private lateinit var fromDate: String
    private lateinit var toDate: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        with(intent) {
            reportType = getSerializableExtra(ReportConstants.ARG_REPORT_TYPE) as ReportType
            reportPeriod = getSerializableExtra(ReportConstants.ARG_REPORT_PERIOD) as TransactionReportPeriod
            val id = getLongExtra(ReportConstants.ARG_FILTER_ID, -1)
            if (id > -1L) {
                filterId = id
            }
            filterTag = getStringExtra(ReportConstants.ARG_FILTER_TAG)
            grouping = getSerializableExtra(ReportConstants.ARG_REPORT_GROUPING) as? ReportGrouping
        }

        updateDates()
        initView()
        fetchReports()
        refresh_layout.onRefresh { fetchReports() }
    }

    private fun updateDates() {
        val now = LocalDate.now()
        toDate = now.toString(ReportDateFormat.DATE_PATTERN_FOR_REQUEST)
        fromDate = now.minusMonths(12).toString(ReportDateFormat.DATE_PATTERN_FOR_REQUEST)
    }

    private fun initView() {
        recycler_reports.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(this@TransactionsReportActivity, LinearLayoutManager.VERTICAL))
            adapter = reportsAdapter
        }
    }

    private fun fetchReports() {
        when (reportType) {
            ReportType.TRANSACTION_CATEGORY -> fetchTransactionCategoryReports()
            ReportType.MERCHANT -> fetchMerchantReports()
            ReportType.BUDGET_CATEGORY -> fetchBudgetCategoryReports()
            ReportType.TAG -> fetchTagReports()
        }
    }

    private fun fetchTransactionCategoryReports() {
        FrolloSDK.reports.fetchTransactionCategoryReports(
                fromDate = fromDate,
                toDate = toDate,
                period = reportPeriod,
                categoryId = filterId,
                grouping = grouping) { postFetch(it) }
    }

    private fun fetchMerchantReports() {
        FrolloSDK.reports.fetchMerchantReports(
                fromDate = fromDate,
                toDate = toDate,
                period = reportPeriod,
                merchantId = filterId,
                grouping = grouping) { postFetch(it) }
    }

    private fun fetchBudgetCategoryReports() {
        FrolloSDK.reports.fetchBudgetCategoryReports(
                fromDate = fromDate,
                toDate = toDate,
                period = reportPeriod,
                budgetCategory = filterId?.let { BudgetCategory.getById(it) },
                grouping = grouping) { postFetch(it) }
    }

    private fun fetchTagReports() {
        FrolloSDK.reports.fetchTagReports(
                fromDate = fromDate,
                toDate = toDate,
                period = reportPeriod,
                transactionTag = filterTag,
                grouping = grouping) { postFetch(it) }
    }

    private fun postFetch(resource: Resource<List<Report>>) {
        refresh_layout.isRefreshing = false

        when (resource.status) {
            Resource.Status.SUCCESS -> resource.data?.let { loadData(it) }
            Resource.Status.ERROR -> displayError(resource.error?.getMessage(), "Reports Fetch Failed")
        }
    }

    private fun loadData(data: List<Report>) {
        val allGroups = mutableListOf<GroupReport>()
        data.forEach {
            allGroups.addAll(it.groups)
        }
        reportsAdapter.replaceAll(allGroups)
    }

    override val resourceId: Int
        get() = R.layout.activity_report_details
}
