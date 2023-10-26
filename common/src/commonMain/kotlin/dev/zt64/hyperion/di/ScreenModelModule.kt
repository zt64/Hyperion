package dev.zt64.hyperion.di

import dev.zt64.hyperion.ui.model.*
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

internal val screenModelModule = module {
    factoryOf(::HomeScreenModel)
    factoryOf(::LibraryScreenModel)
    factoryOf(::HistoryScreenModel)
    factoryOf(::FeedScreenModel)
    factoryOf(::ChannelsScreenModel)
    factoryOf(::ChannelScreenModel)
    factoryOf(::PlaylistScreenModel)
    factoryOf(::PlayerScreenModel)
    factoryOf(::SearchScreenModel)
    factoryOf(::TagScreenModel)
    factoryOf(::NotificationsScreenModel)
    factoryOf(::SettingsScreenModel)
    factoryOf(::AddAccountScreenModel)
}