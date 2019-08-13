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

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.DatePicker
import kotlinx.android.synthetic.main.activity_add_goal.account
import kotlinx.android.synthetic.main.activity_add_goal.description
import kotlinx.android.synthetic.main.activity_add_goal.endDate
import kotlinx.android.synthetic.main.activity_add_goal.frequency
import kotlinx.android.synthetic.main.activity_add_goal.name
import kotlinx.android.synthetic.main.activity_add_goal.periodAmount
import kotlinx.android.synthetic.main.activity_add_goal.progress_bar_layout
import kotlinx.android.synthetic.main.activity_add_goal.sectionAccount
import kotlinx.android.synthetic.main.activity_add_goal.sectionEndDate
import kotlinx.android.synthetic.main.activity_add_goal.sectionFrequency
import kotlinx.android.synthetic.main.activity_add_goal.sectionPeriodAmount
import kotlinx.android.synthetic.main.activity_add_goal.sectionStartDate
import kotlinx.android.synthetic.main.activity_add_goal.sectionTargetAmount
import kotlinx.android.synthetic.main.activity_add_goal.sectionTrackingType
import kotlinx.android.synthetic.main.activity_add_goal.startDate
import kotlinx.android.synthetic.main.activity_add_goal.targetAmount
import kotlinx.android.synthetic.main.activity_add_goal.trackingType
import org.jetbrains.anko.selector
import org.jetbrains.anko.startActivityForResult
import org.threeten.bp.LocalDate
import us.frollo.frollosdk.FrolloSDK
import us.frollo.frollosdk.base.Result
import us.frollo.frollosdk.model.coredata.goals.Goal
import us.frollo.frollosdk.model.coredata.goals.GoalFrequency
import us.frollo.frollosdk.model.coredata.goals.GoalTarget
import us.frollo.frollosdk.model.coredata.goals.GoalTrackingType
import us.frollo.frollosdksample.R
import us.frollo.frollosdksample.base.ARGUMENT.ARG_DATA_1
import us.frollo.frollosdksample.base.ARGUMENT.ARG_DATA_2
import us.frollo.frollosdksample.base.BaseStackActivity
import us.frollo.frollosdksample.base.REQUEST.REQUEST_SELECTION
import us.frollo.frollosdksample.extension.getGoalEndDate
import us.frollo.frollosdksample.extension.getMessage
import us.frollo.frollosdksample.extension.toDisplay
import us.frollo.frollosdksample.utils.displayError
import us.frollo.frollosdksample.utils.hide
import us.frollo.frollosdksample.utils.show
import us.frollo.frollosdksample.utils.toString
import us.frollo.frollosdksample.view.shared.DatePickerFragment
import java.math.BigDecimal

class AddGoalActivity : BaseStackActivity(), DatePickerFragment.CustomOnDateSetListener {

    companion object {
        private const val TAG = "AddGoalActivity"
        private const val DATE_DISPLAY_FORMAT = "M/d/yy"
    }

    private lateinit var goalTarget: GoalTarget
    private var goalTrackingType = GoalTrackingType.CREDIT
    private var goalFrequency = GoalFrequency.WEEKLY
    private var goalStartDate = LocalDate.now()
    private var goalEndDate: LocalDate? = null
    private var goalTargetAmount: BigDecimal? = null
    private var goalPeriodAmount: BigDecimal? = null
    private var goalAccountId: Long = -1
    private var goalAccountName: String = ""
    private var goalName: String = ""
    private var goalDescription: String? = null

    private var minEndDate: LocalDate? = null

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
        when (goalTarget) {
            GoalTarget.AMOUNT -> {
                supportActionBar?.title = "Add Amount Goal"
                sectionEndDate.hide()
            }
            GoalTarget.DATE -> {
                supportActionBar?.title = "Add Date Goal"
                sectionPeriodAmount.hide()
                updateMinimumEndDate()
            }
            GoalTarget.OPEN_ENDED -> {
                supportActionBar?.title = "Add Open Ended Goal"
                sectionTargetAmount.hide()
                updateMinimumEndDate()
            }
        }

        trackingType.text = trackingLabel(goalTrackingType)
        frequency.text = goalFrequency.toDisplay()
        startDate.text = goalStartDate.toString(DATE_DISPLAY_FORMAT)
        endDate.text = goalEndDate?.toString(DATE_DISPLAY_FORMAT)

