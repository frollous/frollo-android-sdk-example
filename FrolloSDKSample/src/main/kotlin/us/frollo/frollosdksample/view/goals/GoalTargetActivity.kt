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

package us.frollo.frollosdksample.view.goals

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_goal_target.goal_target_amount
import kotlinx.android.synthetic.main.activity_goal_target.goal_target_date
import kotlinx.android.synthetic.main.activity_goal_target.goal_target_open_ended
import org.jetbrains.anko.startActivity
import us.frollo.frollosdk.model.coredata.goals.GoalTarget
import us.frollo.frollosdksample.R
import us.frollo.frollosdksample.base.ARGUMENT.ARG_DATA_1
import us.frollo.frollosdksample.base.BaseStackActivity

class GoalTargetActivity : BaseStackActivity() {

    companion object {
        private const val TAG = "GoalTarget"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initView()
    }

    private fun initView() {
        goal_target_amount.setOnClickListener {
            startAddGoal(GoalTarget.AMOUNT)
        }

        goal_target_date.setOnClickListener {
            startAddGoal(GoalTarget.DATE)
        }

        goal_target_open_ended.setOnClickListener {
            startAddGoal(GoalTarget.OPEN_ENDED)
        }
    }

    private fun startAddGoal(goalTarget: GoalTarget) {
        startActivity<AddGoalActivity>(ARG_DATA_1 to goalTarget)
        finish()
    }

    override val resourceId: Int
        get() = R.layout.activity_goal_target
}
