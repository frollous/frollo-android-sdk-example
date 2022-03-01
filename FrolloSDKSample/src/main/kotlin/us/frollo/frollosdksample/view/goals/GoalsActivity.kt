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
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_goals.recycler_goals
import kotlinx.android.synthetic.main.activity_goals.refresh_layout
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.onRefresh
import us.frollo.frollosdk.FrolloSDK
import us.frollo.frollosdk.base.Resource
import us.frollo.frollosdk.model.coredata.goals.Goal
import us.frollo.frollosdksample.R
import us.frollo.frollosdksample.base.ARGUMENT
import us.frollo.frollosdksample.base.BaseStackActivity
import us.frollo.frollosdksample.extension.getMessage
import us.frollo.frollosdksample.utils.displayError
import us.frollo.frollosdksample.utils.observe
import us.frollo.frollosdksample.view.goals.adapters.GoalsAdapter

class GoalsActivity : BaseStackActivity() {

    override val resourceId: Int
        get() = R.layout.activity_goals

    companion object {
        private const val TAG = "GoalsActivity"
    }

    private val mAdapter = GoalsAdapter()
    private var fetchedLiveData: LiveData<Resource<List<Goal>>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        actionBar?.title = getString(R.string.title_goals)
        refreshData()
        initView()
        initLiveData()
        refresh_layout?.onRefresh { refreshData() }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.goals_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_add -> {
                startActivity<GoalTargetActivity>()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initView() {
        recycler_goals.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(this@GoalsActivity, LinearLayoutManager.VERTICAL))
            adapter = mAdapter.apply {
                onItemClick { model, _, _ ->
                    model?.let { showDetails(it) }
                }
            }
        }
    }

    private fun initLiveData() {
        fetchedLiveData?.removeObservers(this)
        fetchedLiveData = FrolloSDK.goals.fetchGoals()
        fetchedLiveData?.observe(this) {
            when (it?.status) {
                Resource.Status.SUCCESS -> it.data?.let { models -> loadData(models) }
                Resource.Status.ERROR -> displayError(it.error?.localizedDescription, "Fetch Goals Failed")
            }
        }
    }

    private fun loadData(models: List<Goal>) {
        mAdapter.replaceAll(models)
    }

    private fun refreshData() {
        FrolloSDK.goals.refreshGoals { resource ->
            refresh_layout?.isRefreshing = false

            when (resource.status) {
                Resource.Status.SUCCESS -> Log.d(TAG, "Goals Refreshed")
                Resource.Status.ERROR -> displayError(resource.error?.getMessage(), "Goals Refresh Failed")
            }
        }
    }

    private fun showDetails(model: Goal) {
        startActivity<GoalPeriodsActivity>(ARGUMENT.ARG_DATA_1 to model.goalId)
    }
}
