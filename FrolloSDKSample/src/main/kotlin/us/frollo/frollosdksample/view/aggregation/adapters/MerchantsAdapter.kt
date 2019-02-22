package us.frollo.frollosdksample.view.aggregation.adapters

import android.view.View
import kotlinx.android.synthetic.main.template_merchant_item.view.*
import us.frollo.frollosdk.model.coredata.aggregation.merchants.Merchant
import us.frollo.frollosdksample.*
import us.frollo.frollosdksample.base.BaseRecyclerAdapter
import us.frollo.frollosdksample.base.BaseViewHolder

class MerchantsAdapter : BaseRecyclerAdapter<Merchant,
        MerchantsAdapter.MerchantViewHolder>(Merchant::class.java, merchantComparator) {

    companion object {
        private val merchantComparator = compareBy<Merchant> { it.name }
    }

    override fun getViewHolderLayout(viewType: Int) =
            R.layout.template_merchant_item

    override fun getViewHolder(view: View, viewType: Int) =
            MerchantViewHolder(view)

    inner class MerchantViewHolder(itemView: View) : BaseViewHolder<Merchant>(itemView) {

        override fun bind(model: Merchant) {
            itemView.text_title.text = model.name
        }

        override fun recycle() {
            itemView.text_title.text = null
        }
    }
}