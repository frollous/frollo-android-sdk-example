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
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.DatePicker
import kotlinx.android.synthetic.main.activity_add_budget.frequency
import kotlinx.android.synthetic.main.activity_add_budget.periodAmount
import kotlinx.android.synthetic.main.activity_add_budget.progress_bar_layout
import kotlinx.android.synthetic.main.activity_add_budget.startDate
import kotlinx.android.synthetic.main.activity_add_budget.budgetCategory
import kotlinx.android.synthetic.main.activity_add_budget.imageUrl
import kotlinx.android.synthetic.main.activity_add_budget.merchantCategory
import kotlinx.android.synthetic.main.activity_add_budget.sectionBudgetCategory
import kotlinx.android.synthetic.main.activity_add_budget.sectionMerchantCategory
import kotlinx.android.synthetic.main.activity_add_budget.sectionTransactionCategory
import kotlinx.android.synthetic.main.activity_add_budget.transactionCategory

import org.jetbrains.anko.selector
import org.threeten.bp.LocalDate
import us.frollo.frollosdk.FrolloSDK
import us.frollo.frollosdk.base.Result
import us.frollo.frollosdk.model.coredata.aggregation.merchants.Merchant
import us.frollo.frollosdk.model.coredata.aggregation.transactioncategories.TransactionCategory
import us.frollo.frollosdk.model.coredata.budgets.Budget
import us.frollo.frollosdk.model.coredata.budgets.BudgetFrequency
import us.frollo.frollosdk.model.coredata.budgets.BudgetType
import us.frollo.frollosdk.model.coredata.shared.BudgetCategory
import us.frollo.frollosdksample.R
import us.frollo.frollosdksample.base.BaseStackActivity
import us.frollo.frollosdksample.utils.displayError
import us.frollo.frollosdksample.utils.hide
import us.frollo.frollosdksample.utils.observe
import us.frollo.frollosdksample.utils.show
import us.frollo.frollosdksample.utils.toString
import us.frollo.frollosdksample.view.shared.DatePickerFragment
import java.math.BigDecimal

class AddBudgetActivity : BaseStackActivity(), DatePickerFragment.CustomOnDateSetListener {

    companion object {
        const val ARG_MODE = "ARG_MODE"
    }

    private var budgetFrequencyX = BudgetFrequency.WEEKLY
    private var budgetCategoryX = BudgetCategory.LIVING
    private var startDateX: LocalDate = LocalDate.now()
    private var periodAmountX: BigDecimal = BigDecimal(0)
    lateinit var mode: BudgetType
    private var merchantList: List<Merchant> = arrayListOf()
    private var transactionCategoryList: List<TransactionCategory> = arrayListOf()
    private var merchantId = -1L
    private var transactionCategoyId = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mode = intent.getSerializableExtra(ARG_MODE) as BudgetType

        when (mode) {
            BudgetType.BUDGET_CATEGORY -> {
                sectionBudgetCategory.show()
            }
            BudgetType.MERCHANT -> {
                sectionMerchantCategory.show()
                val merchantsLiveData = FrolloSDK.aggregation.fetchMerchants()
                merchantsLiveData.removeObservers(this)
                merchantsLiveData.observe(this) {
                    it?.data?.let {
                        merchantList = it
                        merchantCategory.isEnabled = true
                    }
                }
            }
            BudgetType.TRANSACTION_CATEGORY -> {
                sectionTransactionCategory.show()
                val transactionCategoryLiveData = FrolloSDK.aggregation.fetchTransactionCategories()
                transactionCategoryLiveData.removeObservers(this)
                transactionCategoryLiveData.observe(this) {
                    it?.data?.let {
                        transactionCategoryList = it
                        transactionCategory.isEnabled = true
                    }
                }
            }
        }

