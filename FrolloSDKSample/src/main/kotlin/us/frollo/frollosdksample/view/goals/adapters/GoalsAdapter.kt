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
import kotlinx.android.synthetic.main.template_goal_item.view.goal_current_amount
import kotlinx.android.synthetic.main.template_goal_item.view.goal_description
import kotlinx.android.synthetic.main.template_goal_item.view.goal_end_date
import kotlinx.android.synthetic.main.template_goal_item.view.goal_frequency
import kotlinx.android.synthetic.main.template_goal_item.view.goal_name
import kotlinx.android.synthetic.main.template_goal_item.view.goal_target
import kotlinx.android.synthetic.main.template_goal_item.view.goal_target_amount
import us.frollo.frollosdk.model.coredata.goals.Goal
import us.frollo.frollosdksample.R
import us.frollo.frollosdksample.base.BaseRecyclerAdapter
import us.frollo.frollosdksample.base.BaseViewHolder
import us.frollo.frollosdksample.display.UserCurrency
import us.frollo.frollosdksample.extension.toDisplay
import us.frollo.frollosdksample.utils.changeDateFormat
import us.frollo.frollosdksample.utils.display

class GoalsAdapter : BaseRecyclerAdapter<Goal, GoalsAdapter.GoalViewHolder>(Goal::class.java, comparator) {

    companion object {
        private val comparator = compareBy<Goal> { it.endDate }
    }

    override fun getViewHolderLayout(viewType: Int) =
        R.layout.template_goal_item

    override fun getViewHolder(view: View, viewType: Int) =
        GoalViewHolder(view)

    inner class GoalViewHolder(itemView: View) : BaseViewHolder<Goal>(itemView) {

        override fun bind(model: Goal) {
            itemView.goal_name.text = model.name
            itemView.goal_target.text = model.target.name
            itemView.goal_description.text = model.description
            itemView.goal_frequency.text = model.frequency.toDisplay()
            itemView.goal_end_date.text = "Ends ${ model.endDate.changeDateFormat(from = Goal.DATE_FORMAT_PATTERN, to = "M/d/yy") }"
            itemView.goal_target_amount.text = "Target ${ model.targetAmount.display(UserCurrency.currency) }"
            itemView.goal_current_amount.text = "Saved ${ model.currentAmount.display(UserCurrency.currency) }"
        }

        override fun recycle() {
            itemView.goal_name.text = null
            itemView.goal_target.text = null
            itemView.goal_description.text = null
            itemView.goal_frequency.text = null
            itemView.goal_end_date.text = null
            itemView.goal_target_amount.text = null
            itemView.goal_current_amount.text = null
        }
    }
}
