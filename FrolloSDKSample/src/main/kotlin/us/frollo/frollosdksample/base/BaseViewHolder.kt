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

package us.frollo.frollosdksample.base

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import us.frollo.frollosdksample.utils.ifNotNull

abstract class BaseViewHolder<in T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private var mClickCallback: (((View), (Int)) -> Unit)? = null
    private var mLongClickCallback: (((View), (Int)) -> Unit)? = null

    protected open fun bindClickHandlers() {
        itemView.setOnClickListener {
            invokeCallback(it)
        }
    }

    protected open fun bindLongClickHandlers() {
        itemView.setOnLongClickListener {
            invokeLongClickCallback(it)
            true
        }
    }

    protected fun invokeCallback(view: View) =
        ifNotNull(mClickCallback, view) { it, _ -> it.invoke(view, adapterPosition) }

    protected open fun invokeLongClickCallback(view: View) =
        ifNotNull(mLongClickCallback, view) { it, _ -> it.invoke(view, adapterPosition) }

    fun setOnClickListener(callback: (((View), (Int)) -> Unit)) {
        bindClickHandlers()
        this.mClickCallback = callback
    }

    fun setOnLongClickListener(callback: (((View), (Int)) -> Unit)) {
        bindLongClickHandlers()
        this.mLongClickCallback = callback
    }

    abstract fun bind(model: T)
    abstract fun recycle()
}
