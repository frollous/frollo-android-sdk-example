package us.frollo.frollosdksample.view.aggregation

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_add_provider_account.*
import kotlinx.android.synthetic.main.progress_bar_full_screen.*
import org.jetbrains.anko.UI
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import us.frollo.frollosdk.FrolloSDK
import us.frollo.frollosdk.base.Resource
import us.frollo.frollosdk.base.Result
import us.frollo.frollosdk.model.coredata.aggregation.providers.Provider
import us.frollo.frollosdk.model.coredata.aggregation.providers.ProviderLoginForm
import us.frollo.frollosdksample.base.ARGUMENT
import us.frollo.frollosdksample.R
import us.frollo.frollosdksample.base.BaseStackActivity
import us.frollo.frollosdksample.utils.displayError
import us.frollo.frollosdksample.utils.hide
import us.frollo.frollosdksample.utils.show
import us.frollo.frollosdksample.view.aggregation.stubs.*

class AddProviderAccountActivity : BaseStackActivity() {

    companion object {
        private const val TAG = "AddProviderAccount"
    }

    private var providerId: Long = -1
    private val stubs = mutableListOf<RowStub>()
    private lateinit var providerLiveData: LiveData<Resource<Provider>>
    private var menuDone: MenuItem? = null
    private lateinit var provider: Provider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_add_provider_account)

        providerId = intent.getLongExtra(ARGUMENT.ARG_DATA_1, -1)

        initLiveData()

        refreshProvider()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.add_account_menu, menu)
        menuDone = menu?.findItem(R.id.menu_done)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_done -> {
                donePress()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initLiveData() {
        providerLiveData = FrolloSDK.aggregation.fetchProvider(providerId)
        providerLiveData.observe(this, observer)
    }

    private fun refreshProvider() {
        FrolloSDK.aggregation.refreshProvider(providerId) { result ->
            when (result.status) {
                Result.Status.SUCCESS -> Log.d(TAG, "Provider Refreshed")
                Result.Status.ERROR -> displayError(result.error?.localizedDescription, "Refreshing Provider Failed")
            }
        }
    }

    private val observer = Observer<Resource<Provider>> {
        when (it?.status) {
            Resource.Status.SUCCESS -> it.data?.let { provider -> loadProviderForm(provider) }
            Resource.Status.ERROR -> displayError(it.error?.localizedDescription, "Fetch Provider Failed")
        }
    }

    private fun loadProviderForm(model: Provider) {
        text_title.text = model.providerName

        provider = model

        val loginForm = provider.loginForm

        if (loginForm != null) {
            providerLiveData.removeObserver(observer)

            doAsync {
                val rows = linkedMapOf<String, FieldItem>()

                loginForm.rows.forEach {
                    if (it.fieldRowChoice.contains("Choice", true)) {
                        if (rows.containsKey(it.fieldRowChoice))
                            rows[it.fieldRowChoice]?.rows?.add(it)
                        else
                            rows[it.fieldRowChoice] = FieldItem(FieldType.CHOICE, mutableListOf(it))
                    } else {
                        rows[it.fieldRowChoice] = FieldItem(FieldType.TEXT, mutableListOf(it))
                    }
                }

                rows.forEach { (_, value) ->
                    when (value.type) {
                        FieldType.CHOICE -> stubs.add(OptionsRowStub(value, form_container))
                        FieldType.TEXT -> stubs.add(EditTextRowStub(value, form_container))
                    }
                }

                uiThread {
                    stubs.forEach {
                        form_container.addView(it.createView(UI { }))
                    }
                }
            }
        }
    }

    private fun donePress() {
        stubs.forEach { it.load() }

        provider.loginForm?.let { loginForm ->
            loginForm.validateForm { valid, error ->
                if (valid) {
                    shouldEncrypt(loginForm)
                    addAccount(loginForm)
                } else {
                    displayError(error?.localizedDescription, "Invalid Details")
                }
            }
        }
    }

    private fun shouldEncrypt(loginForm: ProviderLoginForm) {
        provider.encryption?.let { encryption ->
            val alias = encryption.alias
            val pem = encryption.pem

            if (loginForm.shouldEncrypt(encryption.encryptionType) && alias != null && pem != null) {
                loginForm.encryptValues(encryptionAlias = alias, encryptionKey = pem)
            }
        }
    }

    private fun addAccount(loginForm: ProviderLoginForm) {
        menuDone?.isEnabled = false
        text_progress_title.text = getString(R.string.str_adding_account)
        progress_bar.show()

        FrolloSDK.aggregation.createProviderAccount(providerId = providerId, loginForm = loginForm) { result ->
            progress_bar.hide()

            when (result.status) {
                Result.Status.SUCCESS -> {
                    toast("Account Added!")
                    setResult(Activity.RESULT_OK)
                    finish()
                }
                Result.Status.ERROR -> {
                    menuDone?.isEnabled = true
                    displayError(result.error?.localizedDescription, "Adding Account Failed")
                }
            }
        }
    }
}
