package hk.com.nmg.interceptorpoc

class WebViewInterceptorManager(
) {
    fun interceptor(url: String): WebViewInterceptor? {
        return DomainName.from(url)?.webViewInterceptor
    }
}