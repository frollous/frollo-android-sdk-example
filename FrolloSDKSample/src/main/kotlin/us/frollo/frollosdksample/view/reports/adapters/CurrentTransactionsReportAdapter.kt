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
import us.frollo.frollosdk.model.coredata.reports.ReportTransactionCurrentRelation
import us.frollo.frollosdksample.R
import us.frollo.frollosdksample.base.BaseRecyclerAdapter
import us.frollo.frollosdksample.base.BaseViewHolder
import us.frollo.frollosdksample.display.UserCurrency
import us.frollo.frollosdksample.utils.display
import java.math.BigDecimal

class CurrentTransactionsReportAdapter : BaseRecyclerAdapter<ReportTransactionCurrentRelation, CurrentTransactionsReportAdapter.ReportsViewHolder>(ReportTransactionCurrentRelation::class.java, reportsComparator) {

    companion object {
        private val reportsComparator = compareBy<ReportTransactionCurrentRelation> { it.report?.day }
    }

    override fun getViewHolderLayout(viewType: Int) =
            R.layout.template_detail_item1

    override fun getViewHolder(view: View, viewType: Int) =
            ReportsViewHolder(view)

    inner class ReportsViewHolder(itemView: View) : BaseViewHolder<ReportTransactionCurrentRelation>(itemView) {

        override fun bind(model: ReportTransactionCurrentRelation) {
            model.report?.let {
                itemView.text_title.text = String.format("Day %s", it.day)
                itemView.text_detail.text = (it.amount ?: BigDecimal("0.00")).display(UserCurrency.currency)
            }
        }

        override fun recycle() {
            itemView.text_title.text = null
            itemView.text_detail.text = null
        }
    }
}