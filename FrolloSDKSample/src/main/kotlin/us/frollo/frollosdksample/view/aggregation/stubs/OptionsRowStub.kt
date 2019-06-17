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
import org.jetbrains.anko.dip
import org.jetbrains.anko.find
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.radioButton
import org.jetbrains.anko.radioGroup
import org.jetbrains.anko.textAppearance
import org.jetbrains.anko.verticalLayout
import us.frollo.frollosdk.model.coredata.aggregation.providers.ProviderFieldOption
import us.frollo.frollosdk.model.coredata.aggregation.providers.ProviderFieldType
import us.frollo.frollosdksample.R
import us.frollo.frollosdksample.utils.hide
import us.frollo.frollosdksample.utils.show

class OptionsRowStub(val item: FieldItem, private val parent: ViewGroup) : RowStub() {
    override fun createView(ui: AnkoContext<Context>): View = with(ui) {
        verticalLayout {

            textViewFor("Choose One")

            radioGroup {
                // Show radio buttons and on click show item of that particular row, based on tag assigned to option of that row.
                item.rows.forEachIndexed { rowIndex, row ->
                    radioButton {
                        text = row.label
                        textAppearance = R.style.TextAppearance_AppCompat_Medium
                        setOnClickListener {
                            // Hide all options
                            item.rows.forEachIndexed { i, row ->
                                this@verticalLayout.findViewWithTag<View>(row.fieldRowChoice + i)?.hide()
                            }
                            // Show this option only
                            this@verticalLayout.findViewWithTag<View>(row.fieldRowChoice + rowIndex)?.show()
                        }
                    }.lparams {
                        topMargin = dip(8)
                    }
                }
                // Check the first item
                check(getChildAt(0).id)
            }.lparams {
                topMargin = dip(8)
                bottomMargin = dip(8)
            }

            item.rows.forEachIndexed { rowIndex, row ->
                verticalLayout {
                    // Use tags to show/hide this layouts later on
                    tag = row.fieldRowChoice + rowIndex

                    textViewFor(row)

                    horizontalLayout {
                        val lastIndex = row.fields.lastIndex
                        row.fields.forEachIndexed { fieldIndex, field ->
                            val viewId = View.generateViewId()
                            viewIdCache[field.fieldId] = viewId

                            if (field.type == ProviderFieldType.OPTION)
                                spinnerFor(field, viewId)
                                        .lparams {
                                            weight = 1f
                                            topMargin = dip(8)
                                            width = matchParent
                                            height = dip(52)
                                        }
                            else
                                editTextFor(field, viewId)
                                        .lparams {
                                            weight = 1f
                                            topMargin = dip(8)
                                            if (fieldIndex < lastIndex) marginEnd = dip(10)
                                            width = matchParent
                                            height = dip(52)
                                        }
                        }
                    }

                    // Hack: don't hide the first option (default one)
                    if (rowIndex > 0) hide()
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