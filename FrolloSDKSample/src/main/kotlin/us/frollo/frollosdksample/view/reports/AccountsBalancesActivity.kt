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
import org.threeten.bp.LocalDate
import us.frollo.frollosdk.FrolloSDK
import us.frollo.frollosdk.base.Resource
import us.frollo.frollosdk.base.Result
import us.frollo.frollosdk.model.coredata.reports.ReportAccountBalanceRelation
import us.frollo.frollosdk.model.coredata.reports.ReportDateFormat
import us.frollo.frollosdk.model.coredata.reports.ReportPeriod
import us.frollo.frollosdksample.R
import us.frollo.frollosdksample.base.ARGUMENT
import us.frollo.frollosdksample.base.BaseStackActivity
import us.frollo.frollosdksample.extension.getMessage
import us.frollo.frollosdksample.utils.displayError
import us.frollo.frollosdksample.utils.observe
import us.frollo.frollosdksample.utils.toString
import us.frollo.frollosdksample.view.reports.adapters.AccountsBalancesAdapter

class AccountsBalancesActivity : BaseStackActivity() {

    companion object {
        private const val TAG = "AccountsBalances"
    }

    private val reportsAdapter = AccountsBalancesAdapter()
    private lateinit var fromDate: String
    private lateinit var toDate: String
    private val period = ReportPeriod.DAY
    private var accountId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        accountId = intent.getLongExtra(ARGUMENT.ARG_DATA_1, -1)

        updateDates()
        initView()
        initLiveData()
        refresh_layout.onRefresh { refreshReports() }
    }

    override fun onResume() {
        super.onResume()

        refreshReports()
    }

    private fun updateDates() {
        val now = LocalDate.now()
        toDate = now.toString(ReportDateFormat.DATE_PATTERN_FOR_REQUEST)
        fromDate = now.minusMonths(3).toString(ReportDateFormat.DATE_PATTERN_FOR_REQUEST)
    }

    private fun initView() {
        recycler_reports.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(this@AccountsBalancesActivity, LinearLayoutManager.VERTICAL))
            adapter = reportsAdapter
        }
    }

    private fun initLiveData() {
        FrolloSDK.reports.fetchAccountBalanceReports(fromDate = fromDate, toDate = toDate, period = period, accountId = accountId).observe(this) {
            when (it?.status) {
                Resource.Status.SUCCESS -> it.data?.let { accounts -> loadReports(accounts) }
                Resource.Status.ERROR -> displayError(it.error?.getMessage(), "Fetch Account Balance Reports Failed")
            }
        }
    }

    private fun loadReports(reports: List<ReportAccountBalanceRelation>) {
        reportsAdapter.replaceAll(reports)
    }

    private fun refreshReports() {
        FrolloSDK.reports.refreshAccountBalanceReports(fromDate = fromDate, toDate = toDate, period = period, accountId = accountId) { result ->
            refresh_layout.isRefreshing = false

            when (result.status) {
                Result.Status.SUCCESS -> Log.d(TAG, "Account Balance Reports Refreshed")
                Result.Status.ERROR -> displayError(result.error?.localizedDescription, "Refreshing Reports Failed")
            }
        }
    }

    override val resourceId: Int
        get() = R.layout.activity_report_details
}
