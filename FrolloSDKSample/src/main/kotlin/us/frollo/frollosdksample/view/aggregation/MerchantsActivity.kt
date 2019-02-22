package us.frollo.frollosdksample.view.aggregation

import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_merchants.*
import us.frollo.frollosdk.FrolloSDK
import us.frollo.frollosdk.base.Resource
import us.frollo.frollosdk.model.coredata.aggregation.merchants.Merchant
import us.frollo.frollosdksample.R
import us.frollo.frollosdksample.base.BaseStackActivity
import us.frollo.frollosdksample.utils.displayError
import us.frollo.frollosdksample.utils.observe
import us.frollo.frollosdksample.view.aggregation.adapters.MerchantsAdapter

class MerchantsActivity : BaseStackActivity() {

    companion object {
        private const val TAG = "MerchantsActivity"
    }

    private val merchantsAdapter = MerchantsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_merchants)

        initView()
        initLiveData()
    }

    private fun initView() {
        recycler_merchants.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(this@MerchantsActivity, LinearLayoutManager.VERTICAL))
            adapter = merchantsAdapter
        }
    }

    private fun initLiveData() {
        FrolloSDK.aggregation.fetchMerchants().observe(this) {
            when (it?.status) {
                Resource.Status.SUCCESS -> it.data?.let { merchants -> loadMerchants(merchants) }
                Resource.Status.ERROR -> displayError(it.error?.localizedDescription, "Fetch Merchants Failed")
            }
        }
    }

    private fun loadMerchants(merchants: List<Merchant>) {
        merchantsAdapter.replaceAll(merchants)
    }
}
