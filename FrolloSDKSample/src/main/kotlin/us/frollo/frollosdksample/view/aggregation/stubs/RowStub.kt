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
import android.graphics.Typeface
import android.text.InputFilter
import android.text.InputType
import android.view.View
import android.view.ViewManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import org.jetbrains.anko.AnkoComponent
import org.jetbrains.anko.AnkoViewDslMarker
import org.jetbrains.anko._LinearLayout
import org.jetbrains.anko.custom.ankoView
import org.jetbrains.anko.dip
import org.jetbrains.anko.editText
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.padding
import org.jetbrains.anko.spinner
import org.jetbrains.anko.textAppearance
import org.jetbrains.anko.textColorResource
import org.jetbrains.anko.textView
import org.jetbrains.anko.verticalLayout
import us.frollo.frollosdk.model.coredata.aggregation.providers.ProviderFieldOption
import us.frollo.frollosdk.model.coredata.aggregation.providers.ProviderFieldType
import us.frollo.frollosdk.model.coredata.aggregation.providers.ProviderFormField
import us.frollo.frollosdk.model.coredata.aggregation.providers.ProviderFormRow
import us.frollo.frollosdksample.R
import java.util.WeakHashMap

abstract class RowStub : AnkoComponent<Context> {
    val viewIdCache: WeakHashMap<String, Int> = WeakHashMap()

    abstract fun load()

    protected fun @AnkoViewDslMarker _LinearLayout.spinnerFor(field: ProviderFormField, viewId: Int): Spinner =
        spinner {
            id = viewId
            adapter = ArrayAdapter<ProviderFieldOption>(this@spinnerFor.context, R.layout.template_spinner_item, field.options ?: listOf())
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                    field.options?.let { options ->
                        field.value = options[pos].optionValue
                    }
                }
            }
            setBackgroundResource(R.drawable.spinner_background)
        }.lparams {
            topMargin = dip(8)
            width = matchParent
            height = dip(52)
        }

    protected fun @AnkoViewDslMarker _LinearLayout.editTextFor(field: ProviderFormField, viewId: Int): EditText =
        editText {
            id = viewId
            padding = dip(10)
            setBackgroundResource(R.drawable.border_background)
            textAppearance = R.style.TextAppearance_AppCompat_Medium
            textColorResource = android.R.color.black
            maxLines = 1
            field.maxLength?.let { maxLength ->
                if (maxLength > 0) setMaxLength(maxLength)
            }
            inputType = when (field.type) {
                ProviderFieldType.PASSWORD -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                else -> InputType.TYPE_CLASS_TEXT
            }
        }.lparams {
            topMargin = dip(8)
            width = matchParent
            height = dip(52)
        }

    protected fun @AnkoViewDslMarker _LinearLayout.textViewFor(model: ProviderFormRow): TextView =
        textViewFor(model.label)

    protected fun @AnkoViewDslMarker _LinearLayout.textViewFor(textStr: String): TextView =
        textView {
            textAppearance = R.style.TextAppearance_AppCompat_Medium
            typeface = Typeface.DEFAULT_BOLD
            text = textStr
        }.lparams {
            topMargin = dip(8)
            marginStart = dip(4)
        }

    protected inline fun ViewManager.horizontalLayout(theme: Int = 0, init: (@AnkoViewDslMarker _LinearLayout).() -> Unit): LinearLayout =
        this.verticalLayout(theme, init).apply { orientation = LinearLayout.HORIZONTAL }
}

fun EditText.setMaxLength(maxLength: Int) {
    filters = if (filters == null) {
        arrayOf(InputFilter.LengthFilter(maxLength))
    } else {
        // This is used in order not to override other filters
        filters.filter { it !is InputFilter.LengthFilter }
            .toMutableList()
            .apply { add(InputFilter.LengthFilter(maxLength)) }
            .toTypedArray()
    }
}

inline fun ViewManager.textInputEditText() = textInputEditText {}
inline fun ViewManager.textInputEditText(theme: Int = 0, init: (@AnkoViewDslMarker TextInputEditText).() -> Unit) = ankoView({ TextInputEditText(it) }, theme, init)

inline fun ViewManager.textInputLayout() = textInputLayout {}
inline fun ViewManager.textInputLayout(theme: Int = 0, init: (@AnkoViewDslMarker TextInputLayout).() -> Unit) = ankoView({ TextInputLayout(it) }, theme, init)
