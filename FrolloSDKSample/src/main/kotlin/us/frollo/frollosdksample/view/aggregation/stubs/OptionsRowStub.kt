package us.frollo.frollosdksample.view.aggregation.stubs

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.*
import org.jetbrains.anko.*
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
                item.rows.forEachIndexed { rowIndex, row ->
                    radioButton {
                        text = row.label
                        textAppearance = R.style.TextAppearance_AppCompat_Medium
                        setOnClickListener {
                            //Hide all options
                            item.rows.forEachIndexed { i, row ->
                                this@verticalLayout.findViewWithTag<View>(row.fieldRowChoice + i)?.hide()
                            }
                            //Show this option only
                            this@verticalLayout.findViewWithTag<View>(row.fieldRowChoice + rowIndex)?.show()
                        }
                    }.lparams {
                        topMargin = dip(8)
                    }
                }
                //Check the first item
                check(getChildAt(0).id)
            }.lparams {
                topMargin = dip(8)
                bottomMargin = dip(8)
            }

            item.rows.forEachIndexed { i, it ->
                verticalLayout {
                    //Use tags to show/hide this layouts later on
                    tag = it.fieldRowChoice + i

                    textViewFor(it)

                    horizontalLayout {
                        val lastIndex = it.fields.lastIndex
                        it.fields.forEachIndexed { index, field ->
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
                                            if (index < lastIndex) marginEnd = dip(10)
                                            width = matchParent
                                            height = dip(52)
                                        }
                        }
                    }

                    //Hack: don't hide the first option (default one)
                    if (i > 0) hide()
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