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
import kotlinx.android.synthetic.main.template_simple_item4.view.*
import us.frollo.frollosdk.model.coredata.reports.GroupReport
import us.frollo.frollosdksample.R
import us.frollo.frollosdksample.base.BaseRecyclerAdapter
import us.frollo.frollosdksample.base.BaseViewHolder
import us.frollo.frollosdksample.display.UserCurrency
import us.frollo.frollosdksample.utils.changeDateFormat
import us.frollo.frollosdksample.utils.display
import us.frollo.frollosdksample.utils.show

class TransactionsReportAdapter : BaseRecyclerAdapter<GroupReport, TransactionsReportAdapter.ReportsViewHolder>(GroupReport::class.java, reportsComparator) {

    companion object {
        private val reportsComparator = compareByDescending<GroupReport> { it.date }
    }

    override fun getViewHolderLayout(viewType: Int) =
            R.layout.template_simple_item4

    override fun getViewHolder(view: View, viewType: Int) =
            ReportsViewHolder(view)

    inner class ReportsViewHolder(itemView: View) : BaseViewHolder<GroupReport>(itemView) {

        override fun bind(model: GroupReport) {
            itemView.text_title.text = model.date.changeDateFormat(from = GroupReport.DATE_FORMAT_PATTERN, to = "MMM d, yyyy").replace(".", "")
            itemView.text_subtitle.show()
            itemView.text_subtitle.text = model.name
            val amount = if (model.isIncome) model.value else -model.value
            itemView.text_amount.text = amount.display(UserCurrency.currency)
        }

        override fun recycle() {
            itemView.text_title.text = null
            itemView.text_subtitle.text = null
            itemView.text_amount.text = null
        }
    }
}