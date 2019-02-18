package us.frollo.frollosdk.data.local.dao

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.platform.app.InstrumentationRegistry
import com.jakewharton.threetenabp.AndroidThreeTen
import com.jraska.livedata.test
import org.junit.After
import org.junit.Assert.*
import org.junit.Before

import org.junit.Rule
import org.junit.Test
import us.frollo.frollosdk.data.local.SDKDatabase
import us.frollo.frollosdk.mapping.toAccount
import us.frollo.frollosdk.model.testAccountResponseData

class AccountDaoTest {

    @get:Rule val testRule = InstantTaskExecutorRule()

    private val app = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as Application
    private val db = SDKDatabase.getInstance(app)

    @Before
    fun setUp() {
        AndroidThreeTen.init(app)
    }

    @After
    fun tearDown() {
        db.clearAllTables()
    }

    @Test
    fun testLoadAll() {
        val data1 = testAccountResponseData(accountId = 100)
        val data2 = testAccountResponseData(accountId = 101)
        val data3 = testAccountResponseData(accountId = 102)
        val data4 = testAccountResponseData(accountId = 103)
        val list = mutableListOf(data1, data2, data3, data4)

        db.accounts().insertAll(*list.map { it.toAccount() }.toList().toTypedArray())

        val testObserver = db.accounts().load().test()
        testObserver.awaitValue()
        assertTrue(testObserver.value().isNotEmpty())
        assertEquals(4, testObserver.value().size)
    }

    @Test
    fun testLoadByAccountId() {
        val data = testAccountResponseData(accountId = 102)
        val list = mutableListOf(testAccountResponseData(accountId = 101), data, testAccountResponseData(accountId = 103))
        db.accounts().insertAll(*list.map { it.toAccount() }.toList().toTypedArray())

        val testObserver = db.accounts().load(data.accountId).test()
        testObserver.awaitValue()
        assertNotNull(testObserver.value())
        assertEquals(data.accountId, testObserver.value()?.accountId)
    }

    @Test
    fun testLoadByProviderAccountId() {
        val data1 = testAccountResponseData(providerAccountId = 1)
        val data2 = testAccountResponseData(providerAccountId = 2)
        val data3 = testAccountResponseData(providerAccountId = 1)
        val data4 = testAccountResponseData(providerAccountId = 1)
        val list = mutableListOf(data1, data2, data3, data4)
        db.accounts().insertAll(*list.map { it.toAccount() }.toList().toTypedArray())

        val testObserver = db.accounts().loadByProviderAccountId(providerAccountId = 1).test()
        testObserver.awaitValue()
        assertNotNull(testObserver.value())
        assertEquals(3, testObserver.value().size)
    }

    @Test
    fun testInsertAll() {
        val data1 = testAccountResponseData(accountId = 100)
        val data2 = testAccountResponseData(accountId = 101)
        val data3 = testAccountResponseData(accountId = 102)
        val data4 = testAccountResponseData(accountId = 103)
        val list = mutableListOf(data1, data2, data3, data4)

        db.accounts().insertAll(*list.map { it.toAccount() }.toList().toTypedArray())

        val testObserver = db.accounts().load().test()
        testObserver.awaitValue()
        assertTrue(testObserver.value().isNotEmpty())
        assertEquals(4, testObserver.value().size)
    }

    @Test
    fun testInsert() {
        val data = testAccountResponseData()

        db.accounts().insert(data.toAccount())

        val testObserver = db.accounts().load().test()
        testObserver.awaitValue()
        assertTrue(testObserver.value().isNotEmpty())
        assertEquals(data.accountId, testObserver.value()[0].accountId)
    }

    @Test
    fun testGetStaleIds() {
        val data1 = testAccountResponseData(accountId = 100)
        val data2 = testAccountResponseData(accountId = 101)
        val data3 = testAccountResponseData(accountId = 102)
        val data4 = testAccountResponseData(accountId = 103)
        val list = mutableListOf(data1, data2, data3, data4)

        db.accounts().insertAll(*list.map { it.toAccount() }.toList().toTypedArray())

        val staleIds = db.accounts().getStaleIds(longArrayOf(100, 103)).sorted()
        assertEquals(2, staleIds.size)
        assertTrue(staleIds.containsAll(mutableListOf<Long>(101, 102)))
    }

    @Test
    fun testDeleteMany() {
        val data1 = testAccountResponseData(accountId = 100)
        val data2 = testAccountResponseData(accountId = 101)
        val data3 = testAccountResponseData(accountId = 102)
        val data4 = testAccountResponseData(accountId = 103)
        val list = mutableListOf(data1, data2, data3, data4)

        db.accounts().insertAll(*list.map { it.toAccount() }.toList().toTypedArray())

        db.accounts().deleteMany(longArrayOf(100, 103))

        val testObserver = db.accounts().load().test()
        testObserver.awaitValue()
        assertTrue(testObserver.value().isNotEmpty())
        assertEquals(2, testObserver.value().size)
    }

    @Test
    fun testDelete() {
        val data1 = testAccountResponseData(accountId = 100)
        val data2 = testAccountResponseData(accountId = 101)
        val data3 = testAccountResponseData(accountId = 102)
        val data4 = testAccountResponseData(accountId = 103)
        val list = mutableListOf(data1, data2, data3, data4)

        db.accounts().insertAll(*list.map { it.toAccount() }.toList().toTypedArray())

        db.accounts().delete(100)

        val testObserver = db.accounts().load().test()
        testObserver.awaitValue()
        assertTrue(testObserver.value().isNotEmpty())
        assertEquals(3, testObserver.value().size)
    }

    @Test
    fun testDeleteByProviderAccountId() {
        val data1 = testAccountResponseData(providerAccountId = 1)
        val data2 = testAccountResponseData(providerAccountId = 2)
        val data3 = testAccountResponseData(providerAccountId = 2)
        val data4 = testAccountResponseData(providerAccountId = 1)
        val list = mutableListOf(data1, data2, data3, data4)

        db.accounts().insertAll(*list.map { it.toAccount() }.toList().toTypedArray())

        db.accounts().deleteByProviderAccountId(1)

        val testObserver = db.accounts().load().test()
        testObserver.awaitValue()
        assertTrue(testObserver.value().isNotEmpty())
        assertEquals(2, testObserver.value().size)
    }

    @Test
    fun testClear() {
        val data1 = testAccountResponseData(accountId = 100)
        val data2 = testAccountResponseData(accountId = 101)
        val data3 = testAccountResponseData(accountId = 102)
        val data4 = testAccountResponseData(accountId = 103)
        val list = mutableListOf(data1, data2, data3, data4)

        db.accounts().insertAll(*list.map { it.toAccount() }.toList().toTypedArray())

        db.accounts().clear()

        val testObserver = db.accounts().load().test()
        testObserver.awaitValue()
        assertTrue(testObserver.value().isEmpty())
    }
}