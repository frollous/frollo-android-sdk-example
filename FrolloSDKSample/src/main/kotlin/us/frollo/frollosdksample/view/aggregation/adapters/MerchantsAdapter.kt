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

package us.frollo.frollosdksample.view.aggregation.adapters

import android.view.View
import kotlinx.android.synthetic.main.template_merchant_item.view.text_title
import us.frollo.frollosdk.model.coredata.aggregation.merchants.Merchant
import us.frollo.frollosdksample.R
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
