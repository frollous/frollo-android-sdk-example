package us.frollo.frollosdksample.view.aggregation

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_transaction_categories.*
import org.jetbrains.anko.support.v4.onRefresh
import us.frollo.frollosdk.FrolloSDK
import us.frollo.frollosdk.base.Resource
import us.frollo.frollosdk.base.Result
import us.frollo.frollosdk.model.coredata.aggregation.transactioncategories.TransactionCategory
import us.frollo.frollosdksample.R
import us.frollo.frollosdksample.base.ARGUMENT.ARG_GENERIC
import us.frollo.frollosdksample.base.BaseStackActivity
import us.frollo.frollosdksample.utils.displayError
import us.frollo.frollosdksample.utils.observe
import us.frollo.frollosdksample.view.aggregation.adapters.TransactionCategoriesAdapter

class TransactionCategoriesActivity : BaseStackActivity() {

    companion object {
        private const val TAG = "TransactionCategories"
    }

    private val categoriesAdapter = TransactionCategoriesAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_transaction_categories)

        initView()
        initLiveData()
        refresh_layout.onRefresh { refreshTransactionCategories() }
    }

    private fun initView() {
        recycler_categories.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(this@TransactionCategoriesActivity, LinearLayoutManager.VERTICAL))
            adapter = categoriesAdapter.apply {
                onItemClick { model, _, _ ->
                    model?.let { chooseCategory(it) }
                }
            }
        }
    }

    private fun initLiveData() {
        FrolloSDK.aggregation.fetchTransactionCategories().observe(this) {
            when (it?.status) {
                Resource.Status.SUCCESS -> it.data?.let { providers -> loadCategories(providers) }
                Resource.Status.ERROR -> displayError(it.error?.localizedDescription, "Fetch Transaction Categories Failed")
            }
        }
    }

    private fun loadCategories(categories: List<TransactionCategory>) {
        categoriesAdapter.replaceAll(categories)
    }

    private fun refreshTransactionCategories() {
        FrolloSDK.aggregation.refreshTransactionCategories { result ->
            refresh_layout.isRefreshing = false

            when (result.status) {
                Result.Status.SUCCESS -> Log.d(TAG, "Transaction Categories Refreshed")
                Result.Status.ERROR -> displayError(result.error?.localizedDescription, "Refreshing Transaction Categories Failed")
            }
        }
    }

    private fun chooseCategory(category: TransactionCategory) {
        setResult(Activity.RESULT_OK, Intent().putExtra(ARG_GENERIC, category.transactionCategoryId))
        finish()
    }
}
