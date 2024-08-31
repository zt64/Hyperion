package dev.zt64.hyperion.api

import dev.zt64.hyperion.api.domain.repository.InnerTubeRepository
import dev.zt64.hyperion.api.network.service.InnerTubeService
import io.ktor.client.engine.okhttp.OkHttp

val repo = InnerTubeRepository(InnerTubeService(OkHttp))

object TestUtil