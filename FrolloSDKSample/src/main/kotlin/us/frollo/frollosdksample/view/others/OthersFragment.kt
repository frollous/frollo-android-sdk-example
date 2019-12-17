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

package us.frollo.frollosdksample.view.others

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_others.budgets
import kotlinx.android.synthetic.main.fragment_others.goals
import org.jetbrains.anko.support.v4.startActivity
import us.frollo.frollosdksample.R
import us.frollo.frollosdksample.base.BaseFragment
import us.frollo.frollosdksample.utils.showBackNavigation
import us.frollo.frollosdksample.view.budgets.BudgetsActivity
import us.frollo.frollosdksample.view.goals.GoalsActivity

class OthersFragment : BaseFragment() {

    companion object {
        private const val TAG = "GoalsFragment"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_others, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        goals.setOnClickListener {
            startActivity<GoalsActivity>()
        }
        budgets.setOnClickListener {
            startActivity<BudgetsActivity>()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        actionBar?.showBackNavigation(show = false)
        actionBar?.title = getString(R.string.title_others)
    }
}
