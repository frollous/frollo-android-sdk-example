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

package us.frollo.frollosdksample.view.aggregation

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_transaction_details.*
import org.jetbrains.anko.selector
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.toast
import us.frollo.frollosdk.FrolloSDK
import us.frollo.frollosdk.base.Resource
import us.frollo.frollosdk.base.Result
import us.frollo.frollosdk.model.coredata.aggregation.transactions.Transaction
import us.frollo.frollosdk.model.coredata.aggregation.transactions.TransactionRelation
import us.frollo.frollosdk.model.coredata.shared.BudgetCategory
import us.frollo.frollosdksample.base.ARGUMENT
import us.frollo.frollosdksample.R
import us.frollo.frollosdksample.base.ARGUMENT.ARG_DATA_1
import us.frollo.frollosdksample.base.ARGUMENT.ARG_DATA_2
import us.frollo.frollosdksample.base.BaseStackActivity
import us.frollo.frollosdksample.base.REQUEST.REQUEST_SELECTION
import us.frollo.frollosdksample.utils.changeDateFormat
import us.frollo.frollosdksample.utils.display
import us.frollo.frollosdksample.utils.displayError
import us.frollo.frollosdksample.utils.hide
import us.frollo.frollosdksample.utils.ifNotNull
import us.frollo.frollosdksample.utils.observe
import us.frollo.frollosdksample.utils.show

class TransactionDetailsActivity : BaseStackActivity() {

    companion object {
        private const val TAG = "TransactionDetails"
    }

    private var fetchedTransaction: Transaction? = null
    private var transactionId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        transactionId = intent.getLongExtra(ARGUMENT.ARG_DATA_1, -1)

        initLiveData()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.details_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_save -> {
                save()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initLiveData() {
        FrolloSDK.aggregation.fetchTransactionWithRelation(transactionId).observe(this) {
            when (it?.status) {
                Resource.Status.SUCCESS -> it.data?.let { transaction -> loadView(transaction) }
                Resource.Status.ERROR -> displayError(it.error?.localizedDescription, "Fetch Transaction Failed")
            }
        }
    }

    private fun loadView(model: TransactionRelation) {
        fetchedTransaction = model.transaction

        fetchedTransaction?.let { transaction ->
            text_title.text = transaction.description?.user ?: transaction.description?.simple ?: transaction.description?.original
            text_description.text = transaction.description?.original
            text_amount.text = transaction.amount.display
            text_date.text = transaction.transactionDate.changeDateFormat(Transaction.DATE_FORMAT_PATTERN, "dd/MM/yyyy")
            text_transaction_category.text = model.transactionCategory?.name
            text_budget_category.text = budgetCategoryLabel(transaction.budgetCategory)
            text_merchant.text = model.merchant?.name
            switch_exclude.isChecked = !transaction.included
        }

        text_transaction_category.setOnClickListener { showCategories() }
        text_budget_category.setOnClickListener { pickBudget() }
        text_merchant.setOnClickListener { showMerchants() }
        switch_exclude.setOnCheckedChangeListener { _, isChecked -> fetchedTransaction?.included = !isChecked }
    }

    private fun showCategories() {
        startActivityForResult<TransactionCategoriesActivity>(REQUEST_SELECTION)
    }

    private fun showMerchants() {
        startActivity<MerchantsActivity>()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

        if (requestCode == REQUEST_SELECTION && resultCode == Activity.RESULT_OK) {
            val categoryId = intent?.getLongExtra(ARG_DATA_1, -1)
            val categoryName = intent?.getStringExtra(ARG_DATA_2)
            ifNotNull(categoryId, categoryName) { id, name ->
                fetchedTransaction?.categoryId = id
                text_transaction_category.text = name
            }
        }
    }

    private fun pickBudget() {
        val budgetValues = BudgetCategory.values()

        selector("Budget Category", budgetValues.map { budgetCategoryLabel(it) }) { _, index ->
            val budget = budgetValues[index]
            fetchedTransaction?.budgetCategory = budget
            text_budget_category.text = budgetCategoryLabel(budget)
        }
    }

    private fun save() {
        progress_bar.show()

        fetchedTransaction?.let {
            FrolloSDK.aggregation.updateTransaction(transactionId, it) { result ->
                progress_bar.hide()

                when (result.status) {
                    Result.Status.SUCCESS -> toast("Updated!")
                    Result.Status.ERROR -> displayError(result.error?.localizedDescription, "Updating Transaction Failed")
                }
            }
        }
    }

    private fun budgetCategoryLabel(budgetCategory: BudgetCategory): String {
        return when (budgetCategory) {
            BudgetCategory.INCOME -> "Income"
            BudgetCategory.LIFESTYLE -> "Lifestyle"
            BudgetCategory.LIVING -> "Living"
            BudgetCategory.ONE_OFF -> "One Off"
            BudgetCategory.SAVINGS -> "Savings"
        }
    }

    override val resourceId: Int
        get() = R.layout.activity_transaction_details
}
