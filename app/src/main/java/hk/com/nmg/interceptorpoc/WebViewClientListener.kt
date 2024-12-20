package hk.com.nmg.interceptorpoc

import android.graphics.Bitmap
import android.webkit.WebResourceRequest
import android.webkit.WebView

interface WebViewClientListener {
    fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean
    fun onPageStarted(view: WebView?, string: String?, bitmap: Bitmap?)
    fun onPageFinished(view: WebView?, url: String?)
}