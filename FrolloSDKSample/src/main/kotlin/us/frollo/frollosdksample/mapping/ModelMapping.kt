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

import us.frollo.frollosdk.model.coredata.reports.ReportGroupTransactionHistoryRelation
import us.frollo.frollosdk.model.coredata.reports.ReportTransactionCurrentRelation
import us.frollo.frollosdksample.display.GroupModel
import us.frollo.frollosdksample.utils.ifNotNull

fun ReportTransactionCurrentRelation.toGroupModel() : GroupModel? {

    var model: GroupModel? = null

    ifNotNull(report?.linkedId, report?.name) { id, name ->
        model = GroupModel(id, name)
    }

    return model
}

fun ReportGroupTransactionHistoryRelation.toGroupModel() : GroupModel? {

    var model: GroupModel? = null

    ifNotNull(groupReport?.linkedId, groupReport?.name) { id, name ->
        model = GroupModel(id, name)
    }

    return model
}