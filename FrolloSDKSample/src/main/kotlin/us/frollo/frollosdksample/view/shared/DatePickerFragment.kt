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

package us.frollo.frollosdksample.view.shared

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import org.threeten.bp.LocalDate
import us.frollo.frollosdksample.R
import us.frollo.frollosdksample.utils.toDate
import java.util.Calendar
import java.util.Date

class DatePickerFragment(
    private val listener: CustomOnDateSetListener,
    private val dateTag: String? = null,
    private val localDate: LocalDate? = null,
    private val minDate: LocalDate? = null
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val today = Date()
        val date = localDate?.toDate() ?: Date()

        val c = Calendar.getInstance()
        c.time = if (date.compareTo(today) >= 0) date else today

        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        // Create a new instance of DatePickerDialog and return it
        val dialog = DatePickerDialog(requireContext(), R.style.DatePickerTheme, adaptListener(listener), year, month, day)
        minDate?.let { dialog.datePicker.minDate = it.toDate().time }
        dateTag?.let { dialog.datePicker.tag = it }
        return dialog
    }

    private fun adaptListener(customListener: CustomOnDateSetListener): DatePickerDialog.OnDateSetListener {
        return DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            customListener.onDateSet(view, LocalDate.of(year, month + 1, dayOfMonth))
        }
    }

    interface CustomOnDateSetListener {
        fun onDateSet(view: DatePicker, date: LocalDate)
    }
}
