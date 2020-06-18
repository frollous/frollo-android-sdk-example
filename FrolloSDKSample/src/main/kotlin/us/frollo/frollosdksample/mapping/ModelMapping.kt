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

package us.frollo.frollosdksample.mapping

import us.frollo.frollosdk.model.coredata.aggregation.merchants.Merchant
import us.frollo.frollosdk.model.coredata.aggregation.tags.TransactionTag
import us.frollo.frollosdk.model.coredata.aggregation.transactioncategories.TransactionCategory
import us.frollo.frollosdk.model.coredata.reports.GroupReport
import us.frollo.frollosdk.model.coredata.reports.ReportGrouping
import us.frollo.frollosdk.model.coredata.shared.BudgetCategory
import us.frollo.frollosdksample.display.GroupModel

fun GroupReport.toGroupModel() = GroupModel(linkedId, name)

fun TransactionCategory.toGroupModel() = GroupModel(id = transactionCategoryId, name = name)

fun BudgetCategory.toGroupModel() = GroupModel(id = budgetCategoryId, name = name)

fun Merchant.toGroupModel() = GroupModel(id = merchantId, name = name)

fun TransactionTag.toGroupModel() = GroupModel(id = 0, name = name)

fun ReportGrouping.toGroupModel() = GroupModel(id = ordinal.toLong(), name = toString())

fun String.toTransactionTag() = TransactionTag(name = this, count = null, lastUsedAt = null, createdAt = null)
