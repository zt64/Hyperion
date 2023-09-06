package dev.zt64.hyperion.di

import dev.zt64.innertube.domain.repository.InnerTubeRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal val repositoryModule = module(createdAtStart = true) {
    singleOf(::InnerTubeRepository)
}