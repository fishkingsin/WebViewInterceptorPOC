package hk.com.nmg.interceptorpoc

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.webkit.*
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/**
 * The custom [WebView] for this application.
 *
 * @author Richard Arriaga
 *
 * @property model
 *   The active [WebViewModel].
 * @property navController
 *   The [NavController] used for screen navigation.
 */
@SuppressLint("ViewConstructor")
class CustomWebView(
    val model: WebViewModel,
    val navController: NavController, context: Context
) : WebView(context)
{
    /**
     * The ExampleNativeAndroidAPI that contains the native Android API that can
     * be called by Javascript running in this [CustomWebView]. These functions
     * are evaluated on the Android side of the wall.
     */
    val nativeAndroidAPI: ExampleNativeAndroidAPI = ExampleNativeAndroidAPI(
        CoroutineScope(Dispatchers.Main + SupervisorJob()),
        model,
        this,
        navController)

    /**
     * Expose the [ExampleNativeAndroidAPI] to this [CustomWebView].
     */
    internal fun exposeAndroidAPI ()
    {
        // This exposes all the public JavascriptInterface annotated functions
        // in ExampleNativeAndroidAPI to the WebView. The second argument
        // provides the name of the interface in Javascript that the functions
        // are callable through.
        addJavascriptInterface(
            nativeAndroidAPI, ExampleNativeAndroidAPI::class.java.simpleName)
    }
}

/**
 * Answer the configured [WebView] for this application.
 *
 * @param context
 *   The [Context] object used to access application assets.
 * @param url
 *   The url of file to load.
 * @param savedInstanceState
 *   The [Bundle] that is used to restore app state or `null` if no app
 *   state to restore.
 * @param model
 *   The [WebViewModel] that manages the state of the [WebView].
 * @param navController
 *   The [NavController] used for application navigation.
 * @return
 *   The constructed and configured [WebView].
 */
@SuppressLint("JavascriptInterface")
fun appWebView (
    context: Context,
    url: String,
    savedInstanceState: Bundle?,
    model: WebViewModel,
    navController: NavController,
    webAppInterface: WebAppInterface? = WebAppInterface(context),
    webViewClientListener: WebViewClientListener? = null
) = CustomWebView(model, navController, context).apply {
    layoutParams = ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT)
    webViewClient = webViewClient(webViewClientListener)

    // The WebView's WebSettings, `WebView.settings`, manages the settings
    // for a WebView. See:
    // https://developer.android.com/reference/android/webkit/WebSettings

    // This tells the WebView to enable Javascript execution. Note it
    // can enable cross site scripting (xss) vulnerabilities
    @SuppressLint("SetJavaScriptEnabled")
    settings.javaScriptEnabled = true

    // This enables Javascript `window.localStorage` to exist and be usable
    // by the app.
    settings.domStorageEnabled = true

    // Establish the WebChromeClient; it enables custom actions on certain
    // events.
    webChromeClient = webChromeClient()

    // This exposes all the public JavascriptInterface annotated
    // functions in ExampleNativeAndroidAPI to the WebView. The second
    // argument provides the name of the interface in Javascript that
    // the functions are callable through.
    exposeAndroidAPI()

    // This will load the website from the provided URL. This can either
    // be a URL to a webpage available from the internet:
    //   webView.loadUrl("https://www.google.com/")
    // or a file placed in the
    //   - app/src/main/assets folder using:
    //       webView.loadUrl("file:///android_asset/<target-file>")
    //   - app/src/main/res folder using:
    //       webView.loadUrl("file:///android_res/<target-file>").
    // A page can also be created dynamically from a string:
    //   webView.loadDataWithBaseURL(
    //			"file:///android_asset/",
    //			"<html><body>A custom page!</body></html>",
    //			"text/html",
    //			"UTF-8",
    //			null)
    if (webAppInterface != null)
    {
        addJavascriptInterface(
            webAppInterface, "Android")
    }
    if (savedInstanceState !== null)
    {
        restoreState(savedInstanceState)
    }
    else
    {
        loadUrl(url)
    }
}
