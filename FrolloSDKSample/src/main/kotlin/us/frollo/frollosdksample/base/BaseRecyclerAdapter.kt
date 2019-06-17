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
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import us.frollo.frollosdk.model.IAdapterModel
import us.frollo.frollosdksample.utils.inflate

abstract class BaseRecyclerAdapter<T : IAdapterModel, VH : BaseViewHolder<T>>(klass: Class<T>, private val comparator: Comparator<T>? = null) : RecyclerView.Adapter<VH>() {
    protected var mData = mutableListOf<T>()
    protected var mClickCallback: ((T, View, Int) -> Unit)? = null
    protected var mLongClickCallback: ((T, View, Int) -> Unit)? = null

    fun onItemClick(l: (model: T?, v: View?, p: Int) -> Unit) {
        this.mClickCallback = l
    }

    fun onItemLongClick(l: (model: T?, v: View?, p: Int) -> Unit) {
        mLongClickCallback = l
    }

    open fun replaceAll(data: List<T>) {
        mData = comparator?.let { data.sortedWith(comparator).toMutableList() } ?: run { data.toMutableList() }
        notifyDataSetChanged()
    }

    open fun clear() {
        mData.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val viewHolder: VH = getViewHolder(parent.inflate(getViewHolderLayout(viewType)), viewType)
        mClickCallback?.let { viewHolder.setOnClickListener(this::invokeClickCallback) }
        mLongClickCallback?.let { viewHolder.setOnLongClickListener(this::invokeLongClickCallback) }
        return viewHolder
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        mData[position].let { holder.bind(it) }
    }

    override fun onViewRecycled(holder: VH) {
        super.onViewRecycled(holder)
        holder.recycle()
    }

    fun getItemAt(position: Int): T? =
            if (position > -1 && position < mData.size) mData[position] else null

    override fun getItemCount(): Int = mData.size

    protected open fun invokeClickCallback(view: View, pos: Int) {
        if (pos < mData.size && pos >= 0) this.mClickCallback?.invoke(mData[pos], view, pos)
    }

    protected open fun invokeLongClickCallback(view: View, pos: Int) {
        if (pos < mData.size && pos >= 0) this.mLongClickCallback?.invoke(mData[pos], view, pos)
    }

    @LayoutRes
    protected abstract fun getViewHolderLayout(viewType: Int): Int
    protected abstract fun getViewHolder(view: View, viewType: Int): VH
}