        initView()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.create_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_create -> {
                createBudget()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initView() {

        frequency.text = budgetFrequencyX.name
        startDate.text = startDateX.toString(Budget.DATE_FORMAT_PATTERN)

        budgetCategory.text = BudgetCategory.LIFESTYLE.name
        budgetCategory.setOnClickListener { pickBudgetCategory() }
        frequency.setOnClickListener { pickFrequency() }
        startDate.setOnClickListener {
            showDatePickerDialog(startDateX, DateMode.START_DATE)
        }
        transactionCategory.setOnClickListener {
            selector("Select transaction category", transactionCategoryList.map { it.name }) { _, index ->
                transactionCategoyId = transactionCategoryList[index].transactionCategoryId
                transactionCategory.text = transactionCategoryList[index].name
            }
        }
        merchantCategory.setOnClickListener {
            selector("Select merchant category", merchantList.map { it.name }) { _, index ->
                merchantId = merchantList[index].merchantId
                merchantCategory.text = merchantList[index].name
            }
        }
    }

    private fun pickBudgetCategory() {
        val values = BudgetCategory.values()

        selector("Frequency", values.map { it.name }) { _, index ->
            budgetCategoryX = values[index]
            budgetCategory.text = budgetCategoryX.name
        }
    }

    private fun pickFrequency() {
        val values = BudgetFrequency.values()

        selector("Frequency", values.map { it.name }) { _, index ->
            budgetFrequencyX = values[index]
            frequency.text = budgetFrequencyX.name
        }
    }

    private fun showDatePickerDialog(date: LocalDate, dateMode: DateMode) {
        val datePicker = DatePickerFragment(this, dateMode.name, date, LocalDate.now())
        datePicker.show(supportFragmentManager, "datePicker")
    }

    override fun onDateSet(view: DatePicker, date: LocalDate) {
        if (view.tag == DateMode.START_DATE.name) {
            startDateX = date
        }
    }

    private fun createBudget() {

        if (!validatePeriodAmount())
            return

        when (mode) {
            BudgetType.MERCHANT -> {

                if (merchantId == -1L) {
                    displayError("Kindly select a merchant", "Create Budget Failed")
                    return
                }
                progress_bar_layout.show()
                FrolloSDK.budgets.createMerchantBudget(budgetFrequencyX, periodAmountX, merchantId, startDateX.toString(Budget.DATE_FORMAT_PATTERN), imageUrl.text.toString()) {
                    onBudgetCreated(it)
                }
            }
            BudgetType.TRANSACTION_CATEGORY -> {
                if (transactionCategoyId == -1L) {
                    displayError("Kindly select a transaction category", "Create Budget Failed")
                    return
                }
                progress_bar_layout.show()
                FrolloSDK.budgets.createCategoryBudget(budgetFrequencyX, periodAmountX, transactionCategoyId, startDateX.toString(Budget.DATE_FORMAT_PATTERN), imageUrl.text.toString()) {
                    onBudgetCreated(it)
                }
            }
            BudgetType.BUDGET_CATEGORY -> {
                progress_bar_layout.show()
                FrolloSDK.budgets.createBudgetCategoryBudget(budgetFrequencyX, periodAmountX, budgetCategoryX, startDateX.toString(Budget.DATE_FORMAT_PATTERN),
                        imageUrl.text.toString()) {
                }
            }
        }
    }

    private fun onBudgetCreated(result: Result) {
        progress_bar_layout.hide()

        when (result.status) {
            Result.Status.SUCCESS -> { finish() }
            Result.Status.ERROR -> displayError(result.error?.localizedMessage, "Create Budget Failed")
        }
    }

    private fun validatePeriodAmount(): Boolean {
        val amount = periodAmount.text.toString()
        return if (amount.isNotBlank() && !amount.startsWith(".")) {
            periodAmountX = BigDecimal(amount)
            periodAmountX != BigDecimal(0)
        } else {
            displayError("Missing period amount", "Create Budget Failed")
            false
        }
    }

    override val resourceId: Int
        get() = R.layout.activity_add_budget

    private enum class DateMode {
        START_DATE, END_DATE
    }
}
