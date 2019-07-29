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

package us.frollo.frollosdksample.view.goals

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.DatePicker
import kotlinx.android.synthetic.main.activity_add_goal.endDate
import kotlinx.android.synthetic.main.activity_add_goal.frequency
import kotlinx.android.synthetic.main.activity_add_goal.progress_bar_layout
import kotlinx.android.synthetic.main.activity_add_goal.sectionAccount
import kotlinx.android.synthetic.main.activity_add_goal.sectionEndDate
import kotlinx.android.synthetic.main.activity_add_goal.sectionFrequency
import kotlinx.android.synthetic.main.activity_add_goal.sectionStartDate
import kotlinx.android.synthetic.main.activity_add_goal.sectionTrackingType
import kotlinx.android.synthetic.main.activity_add_goal.startDate
import kotlinx.android.synthetic.main.activity_add_goal.trackingType
import org.jetbrains.anko.selector
import org.threeten.bp.LocalDate
import us.frollo.frollosdk.model.coredata.goals.Goal
import us.frollo.frollosdk.model.coredata.goals.GoalFrequency
import us.frollo.frollosdk.model.coredata.goals.GoalTarget
import us.frollo.frollosdk.model.coredata.goals.GoalTrackingType
import us.frollo.frollosdksample.R
import us.frollo.frollosdksample.base.ARGUMENT.ARG_DATA_1
import us.frollo.frollosdksample.base.BaseStackActivity
import us.frollo.frollosdksample.extension.toDisplay
import us.frollo.frollosdksample.utils.changeDateFormat
import us.frollo.frollosdksample.utils.show
import us.frollo.frollosdksample.utils.toLocalDate
import us.frollo.frollosdksample.utils.toString
import us.frollo.frollosdksample.view.shared.DatePickerFragment

class AddGoalActivity : BaseStackActivity(), DatePickerFragment.CustomOnDateSetListener {

    companion object {
        private const val TAG = "AddGoalActivity"
        private const val DATE_DISPLAY_FORMAT = "M/d/yy"
    }

    private lateinit var goalTarget: GoalTarget
    private var goalTrackingType = GoalTrackingType.CREDIT
    private var goalFrequency = GoalFrequency.WEEKLY
    private var goalStartDate = LocalDate.now().toString(Goal.DATE_FORMAT_PATTERN)
    private var goalEndDate = LocalDate.now().toString(Goal.DATE_FORMAT_PATTERN)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        goalTarget = intent.getSerializableExtra(ARG_DATA_1) as GoalTarget

        initView()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.create_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_create -> {
                createGoal()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initView() {
        trackingType.text = trackingLabel(goalTrackingType)
        frequency.text = goalFrequency.toDisplay()
        startDate.text = goalStartDate.changeDateFormat(Goal.DATE_FORMAT_PATTERN, DATE_DISPLAY_FORMAT)
        endDate.text = goalEndDate.changeDateFormat(Goal.DATE_FORMAT_PATTERN, DATE_DISPLAY_FORMAT)

        sectionAccount.setOnClickListener { }
        sectionTrackingType.setOnClickListener { pickTrackingType() }
        sectionFrequency.setOnClickListener { pickFrequency() }
        sectionStartDate.setOnClickListener {
            val date = startDate.text.toString().toLocalDate(DATE_DISPLAY_FORMAT)
            showDatePickerDialog(date, "startDate")
        }
        sectionEndDate.setOnClickListener {
            val date = endDate.text.toString().toLocalDate(DATE_DISPLAY_FORMAT)
            showDatePickerDialog(date, "endDate")
        }
    }

    private fun pickTrackingType() {
        val values = GoalTrackingType.values()

        selector("Tracking Method", values.map { trackingLabel(it) }) { _, index ->
            goalTrackingType = values[index]
            trackingType.text = trackingLabel(goalTrackingType)
        }
    }

    private fun pickFrequency() {
        val values = GoalFrequency.values()

        selector("Frequency", values.map { it.toDisplay() }) { _, index ->
            goalFrequency = values[index]
            frequency.text = goalFrequency.toDisplay()
        }
    }

    private fun showDatePickerDialog(date: LocalDate, dateTag: String) {
        DatePickerFragment(this, dateTag, date).show(supportFragmentManager, "datePicker")
    }

    override fun onDateSet(view: DatePicker, date: LocalDate) {
        if (view.tag == "startDate") {
            goalStartDate = date.toString(Goal.DATE_FORMAT_PATTERN)
            startDate.text = date.toString(DATE_DISPLAY_FORMAT)
        } else if (view.tag == "endDate") {
            goalEndDate = date.toString(Goal.DATE_FORMAT_PATTERN)
            endDate.text = date.toString(DATE_DISPLAY_FORMAT)
        }
    }

    private fun createGoal() {
        progress_bar_layout.show()

        /*FrolloSDK.goals.createGoal() { result ->
            progress_bar_layout.hide()

            when (result.status) {
                Result.Status.SUCCESS -> {}
                Result.Status.ERROR -> displayError(result.error?.localizedDescription, "Create Goal Failed")
            }
        }*/
    }

    private fun trackingLabel(type: GoalTrackingType): String {
        return when (type) {
            GoalTrackingType.CREDIT -> "Credit"
            GoalTrackingType.DEBIT -> "Debit"
            GoalTrackingType.DEBIT_CREDIT -> "Debit & Credit"
        }
    }

    override val resourceId: Int
        get() = R.layout.activity_add_goal
}
