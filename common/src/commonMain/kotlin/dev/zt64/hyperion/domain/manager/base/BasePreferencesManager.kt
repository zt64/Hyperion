package dev.zt64.hyperion.domain.manager.base

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set
import kotlin.reflect.KProperty

private typealias Getter<T> = (key: String, defaultValue: T) -> T
private typealias Setter<T> = (key: String, newValue: T) -> Unit

@Suppress("SameParameterValue", "MemberVisibilityCanBePrivate")
abstract class BasePreferenceManager(protected val settings: Settings) {
    protected fun preference(key: String?, defaultValue: String) = PreferenceProvider(
        key = key,
        defaultValue = defaultValue,
        getter = settings::get,
        setter = settings::set
    )

    protected fun preference(key: String?) = PreferenceProvider<String?>(
        key = key,
        defaultValue = null,
        getter = { key, defaultValue -> settings.getStringOrNull(key) ?: defaultValue },
        setter = settings::set
    )

    protected fun preference(key: String?, defaultValue: Boolean) = PreferenceProvider(
        key = key,
        defaultValue = defaultValue,
        getter = settings::get,
        setter = settings::set
    )

    protected fun preference(key: String?, defaultValue: Int) = PreferenceProvider(
        key = key,
        defaultValue = defaultValue,
        getter = settings::get,
        setter = settings::set
    )

    protected fun preference(key: String?, defaultValue: Float) = PreferenceProvider(
        key = key,
        defaultValue = defaultValue,
        getter = settings::get,
        setter = settings::set
    )

    protected fun preference(key: String?, defaultValue: Long) = PreferenceProvider(
        key = key,
        defaultValue = defaultValue,
        getter = settings::get,
        setter = settings::set
    )

    protected inline fun <reified E : Enum<E>> preference(
        key: String?,
        defaultValue: E
    ): PreferenceProvider<E> {
        return PreferenceProvider(
            key = key,
            defaultValue = defaultValue,
            getter = settings::get,
            setter = settings::set
        )
    }

    protected fun preference(defaultValue: Boolean) = preference(null, defaultValue)
    protected fun preference(defaultValue: Int) = preference(null, defaultValue)
    protected fun preference(defaultValue: Float) = preference(null, defaultValue)
    protected fun preference(defaultValue: Long) = preference(null, defaultValue)
    protected inline fun <reified E : Enum<E>> preference(defaultValue: E): PreferenceProvider<E> {
        return preference(null, defaultValue)
    }

    protected class Preference<T>(
        private val key: String,
        defaultValue: T,
        getter: Getter<T>,
        private val setter: Setter<T>
    ) {
        private var value by mutableStateOf(getter(key, defaultValue))

        operator fun getValue(thisRef: Any, property: KProperty<*>) = value
        operator fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
            this.value = value
            setter(key, value)
        }
    }

    protected class PreferenceProvider<T>(
        private val key: String?,
        private val defaultValue: T,
        private val getter: Getter<T>,
        private val setter: Setter<T>
    ) {
        operator fun provideDelegate(thisRef: Any, property: KProperty<*>): Preference<T> {
            return Preference(
                key = key ?: property.name.camelToSnakeCase(),
                defaultValue = defaultValue,
                getter = getter,
                setter = setter
            )
        }
    }

    fun clear() = settings.clear()
}

fun String.camelToSnakeCase(): String {
    val pattern = "(?<=.)[A-Z]".toRegex()
    return replace(pattern, "_$0").lowercase()
}

inline fun <reified E : Enum<E>> Settings.getEnum(key: String, defaultValue: E): E {
    return enumValueOf(getString(key, defaultValue.name))
}

inline fun <reified E : Enum<E>> Settings.putEnum(key: String, value: E) {
    putString(key, value.name)
}

inline operator fun <reified E : Enum<E>> Settings.get(key: String, defaultValue: E): E {
    return getEnum(key, defaultValue)
}

inline operator fun <reified E : Enum<E>> Settings.set(key: String, value: E) {
    putEnum(key, value)
}