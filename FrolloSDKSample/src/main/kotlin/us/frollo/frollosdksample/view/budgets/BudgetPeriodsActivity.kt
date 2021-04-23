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

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_budget_periods.frequency
import kotlinx.android.synthetic.main.activity_budget_periods.periodAmount
import kotlinx.android.synthetic.main.activity_budget_periods.recyclerView
import kotlinx.android.synthetic.main.activity_budget_periods.refresh_layout
import kotlinx.android.synthetic.main.activity_budget_periods.text_edit_save
import kotlinx.android.synthetic.main.activity_budget_periods.type
import kotlinx.android.synthetic.main.progress_bar_full_screen.progress_bar_layout
import kotlinx.android.synthetic.main.progress_bar_full_screen.text_progress_title
import kotlinx.android.synthetic.main.template_budget_item.typeValue
import org.jetbrains.anko.alert
import org.jetbrains.anko.support.v4.onRefresh
import org.jetbrains.anko.toast
import us.frollo.frollosdk.FrolloSDK
import us.frollo.frollosdk.base.PaginatedResult
import us.frollo.frollosdk.base.Resource
import us.frollo.frollosdk.base.Result
import us.frollo.frollosdk.model.coredata.budgets.Budget
import us.frollo.frollosdk.model.coredata.budgets.BudgetPeriod
import us.frollo.frollosdksample.R
import us.frollo.frollosdksample.base.ARGUMENT
import us.frollo.frollosdksample.base.BaseStackActivity
import us.frollo.frollosdksample.extension.getMessage
import us.frollo.frollosdksample.utils.displayError
import us.frollo.frollosdksample.utils.hide
import us.frollo.frollosdksample.utils.observe
import us.frollo.frollosdksample.utils.show
import us.frollo.frollosdksample.utils.showThemed
import us.frollo.frollosdksample.view.budgets.adapters.BudgetPeriodsAdapter
import java.lang.Exception

class BudgetPeriodsActivity : BaseStackActivity() {

    private enum class EditMode {
        EDIT, SAVE
    }

    companion object {
        private const val TAG = "GoalPeriods"
    }

    private val periodsAdapter = BudgetPeriodsAdapter()
    private var budget: Budget? = null
    private var budgetId: Long = -1
    private var menuDelete: MenuItem? = null
    private var editMode = EditMode.EDIT
    private lateinit var editTextBackground: Drawable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        budgetId = intent.getLongExtra(ARGUMENT.ARG_DATA_1, -1)

        initView()
        initLiveData()
        refresh_layout.onRefresh { refreshBudgetPeriods() }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.budget_periods_menu, menu)
        menuDelete = menu?.findItem(R.id.delete)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete -> {
                alert("Are you sure you wish to delete the budget?", "Delete Budget") {
                    positiveButton("Delete") {
                        deleteBudget()
                    }
                    negativeButton("Cancel") {}
                }.showThemed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun deleteBudget() {
        menuDelete?.isEnabled = false
        text_progress_title.text = getString(R.string.budget_period_delete)
        progress_bar_layout.show()

        FrolloSDK.budgets.deleteBudget(budgetId) { result ->
            progress_bar_layout.hide()

            when (result.status) {
                Result.Status.SUCCESS -> {
                    toast("Budget Deleted!")
                    finish()
                }
                Result.Status.ERROR -> {
                    menuDelete?.isEnabled = true
                    displayError(result.error?.getMessage(), "Deleting Budget Failed")
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        refreshBudgetPeriods()
    }

    private fun initView() {
        editTextBackground = periodAmount.background
        setEditMode(EditMode.EDIT)
        text_edit_save.setOnClickListener {
            when (editMode) {
                EditMode.EDIT -> {
                    setEditMode(EditMode.SAVE)
                }
                EditMode.SAVE -> {
                    updateBudget()
                }
            }
        }

        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(this@BudgetPeriodsActivity, LinearLayoutManager.VERTICAL))
            adapter = periodsAdapter
        }
    }

    private fun updateBudget() {
        if (periodAmount.text.toString().isBlank()) {
            displayError("Missing period amount", "Updating Budget Failed")
            return
        }

        try {
            periodAmount.text.toString().toBigDecimal()
        } catch (e: Exception) {
            displayError("Missing period amount", "Updating Budget Failed")
            return
        }

        budget?.let {
            text_progress_title.text = getString(R.string.budget_period_update)
            progress_bar_layout.show()
            it.periodAmount = periodAmount.text.toString().toBigDecimal()

            FrolloSDK.budgets.updateBudget(it) { result ->
                progress_bar_layout.hide()

                when (result.status) {
                    Result.Status.SUCCESS -> {
                        setEditMode(EditMode.EDIT)
                        refreshBudgetPeriods()
                    }
                    Result.Status.ERROR -> {
                        displayError(result.error?.getMessage(), "Updating Budget Failed")
                    }
                }
            }
        }
    }

    private fun setEditMode(mode: EditMode) {
        editMode = mode
        when (mode) {
            EditMode.EDIT -> {
                text_edit_save.text = "Edit"
                periodAmount.background = ColorDrawable(Color.TRANSPARENT)
                periodAmount.isEnabled = false
            }
            EditMode.SAVE -> {
                text_edit_save.text = "Save"
                periodAmount.background = editTextBackground
                periodAmount.isEnabled = true
            }
        }
    }

    private fun initLiveData() {
        FrolloSDK.budgets.fetchBudget(budgetId).observe(this) {
            when (it?.status) {
                Resource.Status.SUCCESS -> it.data?.let { budget1 ->
                    budget = budget1
                    periodAmount.setText(budget1.periodAmount.toPlainString())
                    frequency.text = budget1.frequency.name.capitalize()
                    type.text = Html.fromHtml("<b>Type</b> ${budget1.type.name}")
                    typeValue.text = Html.fromHtml("<b>Value</b> ${budget1.typeValue.toUpperCase()}")
                    text_edit_save.show()
                }
                Resource.Status.ERROR -> displayError(it.error?.localizedDescription, "Fetch Goal Failed")
            }
        }

        FrolloSDK.budgets.fetchBudgetPeriods(budgetId).observe(this) {
            when (it?.status) {
                Resource.Status.SUCCESS -> it.data?.let { models -> loadData(models) }
                Resource.Status.ERROR -> displayError(it.error?.localizedDescription, "Fetch Goal Periods Failed")
            }
        }
    }

    private fun loadData(models: List<BudgetPeriod>) {
        periodsAdapter.replaceAll(models)
    }

    private fun refreshBudgetPeriods() {
        FrolloSDK.budgets.refreshBudgetPeriodsByBudgetIdWithPagination(budgetId = budgetId) { result ->
            refresh_layout.isRefreshing = false

            when (result) {
                is PaginatedResult.Success -> Log.d(TAG, "Budget Periods Refreshed")
                is PaginatedResult.Error -> displayError(result.error?.getMessage(), "Refreshing Budget Periods Failed")
            }
        }
    }

    override val resourceId: Int
        get() = R.layout.activity_budget_periods
}
