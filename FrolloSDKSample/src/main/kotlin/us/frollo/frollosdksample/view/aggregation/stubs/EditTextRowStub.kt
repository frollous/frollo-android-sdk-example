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

package us.frollo.frollosdksample.view.aggregation.stubs

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Spinner
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.find
import org.jetbrains.anko.verticalLayout
import us.frollo.frollosdk.model.coredata.aggregation.providers.ProviderFieldOption
import us.frollo.frollosdk.model.coredata.aggregation.providers.ProviderFieldType

class EditTextRowStub(private val item: FieldItem, private val parent: ViewGroup) : RowStub() {
    override fun createView(ui: AnkoContext<Context>): View = with(ui) {
        verticalLayout {
            // TextView + EditText
            item.rows.forEach {

                textViewFor(it)

                it.fields.forEach { field ->
                    val viewId = View.generateViewId()
                    viewIdCache[field.fieldId] = viewId

                    if (field.type == ProviderFieldType.OPTION)
                        spinnerFor(field, viewId)
                    else
                        editTextFor(field, viewId)
                }
            }
        }
    }

    override fun load() {
        item.rows.forEach { row ->
            row.fields.forEach { field ->
                val viewId = viewIdCache[field.fieldId]
                viewId?.let {
                    if (field.type == ProviderFieldType.OPTION)
                        field.value = (parent.find<Spinner>(it).selectedItem as ProviderFieldOption).optionValue
                    else
                        field.value = parent.find<EditText>(it).text.toString()
                }
            }
        }
    }
}
