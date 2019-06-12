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

package us.frollo.frollosdksample.view.aggregation

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_add_tag.btn_add
import kotlinx.android.synthetic.main.activity_add_tag.recycler_suggested_tags
import kotlinx.android.synthetic.main.activity_add_tag.recycler_user_tags
import kotlinx.android.synthetic.main.activity_add_tag.search_view
import kotlinx.android.synthetic.main.activity_add_tag.text_section_recent
import kotlinx.android.synthetic.main.activity_add_tag.text_section_suggested
import us.frollo.frollosdk.base.Resource
import us.frollo.frollosdk.model.coredata.aggregation.tags.TransactionTag
import us.frollo.frollosdksample.base.ARGUMENT
import us.frollo.frollosdksample.R
import us.frollo.frollosdksample.base.BaseStackActivity
import us.frollo.frollosdksample.utils.hide
import us.frollo.frollosdksample.utils.observe
import us.frollo.frollosdksample.utils.show
import us.frollo.frollosdksample.view.aggregation.adapters.TagsSearchAdapter
import us.frollo.frollosdksample.viewmodel.TagViewModel

class AddTagActivity : BaseStackActivity() {

    companion object {
        private const val TAG = "AddTagActivity"
    }

    private lateinit var tagViewModel: TagViewModel
    private val userTagsAdapter = TagsSearchAdapter()
    private val suggestedTagsAdapter = TagsSearchAdapter()
    private var countDownTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tagViewModel = ViewModelProviders.of(this).get(TagViewModel::class.java)
        initView()
        initLiveData()
    }

    private fun initView() {
        search_view?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean = true
            override fun onQueryTextChange(newText: String): Boolean {
                handleQueryChange(newText)
                return true
            }
        })

        search_view?.setOnClickListener {
            search_view?.isIconified = false
        }

        btn_add.setOnClickListener {
            search_view?.query?.let { text ->
                if (text.isNotBlank()) {
                    selectTag(text.toString())
                }
            }
        }

        recycler_user_tags.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
            adapter = userTagsAdapter.apply {
                onItemClick { model, _, _ ->
                    model?.let { selectTag(model.name) }
                }
            }
        }

        recycler_suggested_tags.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
            adapter = suggestedTagsAdapter.apply {
                onItemClick { model, _, _ ->
                    model?.let { selectTag(model.name) }
                }
            }
        }
    }

    private fun initLiveData() {
        tagViewModel.refreshUserTags()

        tagViewModel.userTagsLiveData.observe(this) {
            if (it?.status == Resource.Status.SUCCESS) {
                 it.data?.let(::loadUserTags)
            }
        }
        tagViewModel.fetchUserTags()

        tagViewModel.suggestedTagsLiveData.observe(this) {
            it?.let(::loadSuggestedTags)
        }
    }

    private fun loadUserTags(models: List<TransactionTag>) {
        if (models.isEmpty()) {
            showUserTagsList(false)
        } else {
            showUserTagsList(true)
            userTagsAdapter.replaceAll(models)
        }
    }

    private fun loadSuggestedTags(models: List<TransactionTag>) {
        if (models.isEmpty()) {
            showSuggestedTagsList(false)
        } else {
            showSuggestedTagsList(true)
            suggestedTagsAdapter.replaceAll(models)
        }
    }

    private fun handleQueryChange(newText: String) {
        countDownTimer?.cancel()

        if (newText.isBlank() || newText.length < 2) {
            showSuggestedTagsList(false)
            tagViewModel.fetchUserTags()
        } else {
            tagViewModel.fetchUserTags(newText)

            countDownTimer = object: CountDownTimer(2000, 1000) {
                override fun onTick(millisUntilFinished: Long) {}
                override fun onFinish() {
                    tagViewModel.fetchSuggestedTags(newText)
                }
            }.start()
        }
    }

    private fun selectTag(tagName: String) {
        val intent = Intent().apply {
            putExtra(ARGUMENT.ARG_DATA_1, tagName)
        }
        setResult(RESULT_OK, intent)
        finish()
    }

    private fun showSuggestedTagsList(show: Boolean) {
        if (show) {
            text_section_suggested.show()
            recycler_suggested_tags.show()
        } else {
            text_section_suggested.hide()
            recycler_suggested_tags.hide()
        }
    }

    private fun showUserTagsList(show: Boolean) {
        if (show) {
            text_section_recent.show()
            recycler_user_tags.show()
        } else {
            text_section_recent.hide()
            recycler_user_tags.hide()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        tagViewModel.removeUserTagSource()
        tagViewModel.removeSuggestedTagSource()
    }

    override val resourceId: Int
        get() = R.layout.activity_add_tag
}
