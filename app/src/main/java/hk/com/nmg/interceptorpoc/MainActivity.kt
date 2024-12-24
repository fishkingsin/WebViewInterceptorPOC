package hk.com.nmg.interceptorpoc

import android.os.Bundle
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import hk.com.nmg.interceptorpoc.ui.theme.WebViewInterceptorPOCTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Enables WebView debugging in Chrome. To debug web app:
        //  1. Connect Android device to computer
        //  2. Open Chrome web browser
        //  3. navigate to `chrome://inspect`
        //  4. Click "inspect" below your listed device.

        WebView.setWebContentsDebuggingEnabled(true)

        setContent {
            WebViewInterceptorPOCTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { padding ->
                    WebViewAppNavHost(
                        modifier = Modifier.fillMaxSize().padding(padding),
                        url = "file:///android_asset/index.html"
                    )
                }
            }
        }
    }
}

@Composable
fun Content(name: String, modifier: Modifier = Modifier) {

}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    WebViewInterceptorPOCTheme {
        Content("Android")
    }
}