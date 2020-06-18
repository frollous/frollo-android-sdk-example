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

package us.frollo.frollosdksample.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.toolbar.*
import us.frollo.frollosdksample.SampleApplication
import us.frollo.frollosdksample.utils.hide
import us.frollo.frollosdksample.utils.isVisible
import us.frollo.frollosdksample.utils.show

abstract class BaseActivity : AppCompatActivity() {

    val app: SampleApplication
        get() = application as SampleApplication

    var toolbarTitle: String
        get() = toolbar_title.text.toString()
        set(value) { toolbar_title.text = value }

    var toolbarTitleEnabled: Boolean
        get() = toolbar_title.isVisible()
        set(value) { if (value) toolbar_title.show() else toolbar_title.hide() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(resourceId)

        initToolbar()
    }

    private fun initToolbar() {
        // Initialize the toolbar as the support action bar, if available on the layout
        toolbar?.let { setSupportActionBar(it) }
    }

    protected abstract val resourceId: Int
}
