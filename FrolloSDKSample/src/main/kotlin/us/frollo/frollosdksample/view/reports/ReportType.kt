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

package us.frollo.frollosdksample.view.reports

import androidx.annotation.StringRes
import us.frollo.frollosdksample.R

enum class ReportType(@StringRes val textResource: Int) {
    BUDGET_CATEGORY(R.string.str_report_type_budget_category),
    TRANSACTION_CATEGORY(R.string.str_report_type_transaction_category),
    MERCHANT(R.string.str_report_type_merchant),
    TAG(R.string.str_report_type_tag);
}