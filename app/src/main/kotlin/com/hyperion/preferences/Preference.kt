package com.hyperion.preferences

import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.edit
import kotlin.reflect.KProperty

lateinit var sharedPreferences: SharedPreferences

class Preference<T>(
    private val key: String,
    private val defaultValue: T,
    private val getter: SharedPreferences.(key: String, defaultValue: T) -> T?,
    private val setter: SharedPreferences.Editor.(key: String, newValue: T) -> Unit
) {
    @Suppress("RedundantSetter")
    var value by mutableStateOf(sharedPreferences.getter(key, defaultValue) ?: defaultValue)
        private set

    operator fun getValue(
        thisRef: Any?,
        property: KProperty<*>
    ) = value

    operator fun setValue(
        thisRef: Any?,
        property: KProperty<*>,
        newValue: T
    ) {
        value = newValue
        sharedPreferences.edit {
            setter(key, newValue)
        }
    }
}

fun stringPreference(
    key: String,
    defaultValue: String = ""
) = Preference(
    key = key,
    defaultValue = defaultValue,
    getter = SharedPreferences::getString,
    setter = SharedPreferences.Editor::putString
)

fun stringSetPreference(
    key: String,
    defaultValue: Set<String> = setOf()
) = Preference(
    key = key,
    defaultValue = defaultValue,
    getter = SharedPreferences::getStringSet,
    setter = SharedPreferences.Editor::putStringSet
)

fun booleanPreference(
    key: String,
    defaultValue: Boolean = false
) = Preference(
    key = key,
    defaultValue = defaultValue,
    getter = SharedPreferences::getBoolean,
    setter = SharedPreferences.Editor::putBoolean
)

fun intPreference(
    key: String,
    defaultValue: Int = 0
) = Preference(
    key = key,
    defaultValue = defaultValue,
    getter = SharedPreferences::getInt,
    setter = SharedPreferences.Editor::putInt
)

fun longPreference(
    key: String,
    defaultValue: Long = 0
) = Preference(
    key = key,
    defaultValue = defaultValue,
    getter = SharedPreferences::getLong,
    setter = SharedPreferences.Editor::putLong
)

inline fun <reified E : Enum<E>> enumPreference(
    key: String,
    defaultValue: E
) = Preference(
    key = key,
    defaultValue = defaultValue,
    getter = { _, _ -> enumValues<E>()[getInt(key, defaultValue.ordinal)] },
    setter = { _, value -> putInt(key, value.ordinal) }
)