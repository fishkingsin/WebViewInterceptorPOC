package hk.com.nmg.interceptorpoc


import android.os.Bundle
import android.util.Log
import android.webkit.*
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController


/**
 * The main [WebView] content wrapped in an [WebViewContent].
 *
 * @param model
 *   The [WebViewModel] used by this application.
 * @param url
 *   The URL to load in the [WebView].
 * @param backHandlerEnabled
 *   The [MutableState] of Boolean that, when `true` indicates the custom
 *   [BackHandler] should be active.
 * @param navController
 *   The [NavController] used for screen navigation.
 */
@Composable
fun WebViewContent (
    model: WebViewModel = viewModel<WebViewModel>(
        factory = WebViewModelFactory(null)
    ),
    url: String,
    backHandlerEnabled: MutableState<Boolean>,
    navController: NavController
)
{
    var loading by remember { mutableStateOf(false) }
    // HACK
    // The following is a hack to preserve the WebView state upon a
    // configuration change. At the time of building this app there is no
    // native way available in Compose that preserves WebView state. This
    // workaround was sourced from the conversation in the open issue;
    // https://github.com/google/accompanist/issues/1178.
    var webView: CustomWebView? by remember {
        mutableStateOf(null)
    }
    var savedBundle: Bundle? by rememberSaveable {
        mutableStateOf(null)
    }
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle) {
        val saveState: (() -> Unit) -> Unit = { then ->
            val bundle = Bundle()
            // If the WebView exists, save its state
            webView?.apply {
                // Save web app state to `window.localStorage`.
                nativeAndroidAPI.jsAPI.saveState()

                // Write the WebView's state to the bundle.
                saveState(bundle)

                // Arbitrary operation to run after data is saved.
                then()
            }
            savedBundle = bundle
        }
        // Add a lifecycle observer to react accordingly to lifecycle events.
        val statePreservingObserver = LifecycleEventObserver { _, event ->
            Log.d("DisposableEffect", "Event occurring: ${event.name}")
            when (event) {
                // Need to ensure the state is saved so it can be reloaded
                // ON_RESUME/ON_START
                Lifecycle.Event.ON_PAUSE, Lifecycle.Event.ON_STOP -> saveState {}

                // Want to save the state as we are not sure why the Activity
                // has been destroyed; it could just be in the background and
                // destroyed by Android to free up resources, but it may come
                // back.
                Lifecycle.Event.ON_DESTROY -> saveState {
                    // We want to disable/cleanup the Android Native API.
                    webView?.removeJavascriptInterface(
                        ExampleNativeAndroidAPI::class.java.simpleName
                    )
                }
                // Nothing needed on these events
                Lifecycle.Event.ON_CREATE, Lifecycle.Event.ON_RESUME, Lifecycle.Event.ON_START, Lifecycle.Event.ON_ANY -> {}
            }
        }

        lifecycle.addObserver(statePreservingObserver)

        onDispose {
            backHandlerEnabled.value = false
            lifecycle.removeObserver(statePreservingObserver)
        }
    }
    // END HACK

    // While the WebView is active, we want to intercept the back button presses
    // to navigate back on the WebView stack. If there is no where else to go
    // we want to navigate back normally in the Android view.
    BackHandler(backHandlerEnabled.value)
    {
        val wv = webView
        if (wv?.canGoBack() == true) {
            wv.goBack()
        } else {
            // This actually has to run twice before navigating away from the
            // WebView when the WebView back stack is exhausted. Not sure why.
            navController.popBackStack()
        }
    }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            appWebView(context, url, savedBundle, model, navController).apply {
                webView = this
            }
        },
        update = {
            savedBundle?.let { bundle ->
                // This is a restore of the WebView so don't load the URL
                it.restoreState(bundle)
            } ?: it.loadUrl(url) // First load of the web app, so need to load URL
        })
}