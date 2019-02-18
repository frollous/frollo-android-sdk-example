package us.frollo.frollosdk.extensions

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.os.bundleOf
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import us.frollo.frollosdk.FrolloSDK
import us.frollo.frollosdk.auth.AuthenticationStatus
import us.frollo.frollosdk.core.ARGUMENT.ARG_AUTHENTICATION_STATUS
import us.frollo.frollosdk.core.ARGUMENT.ARG_DATA

class UtilityExtensionAndroidTest {

    private val app = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as Application

    private var notifyFlag = false
    private var status = AuthenticationStatus.AUTHENTICATED

    @Before
    fun setup() {
        FrolloSDK.app = app
    }

    @Test
    fun testNotify() {
        val lbm = LocalBroadcastManager.getInstance(app)
        lbm.registerReceiver(receiver, IntentFilter("ACTION_NOTIFY"))

        notify("ACTION_NOTIFY", bundleOf(Pair(ARG_AUTHENTICATION_STATUS, AuthenticationStatus.LOGGED_OUT)))

        Thread.sleep(2000)

        assertTrue(notifyFlag)
        assertEquals(AuthenticationStatus.LOGGED_OUT, status)

        lbm.unregisterReceiver(receiver)
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            notifyFlag = true
            status = intent.getBundleExtra(ARG_DATA).getSerializable(ARG_AUTHENTICATION_STATUS) as AuthenticationStatus
        }
    }
}