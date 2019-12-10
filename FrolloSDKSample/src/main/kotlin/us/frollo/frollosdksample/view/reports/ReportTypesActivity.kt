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

package us.frollo.frollosdksample.view.reports

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_report_types.*
import us.frollo.frollosdk.model.coredata.reports.ReportGrouping
import us.frollo.frollosdk.model.coredata.reports.TransactionReportPeriod
import us.frollo.frollosdksample.R
import us.frollo.frollosdksample.base.BaseStackActivity
import us.frollo.frollosdksample.display.GroupModel
import us.frollo.frollosdksample.utils.hide
import us.frollo.frollosdksample.utils.show

class ReportTypesActivity : BaseStackActivity(), ListDialogFragment.OnDialogItemSelectedListener {

    companion object {
        private const val TAG = "ReportTypes"
    }

    private var selectedReportType = ReportType.BUDGET_CATEGORY
    private var selectedReportPeriod = TransactionReportPeriod.ANNUALLY
    private var selectedTransactionCategoryId: Long? = null
    private var selectedMerchantId: Long? = null
    private var selectedBudgetCategoryId: Long? = null
    private var selectedTag: String? = null
    private var selectedGrouping: ReportGrouping? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initView()
    }

    private fun initView() {
        with(spinner_report_type) {
            adapter = ArrayAdapter<String>(this@ReportTypesActivity, R.layout.template_spinner_item, ReportType.values().map { getString(it.textResource) })
            onItemSelectedListener = itemSelectedListener
        }

        with(spinner_period) {
            adapter = ArrayAdapter<TransactionReportPeriod>(this@ReportTypesActivity, R.layout.template_spinner_item, TransactionReportPeriod.values())
            onItemSelectedListener = itemSelectedListener
        }

        val defaultText = getString(R.string.str_default_or_all)

        with(text_category) {
            setText(defaultText)
            setOnClickListener { showOptions(ListDialogFragment.ListType.TRANSACTION_CATEGORIES) }
        }

        with(text_merchant) {
            setText(defaultText)
            setOnClickListener { showOptions(ListDialogFragment.ListType.MERCHANTS) }
        }

        with(text_budget_category) {
            setText(defaultText)
            setOnClickListener { showOptions(ListDialogFragment.ListType.BUDGET_CATEGORIES) }
        }

        with(text_tag) {
            setText(defaultText)
            setOnClickListener { showOptions(ListDialogFragment.ListType.TAGS) }
        }

        with(text_report_grouping) {
            setText(defaultText)
            setOnClickListener { showOptions(ListDialogFragment.ListType.GROUPING) }
        }

        btn_fetch_report.setOnClickListener {
            showReportGrouping()
        }
    }

    private fun showOptions(type: ListDialogFragment.ListType) {
        val fragment = ListDialogFragment.getInstance(type)
        fragment.show(supportFragmentManager, type.name)
    }

    private val itemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {}
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
            when (parent?.id) {
                spinner_report_type.id -> {
                    val type = ReportType.values()[pos]
                    selectedReportType = type
                    updateSpinners(type)
                }
                spinner_period.id -> {
                    selectedReportPeriod = TransactionReportPeriod.values()[pos]
                }
            }
        }
    }

    override fun onDialogItemSelected(listType: ListDialogFragment.ListType, model: GroupModel) {
        when (listType) {
            ListDialogFragment.ListType.TRANSACTION_CATEGORIES -> {
                selectedTransactionCategoryId = if (model.id != -1L) model.id else null
                text_category.setText(model.name)
            }
            ListDialogFragment.ListType.MERCHANTS -> {
                selectedMerchantId = if (model.id != -1L) model.id else null
                text_merchant.setText(model.name)
            }
            ListDialogFragment.ListType.BUDGET_CATEGORIES -> {
                selectedBudgetCategoryId = if (model.id != -1L) model.id else null
                text_budget_category.setText(model.name)
            }
            ListDialogFragment.ListType.TAGS -> {
                selectedTag = if (model.id != -1L) model.name else null
                text_tag.setText(model.name)
            }
            ListDialogFragment.ListType.GROUPING -> {
                selectedGrouping = if (model.id != -1L) ReportGrouping.values()[model.id.toInt()] else null
                text_report_grouping.setText(model.name)
            }
        }
    }

    private fun updateSpinners(type: ReportType) {
        when (type) {
            ReportType.BUDGET_CATEGORY -> {
                text_budget_category.show()
                text_category.hide()
                text_merchant.hide()
                text_tag.hide()
            }
            ReportType.MERCHANT -> {
                text_budget_category.hide()
                text_category.hide()
                text_merchant.show()
                text_tag.hide()
            }
            ReportType.TRANSACTION_CATEGORY -> {
                text_budget_category.hide()
                text_category.show()
                text_merchant.hide()
                text_tag.hide()
            }
            ReportType.TAG -> {
                text_budget_category.hide()
                text_category.hide()
                text_merchant.hide()
                text_tag.show()
            }
        }
    }

    private fun showReportGrouping() {
        val intent = Intent(this, TransactionsReportActivity::class.java)

        with(intent) {
            putExtra(ReportConstants.ARG_REPORT_TYPE, selectedReportType)
            putExtra(ReportConstants.ARG_REPORT_PERIOD, selectedReportPeriod)
            selectedGrouping?.let { putExtra(ReportConstants.ARG_REPORT_GROUPING, it) }

            when (selectedReportType) {
                ReportType.BUDGET_CATEGORY -> {
                    selectedBudgetCategoryId?.let { putExtra(ReportConstants.ARG_FILTER_ID, it) }
                }
                ReportType.TRANSACTION_CATEGORY -> {
                    selectedTransactionCategoryId?.let { putExtra(ReportConstants.ARG_FILTER_ID, it) }
                }
                ReportType.MERCHANT -> {
                    selectedMerchantId?.let { putExtra(ReportConstants.ARG_FILTER_ID, it) }
                }
                ReportType.TAG -> {
                    selectedTag?.let { putExtra(ReportConstants.ARG_FILTER_TAG, it) }
                }
            }
        }

        startActivity(intent)
    }

    override val resourceId: Int
        get() = R.layout.activity_report_types
}
