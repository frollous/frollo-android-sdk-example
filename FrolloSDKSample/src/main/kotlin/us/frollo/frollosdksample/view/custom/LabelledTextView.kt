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

package us.frollo.frollosdksample.view.custom

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.template_labelled_text_view.view.text_label
import kotlinx.android.synthetic.main.template_labelled_text_view.view.text_value
import us.frollo.frollosdksample.R

class LabelledTextView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    init {
        inflate(getContext(), R.layout.template_labelled_text_view, this)
        val attrArray = context.theme.obtainStyledAttributes(attrs, R.styleable.LabelledTextView, 0, 0)
        text_label.text = attrArray.getString(R.styleable.LabelledTextView_label)
    }

    fun setText(text: String) {
        text_value.text = text
    }
}