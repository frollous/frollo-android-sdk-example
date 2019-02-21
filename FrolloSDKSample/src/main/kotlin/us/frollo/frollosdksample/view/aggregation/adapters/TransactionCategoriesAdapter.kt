package us.frollo.frollosdksample.view.aggregation.adapters

import android.view.View
import kotlinx.android.synthetic.main.template_transaction_category_item.view.*
import us.frollo.frollosdk.model.coredata.aggregation.transactioncategories.TransactionCategory
import us.frollo.frollosdksample.*
import us.frollo.frollosdksample.base.BaseRecyclerAdapter
import us.frollo.frollosdksample.base.BaseViewHolder

class TransactionCategoriesAdapter : BaseRecyclerAdapter<TransactionCategory,
        TransactionCategoriesAdapter.TransactionCategoryViewHolder>(TransactionCategory::class.java, categoryComparator) {

    companion object {
        private val categoryComparator = compareBy<TransactionCategory> { it.name }
    }

    override fun getViewHolderLayout(viewType: Int) =
            R.layout.template_transaction_category_item

    override fun getViewHolder(view: View, viewType: Int) =
            TransactionCategoryViewHolder(view)

    inner class TransactionCategoryViewHolder(itemView: View) : BaseViewHolder<TransactionCategory>(itemView) {

        override fun bind(model: TransactionCategory) {
            itemView.text_title.text = model.name
        }

        override fun recycle() {
            itemView.text_title.text = null
        }
    }
}