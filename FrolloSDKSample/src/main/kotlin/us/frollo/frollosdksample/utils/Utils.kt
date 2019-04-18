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

package us.frollo.frollosdksample.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.appcompat.app.ActionBar
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import org.jetbrains.anko.AlertBuilder
import org.jetbrains.anko.alert
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.DateTimeFormatterBuilder
import org.threeten.bp.temporal.ChronoField
import us.frollo.frollosdk.model.coredata.aggregation.accounts.Balance
import us.frollo.frollosdksample.R
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.*

fun <T1, T2> ifNotNull(value1: T1?, value2: T2?, bothNotNull: (T1, T2) -> (Unit)) {
    if (value1 != null && value2 != null) {
        bothNotNull(value1, value2)
    }
}

fun <T> LiveData<T>.observe(owner: LifecycleOwner, observer: (T?) -> Unit) =
        observe(owner, Observer<T> { v -> observer.invoke(v) })

fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToParent: Boolean = false): View =
        LayoutInflater.from(context).inflate(layoutRes, this, attachToParent)

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.hide(visibility: Int = View.GONE) {
    this.visibility = visibility
}

fun View.isVisible(): Boolean =
        visibility == View.VISIBLE

fun AlertBuilder<AlertDialog>.showThemed(): AlertDialog =
        show().setTheme()

fun AlertBuilder<DialogInterface>.showThemed(): DialogInterface =
        show().apply { (this as? AlertDialog)?.setTheme() }

fun DialogInterface.showThemed(): DialogInterface =
        apply { (this as? AlertDialog)?.setTheme() }

private fun AlertDialog.setTheme(): AlertDialog =
        apply {
            getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(ResourcesCompat.getColor(context.resources, R.color.colorPrimary, context.theme))
            getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(ResourcesCompat.getColor(context.resources, R.color.colorPrimary, context.theme))
            getButton(AlertDialog.BUTTON_NEUTRAL)?.setTextColor(ResourcesCompat.getColor(context.resources, R.color.colorPrimary, context.theme))
        }

fun Fragment.displayError(message: String?, title: String)
        = requireActivity().displayError(message, title)

fun Activity.displayError(message: String?, title: String, callback: (() -> Unit)? = null) {
    alert(message ?: "", title) {
        positiveButton("OK") {
            callback?.invoke()
        }
    }.showThemed()
}

fun Fragment.showListDialog(array: Array<String>, title: String? = null,
                            clickListener: DialogInterface.OnClickListener)
        = requireActivity().showListDialog(array, title, clickListener)

fun Activity.showListDialog(array: Array<String>, title: String? = null,
                            clickListener: DialogInterface.OnClickListener) =
        AlertDialog.Builder(this).apply {
            title?.let { setTitle(title) }
            setItems(array, clickListener)
            create()
            show()
        }

fun LocalDateTime.toString(pattern: String): String =
        DateTimeFormatter.ofPattern(pattern).format(this)

fun LocalDate.toString(pattern: String): String =
        DateTimeFormatter.ofPattern(pattern).format(this)

fun String.changeDateFormat(from: String, to: String): String {
    val sourceFormatter = DateTimeFormatterBuilder()
            .appendPattern(from)
            .parseDefaulting(ChronoField.DAY_OF_MONTH, 1)
            .toFormatter()
    val date = LocalDate.parse(this, sourceFormatter)
    return date.toString(to)
}

fun String.formatISOString(pattern: String): String =
        LocalDateTime.parse(this, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toString(pattern)

fun Number.toCurrencyString(currency: String, fractionDigits: Int = 2): String {
    val format = NumberFormat.getCurrencyInstance(Locale.getDefault())
    //Try setting the currency into the new format if possible. If not, go with Locale default
    try {
        format.currency = Currency.getInstance(currency)
    } catch (ex: Exception) {
        Log.e("Number.toCurrencyString", "Unable to format `$this` to currency")
    }
    format.maximumFractionDigits = fractionDigits
    return format.format(this)
}

val Balance?.display: String?
    get() {
        this?.let {
            return it.amount.toCurrencyString(it.currency, 2)
        }
        return null
    }

fun BigDecimal.display(currency: String): String? =
        this.toCurrencyString(currency, 2)

fun ActionBar.showBackNavigation(show: Boolean) {
    displayOptions = if (show) ActionBar.DISPLAY_SHOW_TITLE or ActionBar.DISPLAY_SHOW_HOME or ActionBar.DISPLAY_HOME_AS_UP
                     else ActionBar.DISPLAY_SHOW_TITLE
}