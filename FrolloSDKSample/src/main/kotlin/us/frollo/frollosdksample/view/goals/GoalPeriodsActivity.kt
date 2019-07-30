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
import android.util.Log
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_goal_periods.frequency
import kotlinx.android.synthetic.main.activity_goal_periods.goal_name
import kotlinx.android.synthetic.main.activity_goal_periods.period_amount
import kotlinx.android.synthetic.main.activity_goal_periods.recycler_goal_periods
import kotlinx.android.synthetic.main.activity_goal_periods.refresh_layout
import org.jetbrains.anko.support.v4.onRefresh
import us.frollo.frollosdk.FrolloSDK
import us.frollo.frollosdk.base.Resource
import us.frollo.frollosdk.base.Result
import us.frollo.frollosdk.model.coredata.goals.GoalPeriod
import us.frollo.frollosdksample.base.ARGUMENT
import us.frollo.frollosdksample.R
import us.frollo.frollosdksample.base.BaseStackActivity
import us.frollo.frollosdksample.extension.toDisplay
import us.frollo.frollosdksample.utils.display
import us.frollo.frollosdksample.utils.displayError
import us.frollo.frollosdksample.utils.observe
import us.frollo.frollosdksample.view.goals.adapters.GoalPeriodsAdapter

class GoalPeriodsActivity : BaseStackActivity() {

    companion object {
        private const val TAG = "GoalPeriods"
    }

    private val periodsAdapter = GoalPeriodsAdapter()
    private var goalId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        goalId = intent.getLongExtra(ARGUMENT.ARG_DATA_1, -1)

        initView()
        initLiveData()
        refresh_layout.onRefresh { refreshGoalPeriods() }
    }

    override fun onResume() {
        super.onResume()

        refreshGoalPeriods()
    }

    private fun initView() {
        recycler_goal_periods.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(this@GoalPeriodsActivity, LinearLayoutManager.VERTICAL))
            adapter = periodsAdapter
        }
    }

    private fun initLiveData() {
        FrolloSDK.goals.fetchGoal(goalId = goalId).observe(this) {
            when (it?.status) {
                Resource.Status.SUCCESS -> it.data?.let { goal ->
                    goal_name.text = goal.name
                    frequency.text = goal.frequency.toDisplay()
                    period_amount.text = goal.periodAmount.display(goal.currency)
                }
                Resource.Status.ERROR -> displayError(it.error?.localizedDescription, "Fetch Goal Failed")
            }
        }

        FrolloSDK.goals.fetchGoalPeriods(goalId = goalId).observe(this) {
            when (it?.status) {
                Resource.Status.SUCCESS -> it.data?.let { models -> loadData(models) }
                Resource.Status.ERROR -> displayError(it.error?.localizedDescription, "Fetch Goal Periods Failed")
            }
        }
    }

    private fun loadData(models: List<GoalPeriod>) {
        periodsAdapter.replaceAll(models)
    }

    private fun refreshGoalPeriods() {
        FrolloSDK.goals.refreshGoalPeriods(goalId) { result ->
            refresh_layout.isRefreshing = false

            when (result.status) {
                Result.Status.SUCCESS -> Log.d(TAG, "Goal Periods Refreshed")
                Result.Status.ERROR -> displayError(result.error?.localizedDescription, "Refreshing Goal Periods Failed")
            }
        }
    }

    override val resourceId: Int
        get() = R.layout.activity_goal_periods
}
