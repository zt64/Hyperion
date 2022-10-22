package com.hyperion.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable

@Composable
inline fun <reified T : Destination> rememberNavigator(initial: T): RegularNavigator<T> {
    return rememberSaveable(saver = RegularNavigator.saver()) {
        RegularNavigator(initial)
    }
}

@Composable
inline fun <reified T : Destination> rememberBackstackNavigator(initial: T): BackstackNavigator<T> {
    return rememberSaveable(saver = BackstackNavigator.saver()) {
        BackstackNavigator(initial)
    }
}

interface Navigator<T : Destination> {
    val currentDestination: T
}

class RegularNavigator<T : Destination>(initial: T) : Navigator<T> {
    private val item = mutableStateOf(initial)

    override val currentDestination: T
        get() = item.value

    fun replace(destination: T) {
        item.value = destination
    }

    companion object {
        fun <T : Destination> saver(): Saver<RegularNavigator<T>, T> = Saver(
            save = { it.currentDestination },
            restore = ::RegularNavigator
        )
    }
}

class BackstackNavigator<T : Destination>(initial: T) : Navigator<T> {
    private val items = mutableStateListOf(initial)

    override val currentDestination: T
        get() = items.last()

    fun push(destination: T) {
        items.add(destination)
    }

    fun pop(): Boolean {
        if (items.size <= 1) return false

        return items.removeLastOrNull() != null
    }

    fun replace(destination: T) {
        items[items.lastIndex] = destination
    }

    companion object {
        fun <T : Destination> saver(): Saver<BackstackNavigator<T>, Any> = listSaver(
            save = { it.items.toList() },
            restore = { destinations ->
                val navigator = BackstackNavigator(destinations.first())

                for (i in 1 until destinations.size) navigator.push(destinations[i])

                navigator
            }
        )
    }
}