package hk.com.nmg.interceptorpoc

import android.graphics.Bitmap
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView

/*
    * The default WebViewInterceptor for this application.
    * This interceptor will intercept all URLs that are not in the whitelist
    * and redirect them to the targetURLs.
    * The whitelist is a list of domain names that are supported by this application.
    * The targetURLs is a list of URLs that are supported by this application.
    * The targetURLs will be used to intercept the URLs that are not in the whitelist.
 */
class DefaultWebViewInterceptor(
    override val whitelist: List<String> = DomainName.entries.filter {
        it != DomainName.EBXSH
    }.map { it.domain },
    override val targetURLs: List<String> = listOf(
        "https://www.weekendhk.com"
    )
) : WebViewInterceptor {
    override fun shouldInterceptRequest(
        view: WebView?,
        request: WebResourceRequest?
    ): WebResourceResponse? {
        return null
    }

    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
        if (whitelist.filter { request.url.withOutProtocol().startsWith(it) }.isNotEmpty()) {
            return false
        }
        if (targetURLs.filter { request.url.toString().startsWith(it) }.isNotEmpty()) {
            // intercept here
            Log.d("WebViewClientListener", "intercept ${request.url}")
            view.loadUrl(request.url.withOutProtocol())
            return true
        }
        Log.d("WebViewClientListener", "shouldOverrideUrlLoading ${request.url}")
        return true
    }

    override fun onPageStarted(view: WebView?, string: String?, bitmap: Bitmap?): Boolean {
        Log.d("WebViewClientListener", "onPageStarted $string")
        return true
    }

    override fun onPageFinished(view: WebView?, url: String?): Boolean {
        Log.d("WebViewClientListener", "onPageFinished $url")
        return true
    }

}