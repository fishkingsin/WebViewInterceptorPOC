package hk.com.nmg.interceptorpoc

import android.net.Uri

/*
    * The domain names that are supported by this application.
 */

enum class DomainName(val domain: String) {
    BITLY("bit.ly"),
    T("t.co"),
    GOOGL("goo.gl"),
    TINYURL("tinyurl.com"),
    OW("ow.ly"),
    ISGD("is.gd"),
    BUFFLY("buff.ly"),
    ADFLY("adf.ly"),
    MCAFFEE("mcaf.ee"),
    PNMGCOMHK("p.nmg.com.hk"),
    EBXSH("ebx.sh");

    // Extension to map domain names to their respective WebviewInterceptor
    val webViewInterceptor: WebViewInterceptor
        get() = when (this) {
            EBXSH -> EbxshWebViewInterceptor()
            else -> DefaultWebViewInterceptor()
        }

    companion object
}

/*
    * Answer the DomainName that matches the given string.
    *
    * @param string
    *   The string to match.
    *
    * @return
    *   The DomainName that matches the given string or null if no match is found.
 */
fun DomainName.Companion.from(string: String): DomainName? {
    return DomainName.entries.firstOrNull {
        string.contains(it.domain)
    }
}

fun Uri.withOutProtocol(): String {
    return this.toString().replaceFirst("http://", "").replaceFirst("https://", "")
}