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

package us.frollo.frollosdksample.view.reports.adapters

import android.view.View
import kotlinx.android.synthetic.main.template_simple_item2.view.*
import us.frollo.frollosdk.model.coredata.aggregation.accounts.AccountRelation
import us.frollo.frollosdksample.R
import us.frollo.frollosdksample.base.BaseRecyclerAdapter
import us.frollo.frollosdksample.base.BaseViewHolder

class ReportAccountsAdapter : BaseRecyclerAdapter<AccountRelation, ReportAccountsAdapter.AccountViewHolder>(AccountRelation::class.java, accountsComparator) {

    companion object {
        private val accountsComparator = compareBy<AccountRelation> { it.providerAccount?.providerAccount?.providerAccountId }
            .thenBy { it.account?.attributes?.accountType }.thenBy { it.account?.accountName }
    }

    override fun getViewHolderLayout(viewType: Int) =
        R.layout.template_simple_item2

    override fun getViewHolder(view: View, viewType: Int) =
        AccountViewHolder(view)

    inner class AccountViewHolder(itemView: View) : BaseViewHolder<AccountRelation>(itemView) {

        override fun bind(model: AccountRelation) {
            itemView.text_title.text = model.providerAccount?.provider?.providerName ?: itemView.context.resources.getString(R.string.str_unknown_provider_account)
            itemView.text_subtitle.text = model.account?.accountName
        }

        override fun recycle() {
            itemView.text_title.text = null
            itemView.text_subtitle.text = null
        }
    }
}
