package us.frollo.frollosdk.messages

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.platform.app.InstrumentationRegistry
import com.jraska.livedata.test
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.Test

import org.junit.Assert.*
import org.junit.Rule
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneOffset
import us.frollo.frollosdk.FrolloSDK
import us.frollo.frollosdk.core.SetupParams
import us.frollo.frollosdk.data.local.SDKDatabase
import us.frollo.frollosdk.data.remote.NetworkHelper
import us.frollo.frollosdk.data.remote.NetworkService
import us.frollo.frollosdk.data.remote.api.MessagesAPI
import us.frollo.frollosdk.keystore.Keystore
import us.frollo.frollosdk.model.coredata.messages.*
import us.frollo.frollosdk.model.testMessageNotificationPayload
import us.frollo.frollosdk.model.testMessageResponseData
import us.frollo.frollosdk.preferences.Preferences
import us.frollo.frollosdk.test.R
import us.frollo.frollosdk.testutils.readStringFromJson
import us.frollo.frollosdk.testutils.wait

class MessagesTest {

    @get:Rule val testRule = InstantTaskExecutorRule()
    private val app = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as Application
    private lateinit var mockServer: MockWebServer
    private lateinit var preferences: Preferences
    private lateinit var keystore: Keystore
    private lateinit var database: SDKDatabase
    private lateinit var network: NetworkService

    private lateinit var messages: Messages

    private fun initSetup() {
        mockServer = MockWebServer()
        mockServer.start()
        val baseUrl = mockServer.url("/")

        if (!FrolloSDK.isSetup) FrolloSDK.setup(app, SetupParams.Builder().serverUrl(baseUrl.toString()).build()) {}

        keystore = Keystore()
        keystore.setup()
        preferences = Preferences(app)
        database = SDKDatabase.getInstance(app)
        network = NetworkService(baseUrl.toString(), keystore, preferences)

        preferences.encryptedAccessToken = keystore.encrypt("ExistingAccessToken")
        preferences.encryptedRefreshToken = keystore.encrypt("ExistingRefreshToken")
        preferences.accessTokenExpiry = LocalDateTime.now(ZoneOffset.UTC).toEpochSecond(ZoneOffset.UTC) + 900

        messages = Messages(network, database)
    }

    private fun tearDown() {
        mockServer.shutdown()
        preferences.resetAll()
        database.clearAllTables()
    }

    @Test
    fun testFetchMessageByID() {
        initSetup()

        val data = testMessageResponseData()
        val list = mutableListOf(testMessageResponseData(), data, testMessageResponseData())
        database.messages().insertAll(*list.toTypedArray())

        val testObserver = messages.fetchMessage(data.messageId).test()
        testObserver.awaitValue()
        assertNotNull(testObserver.value().data)
        assertEquals(data.messageId, testObserver.value().data?.messageId)

        tearDown()
    }

    @Test
    fun testFetchAllMessages() {
        initSetup()

        val data1 = testMessageResponseData(read = false)
        val data2 = testMessageResponseData(read = true)
        val data3 = testMessageResponseData(read = false)
        val data4 = testMessageResponseData(read = true)
        val list = mutableListOf(data1, data2, data3, data4)

        database.messages().insertAll(*list.toTypedArray())

        val testObserver = messages.fetchMessages().test()
        testObserver.awaitValue()
        assertNotNull(testObserver.value().data)
        assertEquals(4, testObserver.value().data?.size)

        tearDown()
    }

    @Test
    fun testFetchUnreadMessages() {
        initSetup()

        val data1 = testMessageResponseData(read = false)
        val data2 = testMessageResponseData(read = true)
        val data3 = testMessageResponseData(read = false)
        val data4 = testMessageResponseData(read = true)
        val list = mutableListOf(data1, data2, data3, data4)

        database.messages().insertAll(*list.toTypedArray())

        val testObserver = messages.fetchMessages(read = false).test()
        testObserver.awaitValue()
        assertNotNull(testObserver.value().data)
        assertEquals(2, testObserver.value().data?.size)

        tearDown()
    }

    @Test
    fun testFetchMessagesByMessageType() {
        initSetup()

        val data1 = testMessageResponseData(types = mutableListOf("survey"))
        val data2 = testMessageResponseData(types = mutableListOf("event"))
        val data3 = testMessageResponseData(types = mutableListOf("survey", "welcome"))
        val data4 = testMessageResponseData(types = mutableListOf("dashboard_survey"))
        val list = mutableListOf(data1, data2, data3, data4)

        database.messages().insertAll(*list.toTypedArray())

        val testObserver = messages.fetchMessages(messageTypes = mutableListOf("survey")).test()
        testObserver.awaitValue()
        assertNotNull(testObserver.value().data)
        assertEquals(2, testObserver.value().data?.size)

        tearDown()
    }

    @Test
    fun testRefreshMessages() {
        initSetup()

        val body = readStringFromJson(app, R.raw.messages_valid)
        mockServer.setDispatcher(object: Dispatcher() {
            override fun dispatch(request: RecordedRequest?): MockResponse {
                if (request?.path == MessagesAPI.URL_MESSAGES) {
                    return MockResponse()
                            .setResponseCode(200)
                            .setBody(body)
                }
                return MockResponse().setResponseCode(404)
            }
        })

        messages.refreshMessages { error ->
            assertNull(error)

            val testObserver = messages.fetchMessages().test()
            testObserver.awaitValue()
            val models = testObserver.value().data
            assertNotNull(models)
            assertEquals(39, models?.size)
            models?.forEach { message ->
                when (message.contentType) {
                    ContentType.HTML -> assertTrue(message is MessageHTML)
                    ContentType.VIDEO -> assertTrue(message is MessageVideo)
                    ContentType.IMAGE -> assertTrue(message is MessageImage)
                    ContentType.TEXT -> assertTrue(message is MessageText)
                }
            }
        }

        val request = mockServer.takeRequest()
        assertEquals(MessagesAPI.URL_MESSAGES, request.path)

        wait(3)

        tearDown()
    }

