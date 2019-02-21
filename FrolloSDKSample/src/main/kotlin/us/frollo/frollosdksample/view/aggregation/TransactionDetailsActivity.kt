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
import us.frollo.frollosdk.model.coredata.shared.BudgetCategory
import us.frollo.frollosdksample.base.ARGUMENT
import us.frollo.frollosdksample.R
import us.frollo.frollosdksample.base.ARGUMENT.ARG_GENERIC
import us.frollo.frollosdksample.base.BaseStackActivity
import us.frollo.frollosdksample.base.REQUEST.REQUEST_SELECTION
import us.frollo.frollosdksample.utils.*

class TransactionDetailsActivity : BaseStackActivity() {

    companion object {
        private const val TAG = "TransactionDetails"
    }

    private var fetchedTransaction: Transaction? = null
    private var transactionId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_transaction_details)

        transactionId = intent.getLongExtra(ARGUMENT.ARG_GENERIC, -1)

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
        FrolloSDK.aggregation.fetchTransaction(transactionId).observe(this) {
            when (it?.status) {
                Resource.Status.SUCCESS -> it.data?.let { transaction -> loadView(transaction) }
                Resource.Status.ERROR -> displayError(it.error?.localizedDescription, "Fetch Transaction Failed")
            }
        }
    }

    private fun loadView(transaction: Transaction) {
        fetchedTransaction = transaction

        text_title.text = transaction.description?.user ?: transaction.description?.simple ?: transaction.description?.original
        text_description.text = transaction.description?.original
        text_amount.text = transaction.amount.display
        text_date.text = transaction.transactionDate.changeDateFormat(Transaction.DATE_FORMAT_PATTERN, "dd/MM/yyyy")
        text_transaction_category.text = transaction.categoryId.toString()
        text_budget_category.text = budgetCategoryLabel(transaction.budgetCategory)
        switch_exclude.isChecked = !transaction.included

        text_transaction_category.setOnClickListener { showCategories() }
        text_budget_category.setOnClickListener { pickBudget() }
        switch_exclude.setOnCheckedChangeListener { _, isChecked -> fetchedTransaction?.included = !isChecked }
    }

    private fun showCategories() {
        startActivityForResult<TransactionCategoriesActivity>(REQUEST_SELECTION)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

        if (requestCode == REQUEST_SELECTION && resultCode == Activity.RESULT_OK) {
            val categoryId = intent?.getLongExtra(ARG_GENERIC, -1)
            categoryId?.let {
                fetchedTransaction?.categoryId = it
                text_transaction_category.text = it.toString()
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
            BudgetCategory.GOALS -> "Savings"
        }
    }
}
