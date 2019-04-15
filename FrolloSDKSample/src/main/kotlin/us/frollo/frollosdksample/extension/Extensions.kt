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

package us.frollo.frollosdksample.extension

import us.frollo.frollosdk.model.coredata.bills.BillFrequency
import us.frollo.frollosdk.model.coredata.bills.BillPaymentStatus

fun BillFrequency.toDisplay(): String =
        when (this) {
            BillFrequency.ANNUALLY -> "Annually"
            BillFrequency.BIANNUALLY -> "Biannually"
            BillFrequency.FORTNIGHTLY -> "Fortnightly"
            BillFrequency.FOUR_WEEKLY -> "Four Weekly"
            BillFrequency.IRREGULAR -> "Irregular"
            BillFrequency.MONTHLY -> "Monthly"
            BillFrequency.QUARTERLY -> "Quarterly"
            BillFrequency.WEEKLY -> "Weekly"
            BillFrequency.UNKNOWN -> "Unknown"
        }

fun BillPaymentStatus.toDisplay(): String =
        when (this) {
            BillPaymentStatus.DUE -> "Due"
            BillPaymentStatus.FUTURE -> "Future"
            BillPaymentStatus.OVERDUE -> "Overdue"
            BillPaymentStatus.PAID -> "Paid"
        }