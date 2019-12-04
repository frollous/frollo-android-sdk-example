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

import kotlinx.android.synthetic.main.template_detail_item1.view.*

// TODO: Refactor to use new reports API methods
/*class HistoryTransactionsReportAdapter : BaseRecyclerAdapter<ReportGroupTransactionRelation, HistoryTransactionsReportAdapter.ReportsViewHolder>(ReportGroupTransactionRelation::class.java, reportsComparator) {

    companion object {
        private val reportsComparator = compareByDescending<ReportGroupTransactionRelation> { it.groupReport?.date }
    }

    override fun getViewHolderLayout(viewType: Int) =
            R.layout.template_detail_item1

    override fun getViewHolder(view: View, viewType: Int) =
            ReportsViewHolder(view)

    inner class ReportsViewHolder(itemView: View) : BaseViewHolder<ReportGroupTransactionRelation>(itemView) {

        override fun bind(model: ReportGroupTransactionRelation) {
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
}*/