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

package us.frollo.frollosdksample.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import us.frollo.frollosdk.FrolloSDK
import us.frollo.frollosdk.base.Resource
import us.frollo.frollosdk.model.coredata.aggregation.tags.TransactionTag

class TagViewModel : ViewModel() {
    private var userTagsSource: LiveData<Resource<List<TransactionTag>>>? = null
    var userTagsLiveData = MediatorLiveData<Resource<List<TransactionTag>>?>()
    val suggestedTagsLiveData = MutableLiveData<List<TransactionTag>?>()

    fun fetchUserTags(searchTerm: String? = null) {
        removeUserTagSource()
        userTagsSource = FrolloSDK.aggregation.fetchTransactionUserTags(searchTerm)
            .apply { userTagsLiveData.addSource(this) { userTagsLiveData.value = it } }
    }

    fun fetchSuggestedTags(searchTerm: String? = null) {
        FrolloSDK.aggregation.fetchTransactionSuggestedTags(searchTerm) { resource ->
            if (resource.status == Resource.Status.SUCCESS) {
                suggestedTagsLiveData.value = resource.data
            }
        }
    }

    fun removeUserTagSource() {
        userTagsSource?.let { userTagsLiveData.removeSource(it) }
        userTagsLiveData.value = null
    }

    fun removeSuggestedTagSource() {
        suggestedTagsLiveData.value = null
    }

    fun refreshUserTags() = FrolloSDK.aggregation.refreshTransactionUserTags()
}
