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

package us.frollo.frollosdksample.view.goals.adapters

import android.view.View
import kotlinx.android.synthetic.main.template_goal_period_item.view.period_start_date
import kotlinx.android.synthetic.main.template_goal_period_item.view.period_end_date
import kotlinx.android.synthetic.main.template_goal_period_item.view.period_current_amount
import kotlinx.android.synthetic.main.template_goal_period_item.view.period_target_amount
import kotlinx.android.synthetic.main.template_goal_period_item.view.period_required_amount
import kotlinx.android.synthetic.main.template_goal_period_item.view.period_tracking_status
import org.jetbrains.anko.textColorResource
import us.frollo.frollosdk.model.coredata.goals.GoalPeriod
import us.frollo.frollosdksample.R
import us.frollo.frollosdksample.base.BaseRecyclerAdapter
import us.frollo.frollosdksample.base.BaseViewHolder
import us.frollo.frollosdksample.display.UserCurrency
import us.frollo.frollosdksample.extension.color
import us.frollo.frollosdksample.extension.toDisplay
import us.frollo.frollosdksample.utils.changeDateFormat
import us.frollo.frollosdksample.utils.display

class GoalPeriodsAdapter : BaseRecyclerAdapter<GoalPeriod, GoalPeriodsAdapter.GoalPeriodViewHolder>(GoalPeriod::class.java, comparator) {

    companion object {
        private val comparator = compareBy<GoalPeriod> { it.startDate }
    }

    override fun getViewHolderLayout(viewType: Int) =
            R.layout.template_goal_period_item

    override fun getViewHolder(view: View, viewType: Int) =
            GoalPeriodViewHolder(view)

    inner class GoalPeriodViewHolder(itemView: View) : BaseViewHolder<GoalPeriod>(itemView) {

        override fun bind(model: GoalPeriod) {
            itemView.period_start_date.text = "Start ${ model.startDate.changeDateFormat(from = GoalPeriod.DATE_FORMAT_PATTERN, to = "M/d/yy") }"
            itemView.period_end_date.text = "End ${ model.endDate.changeDateFormat(from = GoalPeriod.DATE_FORMAT_PATTERN, to = "M/d/yy") }"
            itemView.period_current_amount.text = "Current: ${ model.currentAmount?.display(UserCurrency.currency) }"
            itemView.period_target_amount.text = "Target: ${ model.targetAmount?.display(UserCurrency.currency) }"
            itemView.period_required_amount.text = "Required: ${ model.requiredAmount?.display(UserCurrency.currency) }"
            model.trackingStatus?.let {
                itemView.period_tracking_status.text = it.toDisplay()
                itemView.period_tracking_status.textColorResource = it.color
            }
        }

        override fun recycle() {
            itemView.period_start_date.text = null
            itemView.period_end_date.text = null
            itemView.period_current_amount.text = null
            itemView.period_target_amount.text = null
            itemView.period_required_amount.text = null
            itemView.period_tracking_status.text = null
        }
    }
}