package us.frollo.frollosdk.model.coredata.notifications

internal data class NotificationPayload(
        val event: String? = null,
        val link: String? = null,
        val transactionIDs: List<Long>? = null,
        val userEventID: Long? = null,
        val userMessageID: Long? = null
)