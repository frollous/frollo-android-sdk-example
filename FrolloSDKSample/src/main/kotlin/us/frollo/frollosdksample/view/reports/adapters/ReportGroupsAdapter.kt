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
import kotlinx.android.synthetic.main.template_simple_item1.view.*
import org.apache.commons.lang3.StringUtils.capitalize
import us.frollo.frollosdksample.R
import us.frollo.frollosdksample.base.BaseRecyclerAdapter
import us.frollo.frollosdksample.base.BaseViewHolder
import us.frollo.frollosdksample.display.GroupModel

class ReportGroupsAdapter(defaultSort: Boolean = true) : BaseRecyclerAdapter<GroupModel,
    ReportGroupsAdapter.GroupModelViewHolder>(GroupModel::class.java, if (defaultSort) groupsComparator else null) {

    companion object {
        private val groupsComparator = compareBy<GroupModel> { it.name }
    }

    override fun getViewHolderLayout(viewType: Int) =
        R.layout.template_simple_item1

    override fun getViewHolder(view: View, viewType: Int) =
        GroupModelViewHolder(view)

    inner class GroupModelViewHolder(itemView: View) : BaseViewHolder<GroupModel>(itemView) {

        override fun bind(model: GroupModel) {
            itemView.text_title.text = capitalize(model.name)
        }

        override fun recycle() {
            itemView.text_title.text = null
        }
    }
}
