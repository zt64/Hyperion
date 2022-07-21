package com.hyperion.di

import com.hyperion.ui.viewmodel.*
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::PlayerViewModel)
    viewModelOf(::ChannelViewModel)
    viewModelOf(::SearchViewModel)
    viewModelOf(::SettingsViewModel)
}