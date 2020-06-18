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

package us.frollo.frollosdksample.view.goals.adapters

import android.view.View
import kotlinx.android.synthetic.main.template_simple_item4.view.text_amount
import kotlinx.android.synthetic.main.template_simple_item4.view.text_title
import us.frollo.frollosdk.model.coredata.aggregation.accounts.Account
import us.frollo.frollosdksample.R
import us.frollo.frollosdksample.base.BaseRecyclerAdapter
import us.frollo.frollosdksample.base.BaseViewHolder
import us.frollo.frollosdksample.utils.display

class SelectAccountAdapter : BaseRecyclerAdapter<Account, SelectAccountAdapter.AccountViewHolder>(Account::class.java, accountComparator) {

    companion object {
        private val accountComparator = compareBy<Account> { it.attributes.accountType }.thenBy { it.accountName }
    }

    override fun getViewHolderLayout(viewType: Int) =
        R.layout.template_simple_item4

    override fun getViewHolder(view: View, viewType: Int) =
        AccountViewHolder(view)

    inner class AccountViewHolder(itemView: View) : BaseViewHolder<Account>(itemView) {

        override fun bind(model: Account) {
            itemView.text_title.text = model.nickName ?: model.accountName
            itemView.text_amount.text = model.availableBalance?.display ?: model.currentBalance?.display
        }

        override fun recycle() {
            itemView.text_title.text = null
            itemView.text_amount.text = null
        }
    }
}
