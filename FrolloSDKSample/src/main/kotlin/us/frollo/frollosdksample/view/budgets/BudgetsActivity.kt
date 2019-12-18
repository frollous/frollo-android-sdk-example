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

package us.frollo.frollosdksample.view.budgets

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_budgets.recylerView
import kotlinx.android.synthetic.main.activity_goals.refresh_layout
import org.jetbrains.anko.selector
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.onRefresh
import us.frollo.frollosdk.FrolloSDK
import us.frollo.frollosdk.base.Resource
import us.frollo.frollosdk.base.Result
import us.frollo.frollosdk.model.coredata.budgets.Budget
import us.frollo.frollosdk.model.coredata.budgets.BudgetType
import us.frollo.frollosdksample.R
import us.frollo.frollosdksample.base.ARGUMENT
import us.frollo.frollosdksample.base.BaseStackActivity
import us.frollo.frollosdksample.extension.getMessage
import us.frollo.frollosdksample.utils.displayError
import us.frollo.frollosdksample.utils.observe
import us.frollo.frollosdksample.view.budgets.adapters.BudgetsAdapter

class BudgetsActivity : BaseStackActivity() {

    override val resourceId: Int
        get() = R.layout.activity_budgets

    companion object {
        private const val TAG = "BudgetsActivity"
    }

    private val mAdapter = BudgetsAdapter()
    private var fetchedLiveData: LiveData<Resource<List<Budget>>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        refreshData()
        actionBar?.title = getString(R.string.title_budgets)

        initView()
        initLiveData()
        refresh_layout?.onRefresh { refreshData() }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.goals_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_add -> {
                val list = BudgetType.values()
                selector("Select Budget Type", list.map { it.toString().replace("_", " ").capitalize() }) { _, index ->
                    startActivity<AddBudgetActivity>(AddBudgetActivity.ARG_MODE to list[index])
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initView() {
        recylerView.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(this@BudgetsActivity, LinearLayoutManager.VERTICAL))
            adapter = mAdapter.apply {
                onItemClick { model, _, _ ->
                    model?.let { showDetails(it) }
                }
            }
        }
    }

    private fun initLiveData() {
        fetchedLiveData?.removeObservers(this)
        fetchedLiveData = FrolloSDK.budgets.fetchBudgets()
        fetchedLiveData?.observe(this) {
            when (it?.status) {
                Resource.Status.SUCCESS -> it.data?.let { models -> loadData(models) }
                Resource.Status.ERROR -> displayError(it.error?.localizedDescription, "Fetch Budgets Failed")
            }
        }
    }

    private fun loadData(models: List<Budget>) {
        mAdapter.replaceAll(models)
    }

    private fun refreshData() {
        FrolloSDK.budgets.refreshBudgets { result ->
            refresh_layout?.isRefreshing = false

            when (result.status) {
                Result.Status.SUCCESS -> Log.d(TAG, "Budgets Refreshed")
                Result.Status.ERROR -> displayError(result.error?.getMessage(), "Budgets Refresh Failed")
            }
        }
    }

    private fun showDetails(model: Budget) {
        startActivity<BudgetPeriodsActivity>(ARGUMENT.ARG_DATA_1 to model.budgetId)
    }
}
