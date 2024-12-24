package hk.com.nmg.interceptorpoc

import android.graphics.Bitmap
import android.util.Log
import android.webkit.ConsoleMessage
import android.webkit.RenderProcessGoneDetail
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient


/**
 * Create a custom [WebViewClient]. It is responsible for most of the
 * actions that occur inside a WebView. For example allows you to
 *  - intercept url requests for special handling
 *  - dictate where a URL is loaded (in WebView or the default browser)
 *  - What to do for certain events like onPageFinished, onReceivedSslError,
 *    etc
 */
fun webViewClient(webViewClientListener: WebViewClientListener?) = object : WebViewClient()
{
    val webViewClientListener = webViewClientListener
    // By overriding this with a blanket return of false we are making
    // all web navigation occur in our WebView. If we want some web
    // traffic to open in the default browser we have to add logic to
    // check whether or not we want to open the URL in the default
    // browser by returning true.
    // Consider overriding shouldInterceptRequest to determine how
    // requests should be handled.
    override fun shouldOverrideUrlLoading(
        view: WebView,
        request: WebResourceRequest
    ): Boolean
    {
        Log.d("WebViewClient", "shouldOverrideUrlLoading ${request.url}")
        Log.d("WebViewClient", "shouldOverrideUrlLoading ${request.isRedirect}")

        if (webViewClientListener != null)
        {
            return webViewClientListener.shouldOverrideUrlLoading(view, request)
        }
        return super.shouldOverrideUrlLoading(view, request)
    }

    override fun onRenderProcessGone(
        view: WebView,
        detail: RenderProcessGoneDetail
    ): Boolean
    {
        Log.d("WebViewClient", "onRenderProcessGone ${detail.didCrash()}")
        if (!detail.didCrash())
        {
            // Renderer was killed because the system ran out of memory.
            // The app can recover gracefully by creating a new WebView
            // instance in the foreground.
            Log.e(
                "WebViewExample",
                "System killed the WebView rendering process " +
                        "to reclaim memory. Recreating...")
            view.destroy()
            // By this point, the instance variable "webView" is guaranteed
            // to be null, so it's safe to reinitialize it.
            return true
        }
        // Renderer crashed because of an internal error.
        Log.e(
            "WebViewExample",
            "The WebView rendering process crashed!"
        )
        // In this example, the app itself crashes after detecting that
        // the renderer crashed. If you choose to handle the crash more
        // gracefully and allow your app to continue executing, you
        // should:
        //   1) destroy the current WebView instance
        //   2) specify logic for how the app can continue executing
        //   3) return "true" instead
        return false
    }

    override fun onReceivedError(
        view: WebView?,
        request: WebResourceRequest?,
        error: WebResourceError?
    ) {
        Log.e("WebViewClient", "onReceivedError ${error?.description}")
        super.onReceivedError(view, request, error)
    }

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        Log.d("WebViewClient", "onPageStarted $url")
        if (webViewClientListener != null)
        {
            val override = webViewClientListener.onPageStarted(view, url, favicon)
            if (override == true) return
        }

        super.onPageStarted(view, url, favicon)
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        Log.d("WebViewClient", "onPageFinished $url")
        if (webViewClientListener != null)
        {
            val override = webViewClientListener.onPageFinished(view, url)
            if (override == true) return
        }
        super.onPageFinished(view, url)
    }
}

/**
 * Create a [WebChromeClient]. It captures logging to the that would be
 * written to the browser console and logs it using Android's Log API.
 *
 * It is also capable of performing custom actions when certain events
 * happen.
 */
fun webChromeClient() = object : WebChromeClient()
{
    override fun onConsoleMessage(
        consoleMessage: ConsoleMessage
    ): Boolean
    {
        val messageLevel = consoleMessage.messageLevel()
        val logMessage = buildString {
            append("(")
            append(consoleMessage.sourceId())
            append(": ")
            append(messageLevel)
            append(") Line number: ")
            append(consoleMessage.lineNumber())
            append("\n")
            append(consoleMessage.message())
        }
        if (messageLevel == null)
        {
            Log.i("WebViewConsole", logMessage)
            return false
        }
        when (messageLevel)
        {
            ConsoleMessage.MessageLevel.TIP,
            ConsoleMessage.MessageLevel.LOG ->
                Log.i("WebViewConsole", logMessage)
            ConsoleMessage.MessageLevel.WARNING ->
                Log.w("WebViewConsole", logMessage)
            ConsoleMessage.MessageLevel.ERROR ->
                Log.e("WebViewConsole", logMessage)
            ConsoleMessage.MessageLevel.DEBUG ->
                Log.d("WebViewConsole", logMessage)
        }
        return false
    }
}
