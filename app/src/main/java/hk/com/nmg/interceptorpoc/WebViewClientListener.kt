package hk.com.nmg.interceptorpoc

import android.graphics.Bitmap
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebView

interface WebViewClientListener {
    fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean
    fun onPageStarted(view: WebView?, string: String?, bitmap: Bitmap?): Boolean
    fun onPageFinished(view: WebView?, url: String?): Boolean
}

class WebViewClientListenerAdapter : WebViewClientListener {
    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
        if (request.url.toString().startsWith("https://ebx.sh")) {
            return false
        }
        if (request.url.toString().startsWith("https://www.weekendhk.com")) {
            view.loadUrl(request.url.toString())
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