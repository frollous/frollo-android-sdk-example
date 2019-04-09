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

package us.frollo.frollosdksample.view.reports.adapters

import android.view.View
import kotlinx.android.synthetic.main.template_detail_item1.view.*
import us.frollo.frollosdk.model.coredata.reports.ReportGroupTransactionHistoryRelation
import us.frollo.frollosdksample.R
import us.frollo.frollosdksample.base.BaseRecyclerAdapter
import us.frollo.frollosdksample.base.BaseViewHolder
import us.frollo.frollosdksample.display.UserCurrency
import us.frollo.frollosdksample.utils.changeDateFormat
import us.frollo.frollosdksample.utils.display

class HistoryTransactionsReportAdapter : BaseRecyclerAdapter<ReportGroupTransactionHistoryRelation, HistoryTransactionsReportAdapter.ReportsViewHolder>(ReportGroupTransactionHistoryRelation::class.java, reportsComparator) {

    companion object {
        private val reportsComparator = compareByDescending<ReportGroupTransactionHistoryRelation> { it.groupReport?.date }
    }

    override fun getViewHolderLayout(viewType: Int) =
            R.layout.template_detail_item1

    override fun getViewHolder(view: View, viewType: Int) =
            ReportsViewHolder(view)

    inner class ReportsViewHolder(itemView: View) : BaseViewHolder<ReportGroupTransactionHistoryRelation>(itemView) {

        override fun bind(model: ReportGroupTransactionHistoryRelation) {
            model.groupReport?.let {
                itemView.text_title.text = it.date.changeDateFormat(from = it.period.dateFormatPattern, to = "MMM d, yyyy")
                        .replace(".", "")
                itemView.text_detail.text = it.value.display(UserCurrency.currency)
            }
        }

        override fun recycle() {
            itemView.text_title.text = null
            itemView.text_detail.text = null
        }
    }
}