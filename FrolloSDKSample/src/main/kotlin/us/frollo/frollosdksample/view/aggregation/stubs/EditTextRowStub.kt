package us.frollo.frollosdksample.view.aggregation.stubs

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.*
import org.jetbrains.anko.*
import us.frollo.frollosdk.model.coredata.aggregation.providers.ProviderFieldOption
import us.frollo.frollosdk.model.coredata.aggregation.providers.ProviderFieldType

class EditTextRowStub(private val item: FieldItem, private val parent: ViewGroup) : RowStub() {
    override fun createView(ui: AnkoContext<Context>): View = with(ui) {
        verticalLayout {
            //TextView + EditText
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