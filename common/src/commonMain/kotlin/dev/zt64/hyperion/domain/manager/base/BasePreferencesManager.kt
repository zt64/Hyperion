package dev.zt64.hyperion.domain.manager.base

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set
import kotlin.enums.enumEntries
import kotlin.reflect.KProperty

private typealias Getter<T> = (key: String, defaultValue: T) -> T
private typealias Setter<T> = (key: String, newValue: T) -> Unit

@Suppress("SameParameterValue", "MemberVisibilityCanBePrivate")
abstract class BasePreferenceManager(val settings: Settings) {
    protected fun preference(
        key: String,
        defaultValue: String
    ) = Preference(
        key = key,
        defaultValue = defaultValue,
        getter = settings::get,
        setter = settings::set
    )

    protected fun preference(key: String) = Preference<String?>(
        key = key,
        defaultValue = null,
        getter = { actualKey, defaultValue -> settings.getStringOrNull(actualKey) ?: defaultValue },
        setter = settings::set
    )

    protected fun preference(
        key: String,
        defaultValue: Boolean
    ) = Preference(
        key = key,
        defaultValue = defaultValue,
        getter = settings::get,
        setter = settings::set
    )

    protected fun preference(
        key: String,
        defaultValue: Int
    ) = Preference(
        key = key,
        defaultValue = defaultValue,
        getter = settings::get,
        setter = settings::set
    )

    protected fun preference(
        key: String,
        defaultValue: Float
    ) = Preference(
        key = key,
        defaultValue = defaultValue,
        getter = settings::get,
        setter = settings::set
    )

    protected fun preference(
        key: String,
        defaultValue: Long
    ) = Preference(
        key = key,
        defaultValue = defaultValue,
        getter = settings::get,
        setter = settings::set
    )

    protected inline fun <reified E : Enum<E>> preference(
        key: String,
        defaultValue: E
    ) = Preference(
        key = key,
        defaultValue = defaultValue,
        getter = settings::get,
        setter = { actualKey, value -> settings.putEnum(actualKey, value) }
    )

    protected class Preference<T>(
        private val key: String,
        defaultValue: T,
        getter: Getter<T>,
        private val setter: Setter<T>
    ) {
        private var value by mutableStateOf(getter(key, defaultValue))

        operator fun getValue(
            thisRef: Any,
            property: KProperty<*>
        ) = value

        operator fun setValue(
            thisRef: Any,
            property: KProperty<*>,
            value: T
        ) {
            this.value = value
            setter(key, value)
        }
    }

    fun clear() = settings.clear()
}

@OptIn(ExperimentalStdlibApi::class)
inline fun <reified E : Enum<E>> Settings.getEnum(
    key: String,
    defaultValue: E
): E = enumEntries<E>()[getInt(key, defaultValue.ordinal)]

inline fun <reified E : Enum<E>> Settings.putEnum(
    key: String,
    value: E
) {
    putInt(key, value.ordinal)
}

inline operator fun <reified E : Enum<E>> Settings.get(
    key: String,
    defaultValue: E
): E = getEnum(key, defaultValue)

inline operator fun <reified E : Enum<E>> Settings.set(
    key: String,
    value: E
) {
    putEnum(key, value)
}