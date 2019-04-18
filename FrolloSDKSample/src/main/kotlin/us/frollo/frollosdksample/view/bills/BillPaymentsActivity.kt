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

package us.frollo.frollosdksample.view.bills

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_bills.*
import org.jetbrains.anko.support.v4.onRefresh
import org.threeten.bp.LocalDate
import us.frollo.frollosdk.FrolloSDK
import us.frollo.frollosdk.base.Resource
import us.frollo.frollosdk.base.Result
import us.frollo.frollosdk.model.coredata.bills.BillPayment
import us.frollo.frollosdk.model.coredata.bills.BillPaymentRelation
import us.frollo.frollosdk.model.coredata.bills.BillPaymentStatus
import us.frollo.frollosdksample.base.ARGUMENT
import us.frollo.frollosdksample.R
import us.frollo.frollosdksample.base.BaseStackActivity
import us.frollo.frollosdksample.utils.displayError
import us.frollo.frollosdksample.utils.observe
import us.frollo.frollosdksample.utils.showListDialog
import us.frollo.frollosdksample.utils.toString
import us.frollo.frollosdksample.view.bills.adapters.BillPaymentsAdapter

class BillPaymentsActivity : BaseStackActivity(), DialogInterface.OnClickListener {

    companion object {
        private const val TAG = "BillPayments"
        private const val REMOVE = 0
        private const val PAID_UNPAID = 1
    }

    private val mAdapter = BillPaymentsAdapter()
    private var billId: Long = -1
    private var selected: BillPaymentRelation? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        billId = intent.getLongExtra(ARGUMENT.ARG_DATA_1, -1)

        initView()
        initLiveData()
        refresh_layout.onRefresh { refreshData() }
    }

    override fun onResume() {
        super.onResume()

        refreshData()
    }

    private fun initView() {
        registerForContextMenu(recycler_bills)

        recycler_bills.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(this@BillPaymentsActivity, LinearLayoutManager.VERTICAL))
            adapter = mAdapter.apply {
                onItemLongClick { model, _, _ ->
                    selected = model
                    showOptions()
                }
            }
        }
    }

    private fun initLiveData() {
        FrolloSDK.bills.fetchBillPaymentsByBillIdWithRelation(billId).observe(this) {
            when (it?.status) {
                Resource.Status.SUCCESS -> it.data?.let { models -> loadData(models) }
                Resource.Status.ERROR -> displayError(it.error?.localizedDescription, "Fetch Bill Payments Failed")
            }
        }
    }

    private fun loadData(models: List<BillPaymentRelation>) {
        mAdapter.replaceAll(models)
    }

    private fun refreshData() {
        val toDate = LocalDate.now().toString(BillPayment.DATE_FORMAT_PATTERN)
        val fromDate = LocalDate.now().minusMonths(12).toString(BillPayment.DATE_FORMAT_PATTERN)

        FrolloSDK.bills.refreshBillPayments(fromDate = fromDate, toDate = toDate) { result ->
            refresh_layout.isRefreshing = false

            when (result.status) {
                Result.Status.SUCCESS -> Log.d(TAG, "Bill Payments Refreshed")
                Result.Status.ERROR -> displayError(result.error?.localizedDescription, "Refreshing Bill Payments Failed")
            }
        }
    }

    private fun showOptions() {
        val array = if (selected?.billPayment?.paymentStatus == BillPaymentStatus.PAID) {
            if (selected?.billPayment?.unpayable == true) arrayOf("Remove payment", "Mark as unpaid")
            else arrayOf("Remove payment")
        } else {
            arrayOf("Remove payment", "Mark as paid")
        }
        showListDialog(array = array, clickListener = this)
    }

    override fun onClick(dialog: DialogInterface?, which: Int) {
        when (which) {
            REMOVE -> removePayment()
            PAID_UNPAID -> markAsPaidOrUnpaid()
        }
    }

    private fun removePayment() {
        selected?.billPayment?.let { model ->
            FrolloSDK.bills.deleteBillPayment(model.billPaymentId) { result ->
                when (result.status) {
                    Result.Status.SUCCESS -> Log.d(TAG, "Bill Payment Deleted")
                    Result.Status.ERROR -> displayError(result.error?.localizedDescription, "Deleting Bill Payment Failed")
                }
            }

            selected = null
        }
    }

    private fun markAsPaidOrUnpaid() {
        val paid = selected?.billPayment?.paymentStatus != BillPaymentStatus.PAID

        selected?.billPayment?.let { model ->
            FrolloSDK.bills.updateBillPayment(billPaymentId = model.billPaymentId, paid = paid) { result ->
                when (result.status) {
                    Result.Status.SUCCESS -> Log.d(TAG, "Bill Payment Updated")
                    Result.Status.ERROR -> displayError(result.error?.localizedDescription, "Updating Bill Payment Failed")
                }
            }

            selected = null
        }
    }

    override val resourceId: Int
        get() = R.layout.fragment_bills
}