    @Test
    fun testRefreshMessagesSkipsInvalid() {
        initSetup()

        val body = readStringFromJson(app, R.raw.messages_invalid)
        mockServer.setDispatcher(object: Dispatcher() {
            override fun dispatch(request: RecordedRequest?): MockResponse {
                if (request?.path == MessagesAPI.URL_MESSAGES) {
                    return MockResponse()
                            .setResponseCode(200)
                            .setBody(body)
                }
                return MockResponse().setResponseCode(404)
            }
        })

        messages.refreshMessages { error ->
            assertNull(error)

            val testObserver = messages.fetchMessages().test()
            testObserver.awaitValue()
            val models = testObserver.value().data
            assertNotNull(models)
            assertEquals(35, models?.size)
            models?.forEach { message ->
                when (message.contentType) {
                    ContentType.HTML -> assertTrue(message is MessageHTML)
                    ContentType.VIDEO -> assertTrue(message is MessageVideo)
                    ContentType.IMAGE -> assertTrue(message is MessageImage)
                    ContentType.TEXT -> assertTrue(message is MessageText)
                }
            }
        }

        val request = mockServer.takeRequest()
        assertEquals(MessagesAPI.URL_MESSAGES, request.path)

        wait(3)

        tearDown()
    }

    @Test
    fun testRefreshMessageByID() {
        initSetup()

        val body = readStringFromJson(app, R.raw.message_id_12345)
        mockServer.setDispatcher(object: Dispatcher() {
            override fun dispatch(request: RecordedRequest?): MockResponse {
                if (request?.path == "${NetworkHelper.API_VERSION_PATH}/messages/12345") {
                    return MockResponse()
                            .setResponseCode(200)
                            .setBody(body)
                }
                return MockResponse().setResponseCode(404)
            }
        })

        messages.refreshMessage(12345L) { error ->
            assertNull(error)

            val testObserver = messages.fetchMessage(12345L).test()
            testObserver.awaitValue()
            val model = testObserver.value().data
            assertNotNull(model)
            assertEquals(12345L, model?.messageId)
        }

        val request = mockServer.takeRequest()
        assertEquals("${NetworkHelper.API_VERSION_PATH}/messages/12345", request.path)

        wait(3)

        tearDown()
    }

    @Test
    fun testRefreshUnreadMessages() {
        initSetup()

        val body = readStringFromJson(app, R.raw.messages_unread)
        mockServer.setDispatcher(object: Dispatcher() {
            override fun dispatch(request: RecordedRequest?): MockResponse {
                if (request?.path == MessagesAPI.URL_UNREAD) {
                    return MockResponse()
                            .setResponseCode(200)
                            .setBody(body)
                }
                return MockResponse().setResponseCode(404)
            }
        })

        messages.refreshUnreadMessages { error ->
            assertNull(error)

            val testObserver = messages.fetchMessages(read = false).test()
            testObserver.awaitValue()
            val models = testObserver.value().data
            assertNotNull(models)
            assertEquals(7, models?.size)
            models?.forEach { message ->
                when (message.contentType) {
                    ContentType.HTML -> assertTrue(message is MessageHTML)
                    ContentType.VIDEO -> assertTrue(message is MessageVideo)
                    ContentType.IMAGE -> assertTrue(message is MessageImage)
                    ContentType.TEXT -> assertTrue(message is MessageText)
                }
            }
        }

        val request = mockServer.takeRequest()
        assertEquals(MessagesAPI.URL_UNREAD, request.path)

        wait(3)

        tearDown()
    }

    @Test
    fun testUpdateMessage() {
        initSetup()

        val body = readStringFromJson(app, R.raw.message_id_12345)
        mockServer.setDispatcher(object: Dispatcher() {
            override fun dispatch(request: RecordedRequest?): MockResponse {
                if (request?.path == "${NetworkHelper.API_VERSION_PATH}/messages/12345") {
                    return MockResponse()
                            .setResponseCode(200)
                            .setBody(body)
                }
                return MockResponse().setResponseCode(404)
            }
        })

        messages.updateMessage(12345L, true, true) { error ->
            assertNull(error)

            val testObserver = messages.fetchMessage(12345L).test()
            testObserver.awaitValue()
            val model = testObserver.value().data
            assertNotNull(model)
            assertEquals(12345L, model?.messageId)
        }

        val request = mockServer.takeRequest()
        assertEquals("${NetworkHelper.API_VERSION_PATH}/messages/12345", request.path)

        wait(3)

        tearDown()
    }

    @Test
    fun testHandlePushMessage() {
        initSetup()

        val body = readStringFromJson(app, R.raw.message_id_12345)
        mockServer.setDispatcher(object: Dispatcher() {
            override fun dispatch(request: RecordedRequest?): MockResponse {
                if (request?.path == "${NetworkHelper.API_VERSION_PATH}/messages/12345") {
                    return MockResponse()
                            .setResponseCode(200)
                            .setBody(body)
                }
                return MockResponse().setResponseCode(404)
            }
        })

        messages.handleMessageNotification(testMessageNotificationPayload())

        wait(3)

        val request = mockServer.takeRequest()
        assertEquals("${NetworkHelper.API_VERSION_PATH}/messages/12345", request.path)

        val testObserver = messages.fetchMessage(12345L).test()
        testObserver.awaitValue()
        val model = testObserver.value().data
        assertNotNull(model)
        assertEquals(12345L, model?.messageId)

        tearDown()
    }
}