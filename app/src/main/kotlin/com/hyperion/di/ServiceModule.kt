package com.hyperion.di

import com.hyperion.network.service.InnerTubeService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {
    @Singleton
    @Provides
    fun provideInnerTubeService(
        httpClient: HttpClient,
        json: Json
    ) = InnerTubeService(httpClient, json)
}