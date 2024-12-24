package hk.com.nmg.interceptorpoc

import android.content.Context
import android.util.Log
import android.webkit.JavascriptInterface

class WebAppInterface(private val mContext: Context) {

    /** Show a toast from the web page.  */
    @JavascriptInterface
    fun javascriptCallback(message: String) {

        Log.d("WebAppInterface", message)
    }
}