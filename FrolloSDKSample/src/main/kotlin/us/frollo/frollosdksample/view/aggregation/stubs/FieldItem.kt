package us.frollo.frollosdksample.view.aggregation.stubs

import us.frollo.frollosdk.model.coredata.aggregation.providers.ProviderFormRow

data class FieldItem(
        val type: FieldType,
        val rows: MutableList<ProviderFormRow>
)