package hk.com.nmg.interceptorpoc

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class ComposeStateDelegate<T>(initialValue: T) : ReadWriteProperty<Any?, T> {
    private var state = mutableStateOf(initialValue)

    override fun getValue(thisRef: Any?, property: KProperty<*>): T = state.value

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        state.value = value
    }
}

@Composable
fun <T> rememberState(initialValue: T): ComposeStateDelegate<T> {
    return remember { ComposeStateDelegate(initialValue) }
}
