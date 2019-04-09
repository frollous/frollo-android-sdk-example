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
import android.view.*
import kotlinx.android.synthetic.main.fragment_reports.*
import org.jetbrains.anko.support.v4.startActivity
import us.frollo.frollosdksample.R
import us.frollo.frollosdksample.base.ARGUMENT
import us.frollo.frollosdksample.base.BaseFragment

class ReportsFragment : BaseFragment() {

    companion object {
        private const val TAG = "ReportsFragment"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_reports, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        actionBar?.title = getString(R.string.title_reports)

        initView()
    }

    private fun initView() {
        text_account_balances.setOnClickListener {
            startActivity<ReportsAccountsListActivity>()
        }

        text_current_transaction_reports.setOnClickListener {
            showReportTypes(true)
        }

        text_historic_transaction_reports.setOnClickListener {
            showReportTypes(false)
        }
    }

    private fun showReportTypes(current: Boolean) {
        startActivity<ReportTypesActivity>(ARGUMENT.ARG_DATA_1 to current)
    }
}
