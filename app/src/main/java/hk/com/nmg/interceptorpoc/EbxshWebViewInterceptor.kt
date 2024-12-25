package hk.com.nmg.interceptorpoc

import android.graphics.Bitmap
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

/*
    * The domain Ebx.sh WebViewInterceptor for this application.
    * This interceptor will intercept all URLs that are in the whitelist
 */
class EbxshWebViewInterceptor(
    override val whitelist: List<String> = DomainName.entries.filter {
        it == DomainName.EBXSH
    }.map { it.domain },
    override val targetURLs: List<String> = listOf(
        "https://www.weekendhk.com"
    )
) : WebViewInterceptor {
    val TAG = "EbxshWebViewInterceptor"
    override fun shouldInterceptRequest(
        view: WebView?,
        request: WebResourceRequest?
    ): WebResourceResponse? {
        Log.d(TAG, "shouldInterceptRequest#request=${request?.url}")
        request?.url?.let { itUrl ->
            val connection =
                URL(itUrl.toString()).openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("User-Agent", "Mozilla/5.0")
            connection.instanceFollowRedirects = true
            return parseResponse(connection)
        }
        return null
    }

    private fun parseResponse(connection: HttpURLConnection): WebResourceResponse? {
        try {
            connection.connect()
            Log.d(
                TAG,
                "shouldInterceptRequest#connection.responseCode=${connection.responseCode}"
            )
            if (connection.responseCode >= 300 && connection.responseCode < 400) {
                Log.d(
                    TAG,
                    "shouldInterceptRequest#need redirect${connection.url}"
                )
            } else {
                Log.d(
                    TAG,
                    "shouldInterceptRequest#no redirect${connection.url}"
                )
            }
            Log.d(TAG, "shouldInterceptRequest#no#${connection.url}")
            val response =
                getResponse(connection)
            Log.d(TAG, "shouldInterceptRequest#response=${response.toString()}")
            return WebResourceResponse(
                "text/html",
                "UTF-8",
                ByteArrayInputStream(response.toByteArray())
            )
        } catch (e: Exception) {
            Log.e(TAG, "shouldInterceptRequest#exception=${e.message}")
        }
        return null
    }

    private fun getResponse(connection: HttpURLConnection): String =
        if (connection.responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader(InputStreamReader(connection.inputStream)).use {
                it.readText()
            }
                .replace(
                    "setTimeout(function() {",
                    "/*\nsetTimeout(function() {"
                )
                .replace(
                    "}, 1000);",
                    "}, 1000); \n*/\nwindow. history.go(-1);"
                )
        } else {
            "Error: ${connection.responseCode}"
        }

    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
        if (whitelist.filter { request.url.withOutProtocol().startsWith(it) }.isNotEmpty()) {
            return false
        }
        if (targetURLs.filter { request.url.toString().startsWith(it) }.isNotEmpty()) {
            // intercept here
            Log.d(TAG, "intercept ${request.url}")

            return true
        }
        Log.d(TAG, "shouldOverrideUrlLoading ${request.url}")
        return true
    }

    override fun onPageStarted(view: WebView?, string: String?, bitmap: Bitmap?): Boolean {
        Log.d(TAG, "onPageStarted $string")
        return true
    }

    override fun onPageFinished(view: WebView?, url: String?): Boolean {
        Log.d(TAG, "onPageFinished $url")
        return true
    }
}