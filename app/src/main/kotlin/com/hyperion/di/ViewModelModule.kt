package com.hyperion.di

import com.hyperion.ui.viewmodel.MainViewModel
import com.hyperion.ui.viewmodel.PlayerViewModel
import com.hyperion.ui.viewmodel.SearchViewModel
import com.hyperion.ui.viewmodel.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::MainViewModel)
    viewModelOf(::PlayerViewModel)
    viewModelOf(::SearchViewModel)
    viewModelOf(::SettingsViewModel)
}