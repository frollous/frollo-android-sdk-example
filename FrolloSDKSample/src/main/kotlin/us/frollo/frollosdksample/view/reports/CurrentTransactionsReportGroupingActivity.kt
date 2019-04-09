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
import kotlinx.android.synthetic.main.activity_report_grouping.*
import org.jetbrains.anko.startActivity
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
import us.frollo.frollosdksample.display.GroupModel
import us.frollo.frollosdksample.mapping.toGroupModel
import us.frollo.frollosdksample.utils.displayError
import us.frollo.frollosdksample.utils.observe
import us.frollo.frollosdksample.view.reports.adapters.ReportGroupsAdapter

class CurrentTransactionsReportGroupingActivity : BaseStackActivity() {

    companion object {
        private const val TAG = "CurrentReportGroup"
    }

    private val groupsAdapter = ReportGroupsAdapter()
    private var grouping = ReportGrouping.BUDGET_CATEGORY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_report_grouping)

        grouping = intent.getSerializableExtra(ARGUMENT.ARG_DATA_1) as ReportGrouping

        initView()
        initLiveData()
        refresh_layout.onRefresh { refreshReports() }
    }

    override fun onResume() {
        super.onResume()

        refreshReports()
    }

    private fun initView() {
        recycler_groups.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(this@CurrentTransactionsReportGroupingActivity, LinearLayoutManager.VERTICAL))
            adapter = groupsAdapter.apply {
                onItemClick { model, _, _ ->
                    model?.let { showDetails(it) }
                }
            }
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
        val models = data.toDisplay().groupReports.mapNotNull { it.toGroupModel() }.toSet()
        groupsAdapter.replaceAll(models.toList())
    }

    private fun showDetails(model: GroupModel) {
        startActivity<CurrentTransactionsReportActivity>(ARGUMENT.ARG_DATA_1 to grouping, ARGUMENT.ARG_DATA_2 to model.id)
    }

    private fun refreshReports() {
        FrolloSDK.reports.refreshTransactionCurrentReports(grouping) { result ->
            refresh_layout.isRefreshing = false

            when (result.status) {
                Result.Status.SUCCESS -> Log.d(TAG, "Reports Refreshed")
                Result.Status.ERROR -> displayError(result.error?.localizedDescription, "Refreshing Reports Failed")
            }
        }
    }
}
