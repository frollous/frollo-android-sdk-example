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
import kotlinx.android.synthetic.main.activity_report_types.*
import org.jetbrains.anko.startActivity
import us.frollo.frollosdk.model.coredata.reports.ReportGrouping
import us.frollo.frollosdksample.R
import us.frollo.frollosdksample.base.ARGUMENT
import us.frollo.frollosdksample.base.BaseStackActivity

class ReportTypesActivity : BaseStackActivity() {

    companion object {
        private const val TAG = "ReportTypes"
    }

    private var current = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        current = intent.getBooleanExtra(ARGUMENT.ARG_DATA_1, false)

        initView()
    }

    private fun initView() {
        text_budget_category.setOnClickListener {
            showReportGrouping(ReportGrouping.BUDGET_CATEGORY)
        }

        text_merchant.setOnClickListener {
            showReportGrouping(ReportGrouping.MERCHANT)
        }

        text_transaction_category.setOnClickListener {
            showReportGrouping(ReportGrouping.CATEGORY)
        }
    }

    private fun showReportGrouping(grouping: ReportGrouping) {
        if (current) startActivity<CurrentTransactionsReportGroupingActivity>(ARGUMENT.ARG_DATA_1 to grouping)
        else startActivity<HistoryTransactionsReportGroupingActivity>(ARGUMENT.ARG_DATA_1 to grouping)
    }

    override val resourceId: Int
        get() = R.layout.activity_report_types
}
