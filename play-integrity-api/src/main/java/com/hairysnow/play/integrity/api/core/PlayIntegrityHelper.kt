package com.hairysnow.play.integrity.api.core

import android.util.Log
import com.google.android.play.core.integrity.IntegrityManagerFactory
import com.google.android.play.core.integrity.IntegrityTokenRequest
import com.google.android.play.core.integrity.IntegrityTokenResponse
import com.google.android.play.core.integrity.model.IntegrityErrorCode
import com.hairysnow.play.integrity.api.core.configuration.IntegrityConfiguration
import com.hairysnow.play.integrity.api.core.exception.IntegrityException
import com.hairysnow.play.integrity.api.core.result.IntegrityResult
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.math.floor

/**
 * Description :
 *
 * @author Jam 2022/11/7
 */
class PlayIntegrityHelper(
    private val integrityConfiguration: IntegrityConfiguration
) {

    fun requestIntegrityToken(onIntegrityResultListener: OnIntegrityResultListener?) {
        // Create an instance of a manager.
        val integrityManager = IntegrityManagerFactory.create(integrityConfiguration.context)

        // Request the integrity token by providing a nonce.
        val integrityTokenResponse = integrityManager.requestIntegrityToken(
            IntegrityTokenRequest.builder()
                .setNonce(generateOneTimeRequestNonce())
                .build()
        )
        integrityTokenResponse.addOnSuccessListener { integrityTokenResponse1: IntegrityTokenResponse ->
            val integrityToken = integrityTokenResponse1.token()
            logE(String.format("integrityToken:%s", integrityToken))
            integrityToken?.let {
                requestBackendIntegrityValidation(it, onIntegrityResultListener)
            } ?: callbackFailure(Exception(), onIntegrityResultListener)
        }
        integrityTokenResponse.addOnFailureListener { e: Exception ->
            callbackFailure(e, onIntegrityResultListener)
        }
    }

    fun requestBackendIntegrityValidation(
        integrityToken: String,
        onIntegrityResultListener: OnIntegrityResultListener?
    ) {
        val params = HashMap<String, String>().apply {
            put(
                "applicationName", kotlin.runCatching {
                    integrityConfiguration.context.resources.getString(
                        integrityConfiguration.context.packageManager.getPackageInfo(
                            integrityConfiguration.context.packageName,
                            0
                        ).applicationInfo.labelRes
                    )
                }.getOrElse { "" }
            )
            put("packageName", integrityConfiguration.context.packageName)
            put("integrityToken", integrityToken)
        }
        integrityConfiguration.additionalParams?.map {
            params.put(it.key, it.value)
        }
        val requestBody = if (integrityConfiguration.isJsonType) {
            RequestBody.create("application/json; charset=utf-8".toMediaType(), JSONObject().apply {
                params.map {
                    put(it.key, it.value)
                }
            }.toString())
        } else {
            FormBody.Builder()
                .apply {
                    params.map {
                        add(it.key, it.value)
                    }
                }
                .build()
        }

        val requestBuilder = Request.Builder()
            .url(integrityConfiguration.backendUrl)
        integrityConfiguration.additionalHeaders?.map {
            requestBuilder.addHeader(it.key, it.value)
        }
        val request = requestBuilder
            .post(requestBody)
            .build()
        val okHttpClientBuilder = OkHttpClient.Builder()
            .connectTimeout(integrityConfiguration.timeout, TimeUnit.MILLISECONDS)
            .writeTimeout(integrityConfiguration.timeout, TimeUnit.MILLISECONDS)
            .readTimeout(integrityConfiguration.timeout, TimeUnit.MILLISECONDS)
            .apply {
                if (integrityConfiguration.loggable) {
                    addInterceptor(HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    })
                }
            }
        if (integrityConfiguration.sslSocketFactory != null && integrityConfiguration.trustManager != null) {
            okHttpClientBuilder.sslSocketFactory(
                integrityConfiguration.sslSocketFactory,
                integrityConfiguration.trustManager
            )
        }
        okHttpClientBuilder.build().newCall(request)
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callbackInternalError(e, onIntegrityResultListener)
                }

                override fun onResponse(call: Call, response: Response) {
                    kotlin.runCatching {
                        val result = response.body!!.string()
                        val jsonObject = JSONObject(result)
                        onIntegrityResultListener?.onSuccess(
                            IntegrityResult(
                                jsonObject.optBoolean("isPlayRecognized"),
                                jsonObject.optBoolean("isMeetsDeviceIntegrity"),
                                jsonObject.optBoolean("isMeetsBasicIntegrity")
                            ),
                            result
                        )
                    }.onFailure {
                        callbackInternalError(null, onIntegrityResultListener)
                    }
                }
            })
    }

    private fun callbackInternalError(
        e: Exception?,
        onIntegrityResultListener: OnIntegrityResultListener?
    ) {
        callbackFailure(
            IntegrityException(
                IntegrityErrorCode.INTERNAL_ERROR,
                e?.message ?: "Unknown Error"
            ), onIntegrityResultListener
        )
    }

    private fun callbackFailure(
        e: Exception,
        onIntegrityResultListener: OnIntegrityResultListener?
    ) {
        e.takeIf {
            it is IntegrityException
        }?.let {
            val exp = it as IntegrityException
            logE(
                String.format(
                    "integrityTokenResponse failure, errorCode:%s, errorMessage:%s",
                    exp.errorCode,
                    exp.message
                )
            )
            onIntegrityResultListener?.onFailure(exp)
        } ?: let {
            var errorMessage = "Unknown Error"
            var errorCode = IntegrityErrorCode.INTERNAL_ERROR
            e.message?.let {
                //Pretty junk way of getting the error code but it works
                errorCode =
                    it.replace("\n".toRegex(), "").replace(":(.*)".toRegex(), "").toInt()
                errorMessage = when (errorCode) {
                    IntegrityErrorCode.API_NOT_AVAILABLE ->
                        "Integrity API is not available.\n\nThe Play Store version might be old, try updating it."

                    IntegrityErrorCode.APP_NOT_INSTALLED ->
                        "The calling app is not installed.\n\nThis shouldn't happen. If it does please open an issue on Github."

                    IntegrityErrorCode.APP_UID_MISMATCH ->
                        "The calling app UID (user id) does not match the one from Package Manager.\n\nThis shouldn't happen. If it does please open an issue on Github."

                    IntegrityErrorCode.CANNOT_BIND_TO_SERVICE ->
                        "Binding to the service in the Play Store has failed.\n\nThis can be due to having an old Play Store version installed on the device."

                    IntegrityErrorCode.GOOGLE_SERVER_UNAVAILABLE ->
                        "Unknown internal Google server error."

                    IntegrityErrorCode.INTERNAL_ERROR ->
                        "Unknown internal error."

                    IntegrityErrorCode.NETWORK_ERROR ->
                        "No available network is found.\n\nPlease check your connection."

                    IntegrityErrorCode.NO_ERROR ->
                        "No error has occurred.\n\n" +
                                "If you ever get this, congrats, I have no idea what it means."

                    IntegrityErrorCode.NONCE_IS_NOT_BASE64 ->
                        "Nonce is not encoded as a base64 web-safe no-wrap string.\n\n" +
                                "This shouldn't happen. If it does please open an issue on Github."

                    IntegrityErrorCode.NONCE_TOO_LONG ->
                        "Nonce length is too long.\n" +
                                "This shouldn't happen. If it does please open an issue on Github."

                    IntegrityErrorCode.NONCE_TOO_SHORT ->
                        "Nonce length is too short.\n" +
                                "This shouldn't happen. If it does please open an issue on Github."

                    IntegrityErrorCode.PLAY_SERVICES_NOT_FOUND ->
                        "Play Services is not available or version is too old.\n\n" +
                                "Try updating Google Play Services."

                    IntegrityErrorCode.PLAY_STORE_ACCOUNT_NOT_FOUND ->
                        "No Play Store account is found on device.\n\n" +
                                "Try logging into Play Store."

                    IntegrityErrorCode.PLAY_STORE_NOT_FOUND ->
                        "No Play Store app is found on device or not official version is installed.\n\n" +
                                "This app can't work without Play Store."

                    IntegrityErrorCode.TOO_MANY_REQUESTS ->
                        "The calling app is making too many requests to the API and hence is throttled.\n" +
                                "\n" +
                                "This shouldn't happen. If it does please open an issue on Github."

                    else -> "Unknown Error"
                }
                logE(
                    String.format(
                        "integrityTokenResponse failure, errorCode:%s, errorMessage:%s",
                        errorCode,
                        errorMessage
                    )
                )
            } ?: logE("integrityTokenResponse failure unknown error:$errorMessage")
            onIntegrityResultListener?.onFailure(IntegrityException(errorCode, errorMessage))
        }
    }


    private fun logE(msg: String) {
        if (integrityConfiguration.loggable) {
            Log.e(PlayIntegrityHelper::class.simpleName, msg)
        }
    }

    private fun generateOneTimeRequestNonce(): String {
        val length = 50
        var nonce = ""
        val allowed = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        for (i in 0 until length) {
            nonce += allowed[floor(Math.random() * allowed.length).toInt()].toString()
        }
        return nonce
    }

    interface OnIntegrityResultListener {
        /**
         * 请求后台校验integrity Token成功
         * @param integrityResult 校验结果
         * @param responseString 返回的数据，若需自定义成功状态可以使用
         */
        fun onSuccess(integrityResult: IntegrityResult, responseString: String)
        fun onFailure(e: IntegrityException)
    }

}