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

package us.frollo.frollosdksample.managers

import android.app.Application
import android.util.Log
import us.frollo.frollosdk.FrolloSDK
import us.frollo.frollosdk.authentication.Authentication
import us.frollo.frollosdk.authentication.AuthenticationType.Custom
import us.frollo.frollosdk.authentication.AuthenticationType.OAuth2
import us.frollo.frollosdk.base.Result
import us.frollo.frollosdk.core.FrolloSDKConfiguration
import us.frollo.frollosdk.logging.LogLevel
import us.frollo.frollosdksample.auth.CustomV1Authentication
import us.frollo.frollosdksample.view.authentication.Host

object SetupManager {

    private const val TAG = "SetupManager"
    var customAuthentication: Authentication? = null
    var useV1Auth = false

    /**
     * This is to choose Auth0 or Frollo as authorization server
     *
     * NOTE: Every time you change this make sure to clear App Data in Settings for the change to take effect
     */
    private val host = Host.FROLLO_V2

    fun setup(application: Application, callback: () -> Unit) {
        val configuration = when (host) {
            Host.FROLLO_V2 -> {
                useV1Auth = false
                FrolloSDKConfiguration(
                        authenticationType = OAuth2(
                                redirectUrl = "frollo-sdk-example://authorize",
                                authorizationUrl = "https://id-sandbox.frollo.us/oauth/authorize/",
                                tokenUrl = "https://id-sandbox.frollo.us/oauth/token/",
                                revokeTokenURL = "https://id-sandbox.frollo.us/oauth/revoke/"),
                        clientId = "243ffc404803ee5a567d93e1f2dd322a0df911557a5283dd3dd7ebed3258ddeb",
                        serverUrl = "https://api-sandbox.frollo.us/api/v2/",
                        logLevel = LogLevel.DEBUG)
            }
            Host.FROLLO_AUTH0 -> {
                useV1Auth = false
                FrolloSDKConfiguration(
                        authenticationType = OAuth2(
                                redirectUrl = "frollo-sdk-example://authorize",
                                authorizationUrl = "https://frollo-test.au.auth0.com/authorize/",
                                tokenUrl = "https://frollo-test.au.auth0.com/oauth/token/",
                                revokeTokenURL = "https://frollo-test.au.auth0.com/oauth/revoke/"),
                        clientId = "PzlborkOwZf42SJ2b6Fdj6JTi9lcqiNi",
                        serverUrl = "https://volt-sandbox.frollo.us/api/v2/",
                        logLevel = LogLevel.DEBUG)
            }
            Host.FROLLO_V1 -> {
                useV1Auth = true
                val authentication = CustomV1Authentication(
                        app = application,
                        baseUrl = "https://api-sandbox.frollo.us/api/v1/")
                customAuthentication = authentication
                FrolloSDKConfiguration(
                        authenticationType = Custom(authentication = authentication),
                        clientId = "243ffc404803ee5a567d93e1f2dd322a0df911557a5283dd3dd7ebed3258ddeb",
                        serverUrl = "https://api-sandbox.frollo.us/api/v2/",
                        logLevel = LogLevel.DEBUG)
            }
        }

        setupSdk(application, configuration, callback)
    }

    private fun setupSdk(application: Application, configuration: FrolloSDKConfiguration, callback: () -> Unit) {
        if (FrolloSDK.isSetup) {
            callback.invoke()
        } else {
            FrolloSDK.setup(application, configuration = configuration) { result ->
                when (result.status) {
                    Result.Status.SUCCESS -> callback.invoke()
                    Result.Status.ERROR -> Log.e(TAG, result.error?.localizedDescription)
                }
            }
        }
    }
}