        sectionAccount.setOnClickListener { startActivityForResult<SelectAccountActivity>(REQUEST_SELECTION) }
        sectionTrackingType.setOnClickListener { pickTrackingType() }
        sectionFrequency.setOnClickListener { pickFrequency() }
        sectionStartDate.setOnClickListener {
            showDatePickerDialog(goalStartDate, DateMode.START_DATE)
        }
        sectionEndDate.setOnClickListener {
            goalEndDate?.let { date ->
                showDatePickerDialog(date, DateMode.END_DATE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_SELECTION && resultCode == RESULT_OK && data != null) {
            goalAccountId = data.getLongExtra(ARG_DATA_1, -1)
            goalAccountName = data.getStringExtra(ARG_DATA_2)
            account.text = goalAccountName
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
            updateMinimumEndDate()
        }
    }

    private fun showDatePickerDialog(date: LocalDate, dateMode: DateMode) {
        val datePicker = when (dateMode) {
            DateMode.START_DATE -> DatePickerFragment(this, dateMode.name, date, LocalDate.now())
            DateMode.END_DATE -> DatePickerFragment(this, dateMode.name, date, minEndDate)
        }
        datePicker.show(supportFragmentManager, "datePicker")
    }

    override fun onDateSet(view: DatePicker, date: LocalDate) {
        if (view.tag == DateMode.START_DATE.name) {
            goalStartDate = date
            startDate.text = date.toString(DATE_DISPLAY_FORMAT)
            updateMinimumEndDate()
        } else if (view.tag == DateMode.END_DATE.name) {
            goalEndDate = date
            endDate.text = date.toString(DATE_DISPLAY_FORMAT)
        }
    }

    private fun updateMinimumEndDate() {
        if (goalTarget == GoalTarget.DATE || goalTarget == GoalTarget.OPEN_ENDED) {
            minEndDate = goalStartDate.getGoalEndDate(goalFrequency)
            goalEndDate = minEndDate
            endDate.text = minEndDate?.toString(DATE_DISPLAY_FORMAT)
        }
    }

    private fun createGoal() {
        goalName = name.text.toString()
        if (goalName.isBlank()) {
            displayError("Missing goal name", "Create Goal Failed")
            return
        }

        if (goalAccountId == -1L) {
            displayError("Missing goal account", "Create Goal Failed")
            return
        }

        when (goalTarget) {
            GoalTarget.AMOUNT -> {
                if (!validateTargetAmount()) {
                    return
                }
                if (!validatePeriodAmount()) {
                    return
                }
            }
            GoalTarget.DATE -> {
                if (!validateEndDate()) {
                    return
                }
                if (!validateTargetAmount()) {
                    return
                }
            }
            GoalTarget.OPEN_ENDED -> {
                if (!validateEndDate()) {
                    return
                }
                if (!validatePeriodAmount()) {
                    return
                }
            }
        }

        goalDescription = if (description.text.toString().isNotBlank()) description.text.toString() else null

        progress_bar_layout.show()

        FrolloSDK.goals.createGoal(
                name = goalName,
                description = goalDescription,
                target = goalTarget,
                trackingType = goalTrackingType,
                frequency = goalFrequency,
                startDate = goalStartDate.toString(Goal.DATE_FORMAT_PATTERN),
                endDate = goalEndDate?.toString(Goal.DATE_FORMAT_PATTERN),
                periodAmount = goalPeriodAmount,
                targetAmount = goalTargetAmount,
                accountId = goalAccountId
        ) { result ->
            progress_bar_layout.hide()

            when (result.status) {
                Result.Status.SUCCESS -> { finish() }
                Result.Status.ERROR -> displayError(result.error?.getMessage(), "Create Goal Failed")
            }
        }
    }

    private fun validateTargetAmount(): Boolean {
        val amount = targetAmount.text.toString()
        return if (amount.isNotBlank() && !amount.startsWith(".")) {
            goalTargetAmount = BigDecimal(amount)
            true
        } else {
            goalTargetAmount = null
            displayError("Missing target amount", "Create Goal Failed")
            false
        }
    }

    private fun validatePeriodAmount(): Boolean {
        val amount = periodAmount.text.toString()
        return if (amount.isNotBlank() && !amount.startsWith(".")) {
            goalPeriodAmount = BigDecimal(amount)
            true
        } else {
            goalPeriodAmount = null
            displayError("Missing period amount", "Create Goal Failed")
            false
        }
    }

    private fun validateEndDate(): Boolean {
        return if (goalEndDate != null) {
            true
        } else {
            displayError("Missing end date", "Create Goal Failed")
            false
        }
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

    private enum class DateMode {
        START_DATE, END_DATE
    }
}
