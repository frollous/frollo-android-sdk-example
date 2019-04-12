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

package us.frollo.frollosdksample.view.bills.adapters

import android.view.View
import kotlinx.android.synthetic.main.template_simple_item3.view.*
import us.frollo.frollosdk.model.coredata.bills.Bill
import us.frollo.frollosdk.model.coredata.bills.BillRelation
import us.frollo.frollosdksample.*
import us.frollo.frollosdksample.base.BaseRecyclerAdapter
import us.frollo.frollosdksample.base.BaseViewHolder
import us.frollo.frollosdksample.display.UserCurrency
import us.frollo.frollosdksample.extension.toDisplay
import us.frollo.frollosdksample.utils.*

class BillsAdapter : BaseRecyclerAdapter<BillRelation, BillsAdapter.BillViewHolder>(BillRelation::class.java, comparator) {

    companion object {
        private val comparator = compareBy<BillRelation> { it.bill?.name }
    }

    override fun getViewHolderLayout(viewType: Int) =
            R.layout.template_simple_item3

    override fun getViewHolder(view: View, viewType: Int) =
            BillViewHolder(view)

    inner class BillViewHolder(itemView: View) : BaseViewHolder<BillRelation>(itemView) {

        override fun bind(model: BillRelation) {
            itemView.text_title.text = model.bill?.name
            itemView.text_subtitle1.text = itemView.context.getString(R.string.str_next_due,
                    model.bill?.nextPaymentDate?.changeDateFormat(from = Bill.DATE_FORMAT_PATTERN, to = "M/d/yy"))
            itemView.text_amount.text = model.bill?.dueAmount?.display(UserCurrency.currency)
            itemView.text_subtitle3.text = model.bill?.frequency?.toDisplay()
            itemView.text_subtitle2.text = model.transactionCategory?.name
        }

        override fun recycle() {
            itemView.text_title.text = null
            itemView.text_subtitle1.text = null
            itemView.text_amount.text = null
            itemView.text_subtitle3.text = null
            itemView.text_subtitle2.text = null
        }
    }
}