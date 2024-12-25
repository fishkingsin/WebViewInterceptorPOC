package hk.com.nmg.interceptorpoc

import android.graphics.Bitmap
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView

/**
 * Interface for intercepting WebView requests.
 */
interface WebViewInterceptor {
    val whitelist: List<String>
    val targetURLs: List<String>
    fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse?
    fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean
    fun onPageStarted(view: WebView?, string: String?, bitmap: Bitmap?): Boolean
    fun onPageFinished(view: WebView?, url: String?): Boolean
}