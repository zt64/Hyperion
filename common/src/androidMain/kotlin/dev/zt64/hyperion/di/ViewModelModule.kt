package dev.zt64.hyperion.di

import dev.zt64.hyperion.ui.viewmodel.*
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::PlayerViewModel)
    viewModelOf(::ChannelViewModel)
    viewModelOf(::PlaylistViewModel)
    viewModelOf(::TagViewModel)
    viewModelOf(::SearchViewModel)
    viewModelOf(::HistoryViewModel)
    viewModelOf(::ChannelsViewModel)
    viewModelOf(::FeedViewModel)
    viewModelOf(::LibraryViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::AddAccountViewModel)
    viewModelOf(::NotificationsViewModel)
}