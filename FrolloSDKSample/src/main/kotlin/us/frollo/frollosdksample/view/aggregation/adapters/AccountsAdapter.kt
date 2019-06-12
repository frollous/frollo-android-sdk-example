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
import kotlinx.android.synthetic.main.template_account_item.view.progress_bar
import kotlinx.android.synthetic.main.template_account_item.view.text_account_name
import kotlinx.android.synthetic.main.template_account_item.view.text_amount
import kotlinx.android.synthetic.main.template_account_item.view.text_last_updated
import kotlinx.android.synthetic.main.template_account_item.view.text_status
import us.frollo.frollosdk.model.coredata.aggregation.accounts.Account
import us.frollo.frollosdk.model.coredata.aggregation.provideraccounts.AccountRefreshStatus
import us.frollo.frollosdksample.R
import us.frollo.frollosdksample.base.BaseRecyclerAdapter
import us.frollo.frollosdksample.base.BaseViewHolder
import us.frollo.frollosdksample.utils.display
import us.frollo.frollosdksample.utils.formatISOString
import us.frollo.frollosdksample.utils.hide
import us.frollo.frollosdksample.utils.show

class AccountsAdapter : BaseRecyclerAdapter<Account, AccountsAdapter.AccountViewHolder>(Account::class.java, accountComparator) {

    companion object {
        private val accountComparator = compareBy<Account> { it.attributes.accountType }.thenBy { it.accountName }
    }

    override fun getViewHolderLayout(viewType: Int) =
            R.layout.template_account_item

    override fun getViewHolder(view: View, viewType: Int) =
            AccountViewHolder(view)

    inner class AccountViewHolder(itemView: View) : BaseViewHolder<Account>(itemView) {

        override fun bind(model: Account) {
            itemView.text_account_name.text = model.nickName ?: model.accountName
            itemView.text_last_updated.text = model.refreshStatus?.lastRefreshed?.let {
                String.format("Last updated %s", it.formatISOString("dd/MM/yyyy"))
            } ?: run { "Never updated" }
            itemView.text_amount.text = model.availableBalance?.display ?: model.currentBalance?.display

            itemView.text_amount.show()
            itemView.progress_bar.hide()

            when (model.refreshStatus?.status) {
                AccountRefreshStatus.ADDING -> {
                    itemView.progress_bar.show()
                    itemView.text_amount.hide()
                    itemView.text_status.text = "Adding"
                }
                AccountRefreshStatus.FAILED -> itemView.text_status.text = "Failed"
                AccountRefreshStatus.NEEDS_ACTION -> itemView.text_status.text = "Needs Action"
                AccountRefreshStatus.SUCCESS -> itemView.text_status.text = "Success"
                AccountRefreshStatus.UPDATING -> {
                    itemView.text_amount.hide()
                    itemView.text_status.text = "Updating"
                }
            }
        }

        override fun recycle() {
            itemView.text_account_name.text = null
            itemView.text_status.text = null
            itemView.text_last_updated.text = null
            itemView.text_amount.text = null
        }
    }
}