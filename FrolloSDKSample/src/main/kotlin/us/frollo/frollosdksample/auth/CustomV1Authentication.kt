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

package us.frollo.frollosdksample.auth

import android.app.Application
import android.content.Context
import android.os.Build
import android.provider.Settings
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import us.frollo.frollosdk.FrolloSDK
import us.frollo.frollosdk.authentication.AccessToken
import us.frollo.frollosdk.authentication.AccessTokenProvider
import us.frollo.frollosdk.authentication.AuthenticationCallback
import us.frollo.frollosdk.base.Result
import us.frollo.frollosdk.core.OnFrolloSDKCompletionListener
import us.frollo.frollosdk.error.FrolloSDKError
import us.frollo.frollosdksample.BuildConfig
import java.io.IOException

class CustomV1Authentication(private val app: Application, private val baseUrl: String) : AccessTokenProvider, AuthenticationCallback {

    override var accessToken: AccessToken?
        get() = preferences.accessToken?.let { AccessToken(it) } ?: run { null }
        set(value) { preferences.accessToken = value?.token }

    private val preferences = Preferences(app)

    var loggedIn: Boolean
        get() = preferences.loggedIn
        private set(value) { preferences.loggedIn = value }

    fun loginUser(email: String, password: String, completion: OnFrolloSDKCompletionListener<Result>) {
        val apiService = CustomNetworkService().create(baseUrl, LoginApi::class.java)
        val request = CustomLoginRequest(email = email, password = password)
        apiService.loginUser(request).enqueue(object : Callback<Token> {
            override fun onResponse(call: Call<Token>, response: Response<Token>) {
                if (response.isSuccessful) {
                    val token = response.body()?.accessToken
                    val expiry = response.body()?.accessTokenExp
                    if (token != null && expiry != null) {
                        loggedIn = true

                        accessToken = AccessToken(token, expiry)

                        completion.invoke(Result.success())
                    } else {
                        val error = FrolloSDKError("Data Did Not contain an access token")
                        completion.invoke(Result.error(error))
                    }
                } else {
                    val error = FrolloSDKError(response.errorBody()?.string())
                    completion.invoke(Result.error(error))
                }
            }

            override fun onFailure(call: Call<Token>, t: Throwable) {
                val error = FrolloSDKError(t.message)
                completion.invoke(Result.error(error))
            }
        })
    }

    override fun accessTokenExpired() {
        // Only one access token is retrieved - reset if we try to renew the token
        reset() // Note: Example app will not show logout screen. Just kill the app and launch again.
    }

    override fun tokenInvalidated() {
        reset() // Note: Example app will not show logout screen. Just kill the app and launch again.
    }

    fun logout() {
        reset()
    }

    private fun reset() {
        FrolloSDK.reset()
        loggedIn = false
        preferences.reset()
    }

    private inner class CustomNetworkService {
        private fun createRetrofit(baseUrl: String): Retrofit {
            val gson = GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .enableComplexMapKeySerialization()
                .create()

            val httpClient = OkHttpClient.Builder()
                .addInterceptor(CustomInterceptor())
                .build()

            val builder = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient)

            return builder.build()
        }

        fun <T> create(baseUrl: String, service: Class<T>): T =
            createRetrofit(baseUrl).create(service)
    }

    private inner class CustomInterceptor : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
            val request = chain.request()
            val builder = request.newBuilder()
            addRequestUserAgentHeader(builder)
            return chain.proceed(builder.build())
        }

        private fun addRequestUserAgentHeader(builder: Request.Builder) {
            builder.addHeader(
                "User-Agent",
                "${BuildConfig.APPLICATION_ID}|V${BuildConfig.VERSION_NAME}|B${BuildConfig.VERSION_CODE}|Android${Build.VERSION.RELEASE}|API1.17"
            )
        }
    }

    private interface LoginApi {
        @POST("user/login")
        fun loginUser(@Body request: CustomLoginRequest): Call<Token>
    }

    private inner class CustomLoginRequest(
        @SerializedName("email") val email: String,
        @SerializedName("password") val password: String,
        @SerializedName("device_id") var deviceId: String? = null,
        @SerializedName("device_name") var deviceName: String? = null,
        @SerializedName("device_type") var deviceType: String? = null,
        @SerializedName("auth_type") val authType: String = "email"
    ) {
        init {
            deviceId = Settings.Secure.getString(app.contentResolver, Settings.Secure.ANDROID_ID)
            deviceName = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N)
                Settings.System.getString(app.contentResolver, Settings.Global.DEVICE_NAME) ?: Build.MODEL
            else
                Build.MODEL
            deviceType = if (Build.MODEL.startsWith(Build.MANUFACTURER)) {
                Build.MODEL
            } else {
                Build.MANUFACTURER + " " + Build.MODEL
            }
        }
    }

    private data class Token(
        @SerializedName("refresh_token") val refreshToken: String?,
        @SerializedName("access_token") val accessToken: String?,
        @SerializedName("access_token_exp") val accessTokenExp: Long?
    )

    class Preferences(context: Context) {
        companion object {
            private const val PREFERENCES = "pref_frollosdkexample"
            private const val KEY_LOGGED_IN = "key_frollosdkexample_user_logged_in"
            private const val KEY_ACCESS_TOKEN = "key_frollosdkexample_access_token"
        }

        private val preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)

        /** User Logged In */
        internal var loggedIn: Boolean
            get() = preferences.getBoolean(KEY_LOGGED_IN, false)
            set(value) = preferences.edit().putBoolean(KEY_LOGGED_IN, value).apply()

        /** Encrypted Access Token */
        internal var accessToken: String?
            get() = preferences.getString(KEY_ACCESS_TOKEN, null)
            set(value) = preferences.edit().putString(KEY_ACCESS_TOKEN, value).apply()

        internal fun reset() {
            preferences.edit().clear().apply()
        }
    }
}
