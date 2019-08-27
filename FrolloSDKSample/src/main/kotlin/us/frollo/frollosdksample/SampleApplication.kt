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

package us.frollo.frollosdksample

import android.app.Application
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.jakewharton.threetenabp.AndroidThreeTen
import us.frollo.frollosdk.FrolloSDK
import us.frollo.frollosdksample.managers.SetupManager

class SampleApplication : Application(), LifecycleObserver {

    var setupManager: SetupManager? = null

    override fun onCreate() {
        super.onCreate()

        AndroidThreeTen.init(this)

        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

        setupManager = SetupManager()
        setupManager?.setupFrolloSDK(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {

        FrolloSDK.onAppBackgrounded()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {

        FrolloSDK.onAppForegrounded()
    }
}
