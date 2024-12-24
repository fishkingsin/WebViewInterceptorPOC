package hk.com.nmg.interceptorpoc


import android.webkit.WebView
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

/**
 * The enumeration of the different [Composable]s that can be navigated to in
 * this example application.
 */
@Suppress("EnumEntryName")
enum class NavRoutes
{
    /** Navigate to the main [WebView]. */
    webView,

    /** Navigate to the [CameraView]. */
    camera,

    /** Navigate to the [Scanner]. */
    scanner
}

/**
 * The [Composable] [NavHost] that manages navigation.
 *
 * @param modifier
 *   The [Modifier] to be applied to the layout.
 * @param navController
 *   The [NavHostController] for the [NavHost].
 * @param startDestination
 *   The route for the staring [Composable] destination.
 */
@Composable
fun WebViewAppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = NavRoutes.webView.name,
    url: String = "https://bit.ly/45mm1yp",
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable(NavRoutes.webView.name)
        {
            val backHandlerEnabled =
                remember { mutableStateOf(true) }
            WebViewContent(
                url = url,
                navController = navController,
                backHandlerEnabled = backHandlerEnabled)
        }

    }
}
