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
import androidx.annotation.IdRes
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

abstract class BaseFragment : ViewLifecycleFragment() {

    protected val activity: AppCompatActivity?
        get() = super.getActivity() as? AppCompatActivity

    protected val actionBar: ActionBar?
        get() = activity?.supportActionBar

    protected fun start(fragment: Fragment, @IdRes containerViewId: Int, backStack: Boolean = false, args: Bundle? = null) {
        fragment.arguments = args

        requireActivity().supportFragmentManager.beginTransaction().apply {
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            replace(containerViewId, fragment)
            if (backStack) addToBackStack(fragment.javaClass.name)
        }.commit()
    }

    protected fun navigateBack(tag: String? = null) {
        requireActivity().supportFragmentManager.popBackStack(tag, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }
}
