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
import kotlinx.android.synthetic.main.template_simple_item2.view.*
import us.frollo.frollosdk.model.coredata.aggregation.provideraccounts.ProviderAccountRelation
import us.frollo.frollosdksample.R
import us.frollo.frollosdksample.base.BaseRecyclerAdapter
import us.frollo.frollosdksample.base.BaseViewHolder

class ProviderAccountsAdapter : BaseRecyclerAdapter<ProviderAccountRelation, ProviderAccountsAdapter.ProviderAccountViewHolder>(ProviderAccountRelation::class.java, providerAccountsComparator) {

    companion object {
        private val providerAccountsComparator = compareBy<ProviderAccountRelation> { it.providerAccount?.providerAccountId }
    }

    override fun getViewHolderLayout(viewType: Int) =
        R.layout.template_simple_item2

    override fun getViewHolder(view: View, viewType: Int) =
        ProviderAccountViewHolder(view)

    inner class ProviderAccountViewHolder(itemView: View) : BaseViewHolder<ProviderAccountRelation>(itemView) {

        override fun bind(model: ProviderAccountRelation) {
            itemView.text_title.text = model.provider?.providerName ?: itemView.context.resources.getString(R.string.str_unknown_provider_account)
            itemView.text_subtitle.text = itemView.context.resources.getString(R.string.str_accounts_count, model.accounts?.size ?: 0)
        }

        override fun recycle() {
            itemView.text_title.text = null
            itemView.text_subtitle.text = null
        }
    }
}
