package com.hairysnow.play.integrity.api.core.configuration

import android.content.Context
import okhttp3.MediaType
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager

/**
 * Description :
 *
 * @author Jam 2022/11/7
 */
class IntegrityConfiguration private constructor(
    val context: Context,
    val backendUrl: String,
    val timeout: Long,
    val loggable: Boolean,
    val additionalParams: HashMap<String, String>?,
    val sslSocketFactory: SSLSocketFactory?,
    val trustManager: X509TrustManager?,
    val isJsonType: Boolean
) {

    data class Builder(
        var context: Context,
        var backendUrl: String,
        /*请求后台超时时间*/
        var timeout: Long,
        /*是否打印日志，包含了是否打印okhttp日志*/
        var loggable: Boolean = false,
        /*想要多给后端传的参数*/
        var additionalParams: HashMap<String, String>? = null,
        /*sslSocketFactory，用于自定义cert pining*/
        var sslSocketFactory: SSLSocketFactory?,
        /*trustManager，用于自定义cert pining*/
        var trustManager: X509TrustManager?,
        var isJsonType: Boolean
    ) {

        constructor(
            context: Context,
            backendUrl: String
        ) : this(
            context.applicationContext, backendUrl, 10_000, false, null, null, null, false
        )

        constructor(
            context: Context,
            backendUrl: String,
            timeout: Long,
            loggable: Boolean,
            isJsonType: Boolean
        ) : this(
            context.applicationContext, backendUrl, timeout, loggable, null, null, null, isJsonType
        )

        fun context(context: Context) = apply { this.context = context.applicationContext }

        fun backendUrl(backendUrl: String) = apply { this.backendUrl = backendUrl }

        fun timeout(timeout: Long) = apply { this.timeout = timeout }

        fun loggable(loggable: Boolean) = apply { this.loggable = loggable }

        fun additionalParams(additionalParams: HashMap<String, String>?) =
            apply { this.additionalParams = additionalParams }

        fun sslSocketFactory(sslSocketFactory: SSLSocketFactory?) =
            apply { this.sslSocketFactory = sslSocketFactory }

        fun trustManager(trustManager: X509TrustManager?) =
            apply { this.trustManager = trustManager }

        fun isJsonType(isJsonType: Boolean) =
            apply { this.isJsonType = isJsonType }

        fun build() = IntegrityConfiguration(
            context,
            backendUrl,
            timeout,
            loggable,
            additionalParams,
            sslSocketFactory,
            trustManager,
            isJsonType
        )
    }
}
