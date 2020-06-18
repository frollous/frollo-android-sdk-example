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

package us.frollo.frollosdksample.view.messages.adapters

import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.template_message_item.view.*
import us.frollo.frollosdk.model.coredata.messages.Message
import us.frollo.frollosdk.model.coredata.messages.MessageText
import us.frollo.frollosdksample.R
import us.frollo.frollosdksample.base.BaseRecyclerAdapter
import us.frollo.frollosdksample.base.BaseViewHolder
import us.frollo.frollosdksample.utils.hide
import us.frollo.frollosdksample.utils.show

class MessagesAdapter : BaseRecyclerAdapter<Message, MessagesAdapter.MessageTextViewHolder>(Message::class.java, messageComparator) {

    companion object {
        private val messageComparator = compareByDescending<Message> { it.placement }
    }

    override fun getViewHolderLayout(viewType: Int) =
        R.layout.template_message_item

    override fun getViewHolder(view: View, viewType: Int) =
        MessageTextViewHolder(view)

    inner class MessageTextViewHolder(itemView: View) : BaseViewHolder<Message>(itemView) {

        override fun bind(model: Message) {
            val message = model as? MessageText
            loadText(itemView.text_header, message?.header)
            loadText(itemView.text_title, message?.title)
            loadText(itemView.text_body, message?.text)
            loadText(itemView.text_footer, message?.footer)
            loadText(itemView.text_action, message?.action?.title)
        }

        override fun recycle() {
            itemView.text_title.text = null
        }

        private fun loadText(tv: TextView, text: String?) {
            text?.let {
                tv.show()
                tv.text = it
            } ?: run {
                tv.hide()
            }
        }
    }
}
