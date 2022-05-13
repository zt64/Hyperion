package com.hyperion.di

import com.hyperion.domain.repository.InnerTubeRepository
import com.hyperion.network.service.InnerTubeService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Singleton
    @Provides
    fun provideInnerTubeRepository(
        innerTubeService: InnerTubeService
    ) = InnerTubeRepository(innerTubeService)
}