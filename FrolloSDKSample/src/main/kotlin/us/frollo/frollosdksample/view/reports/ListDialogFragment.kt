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

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import us.frollo.frollosdk.FrolloSDK
import us.frollo.frollosdk.base.Resource
import us.frollo.frollosdk.model.coredata.reports.ReportGrouping
import us.frollo.frollosdk.model.coredata.shared.BudgetCategory
import us.frollo.frollosdksample.R
import us.frollo.frollosdksample.base.ARGUMENT
import us.frollo.frollosdksample.display.GroupModel
import us.frollo.frollosdksample.mapping.toGroupModel
import us.frollo.frollosdksample.utils.displayError
import us.frollo.frollosdksample.utils.observe
import us.frollo.frollosdksample.view.reports.adapters.ReportGroupsAdapter
import us.frollo.frollosdksample.viewmodel.TagViewModel

class ListDialogFragment : DialogFragment() {

    companion object {
        fun getInstance(type: ListType): ListDialogFragment {
            val fragment = ListDialogFragment()
            fragment.arguments = bundleOf(ARGUMENT.ARG_DATA_1 to type)
            return fragment
        }
    }

    private lateinit var type: ListType
    private val listAdapter = ReportGroupsAdapter(defaultSort = false)
    private lateinit var listener: OnDialogItemSelectedListener
    private lateinit var tagViewModel: TagViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        type = arguments?.getSerializable(ARGUMENT.ARG_DATA_1) as? ListType ?: ListType.TRANSACTION_CATEGORIES
        tagViewModel = ViewModelProviders.of(this).get(TagViewModel::class.java)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val recyclerView = RecyclerView(requireContext())
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
            adapter = listAdapter.apply {
                onItemClick { model, _, _ ->
                    model?.let { listener.onDialogItemSelected(type, it) }
                    dismiss()
                }
            }
        }

        val builder = AlertDialog.Builder(requireContext())
        with(builder) {
            setView(recyclerView)
            setTitle(type.title)
        }

        return builder.create()
    }

    override fun onResume() {
        super.onResume()

        when (type) {
            ListType.TRANSACTION_CATEGORIES -> fetchTransactionCategories()
            ListType.MERCHANTS -> fetchMerchants()
            ListType.BUDGET_CATEGORIES -> fetchBudgetCategories()
            ListType.TAGS -> fetchTags()
            ListType.GROUPING -> fetchGroupings()
        }
    }

    private fun fetchTransactionCategories() {
        FrolloSDK.aggregation.fetchTransactionCategories().observe(this) {
            when (it?.status) {
                Resource.Status.SUCCESS -> it.data?.let { categories ->
                    loadData(categories.map { category -> category.toGroupModel() }.toMutableList())
                }
                Resource.Status.ERROR -> displayError(it.error?.localizedDescription, "Fetch Transaction Categories Failed")
            }
        }
    }

    private fun fetchMerchants() {
        FrolloSDK.aggregation.fetchMerchants().observe(this) {
            when (it?.status) {
                Resource.Status.SUCCESS -> it.data?.let { merchants ->
                    loadData(merchants.map { merchant -> merchant.toGroupModel() }.toMutableList()) }
                Resource.Status.ERROR -> displayError(it.error?.localizedDescription, "Fetch Merchants Failed")
            }
        }
    }

    private fun fetchBudgetCategories() {
        loadData(BudgetCategory.values().map { it.toGroupModel() }.toMutableList())
    }

    private fun fetchTags() {
        tagViewModel.userTagsLiveData.observe(this) {
            when (it?.status) {
                Resource.Status.SUCCESS -> it.data?.let { tags ->
                    loadData(tags.map { tag -> tag.toGroupModel() }.toMutableList()) }
                Resource.Status.ERROR -> displayError(it.error?.localizedDescription, "Fetch Tags Failed")
            }
        }
        tagViewModel.fetchUserTags()
        tagViewModel.refreshUserTags()
    }

    private fun fetchGroupings() {
        loadData(ReportGrouping.values().map { it.toGroupModel() }.toMutableList())
    }

    private fun loadData(models: MutableList<GroupModel>) {
        models.sortBy { it.name }
        val list = mutableListOf(GroupModel(-1, getString(R.string.str_default_or_all)))
        list.addAll(models)
        listAdapter.replaceAll(list)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is OnDialogItemSelectedListener) {
            listener = context
        }
    }

    interface OnDialogItemSelectedListener {
        fun onDialogItemSelected(listType: ListType, model: GroupModel)
    }

    enum class ListType(@StringRes val title: Int) {
        BUDGET_CATEGORIES(R.string.str_report_type_budget_categories),
        TRANSACTION_CATEGORIES(R.string.str_report_type_transaction_categories),
        MERCHANTS(R.string.str_report_type_merchants),
        TAGS(R.string.str_report_type_tags),
        GROUPING(R.string.str_report_type_grouping)
    }
}