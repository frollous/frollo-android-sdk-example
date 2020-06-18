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

package us.frollo.frollosdksample.view.budgets.adapters

import android.text.Html
import android.view.View
import kotlinx.android.synthetic.main.template_budget_item.view.currentAmount
import kotlinx.android.synthetic.main.template_budget_item.view.frequency
import kotlinx.android.synthetic.main.template_budget_item.view.periodAmount
import kotlinx.android.synthetic.main.template_budget_item.view.trackingStatus
import kotlinx.android.synthetic.main.template_budget_item.view.type
import kotlinx.android.synthetic.main.template_budget_item.view.typeValue
import us.frollo.frollosdk.model.coredata.budgets.Budget
import us.frollo.frollosdksample.R
import us.frollo.frollosdksample.base.BaseRecyclerAdapter
import us.frollo.frollosdksample.base.BaseViewHolder
import us.frollo.frollosdksample.display.UserCurrency
import us.frollo.frollosdksample.utils.display

class BudgetsAdapter : BaseRecyclerAdapter<Budget, BudgetsAdapter.BudgetViewHolder>(Budget::class.java, comparator) {

    companion object {
        private val comparator = compareBy<Budget> { it.startDate }
    }

    override fun getViewHolderLayout(viewType: Int) =
        R.layout.template_budget_item

    override fun getViewHolder(view: View, viewType: Int) =
        BudgetViewHolder(view)

    inner class BudgetViewHolder(itemView: View) : BaseViewHolder<Budget>(itemView) {

        override fun bind(model: Budget) {
            itemView.frequency.text = model.frequency.name
            itemView.trackingStatus.text = model.status.name
            itemView.currentAmount.text = Html.fromHtml("<b>Spent:</b> ${model.currentAmount.display(UserCurrency.currency)}")
            itemView.periodAmount.text = Html.fromHtml("Budget: ${model.periodAmount.display(UserCurrency.currency)}")
            itemView.type.text = Html.fromHtml("<b>Type</b> ${model.type.name}")
            itemView.typeValue.text = Html.fromHtml("<b>Value</b> ${model.typeValue.toUpperCase()}")
        }

        override fun recycle() {
            itemView.frequency.text = null
            itemView.trackingStatus.text = null
            itemView.currentAmount.text = null
            itemView.periodAmount.text = null
            itemView.type.text = null
            itemView.typeValue.text = null
        }
    }
}
