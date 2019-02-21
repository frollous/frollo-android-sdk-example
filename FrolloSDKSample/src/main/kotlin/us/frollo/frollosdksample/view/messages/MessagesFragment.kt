package us.frollo.frollosdksample.view.messages

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_messages.*
import androidx.recyclerview.widget.DividerItemDecoration
import org.jetbrains.anko.support.v4.onRefresh
import us.frollo.frollosdk.FrolloSDK
import us.frollo.frollosdk.base.Resource
import us.frollo.frollosdk.base.Result
import us.frollo.frollosdk.model.coredata.messages.ContentType
import us.frollo.frollosdk.model.coredata.messages.Message
import us.frollo.frollosdk.model.coredata.messages.MessageText
import us.frollo.frollosdksample.R
import us.frollo.frollosdksample.base.BaseFragment
import us.frollo.frollosdksample.utils.displayError
import us.frollo.frollosdksample.utils.observe
import us.frollo.frollosdksample.view.messages.adapters.MessagesAdapter

class MessagesFragment : BaseFragment() {

    companion object {
        private const val TAG = "MessagesFragment"
    }

    private val messagesAdapter = MessagesAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_messages, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        actionBar?.title = getString(R.string.title_messages)
        initView()
        initLiveData()
        refresh_layout.onRefresh { refreshUnreadMessages() }
    }

    private fun initView() {
        recycler_messages.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
            adapter = messagesAdapter
        }
    }

    private fun initLiveData() {
        viewLifecycleOwner?.let { owner ->
            FrolloSDK.messages.fetchMessages(read = false).observe(owner) {
                when (it?.status) {
                    Resource.Status.SUCCESS -> loadMessages(it.data)
                    Resource.Status.ERROR -> displayError(it.error?.localizedDescription, "Fetch Messages Failed")
                }
            }
        }
    }

    private fun loadMessages(messages: List<Message>?) {
        val textMessages = messages?.filter { it.contentType == ContentType.TEXT }?.map { it as MessageText }?.toList()
        textMessages?.let { messagesAdapter.replaceAll(textMessages) }
    }

    private fun refreshUnreadMessages() {
        FrolloSDK.messages.refreshUnreadMessages { result ->
            refresh_layout.isRefreshing = false

            when (result.status) {
                Result.Status.SUCCESS -> Log.d(TAG, "Messages Refreshed")
                Result.Status.ERROR -> displayError(result.error?.localizedDescription, "Refreshing Messages Failed")
            }
        }
    }
}
