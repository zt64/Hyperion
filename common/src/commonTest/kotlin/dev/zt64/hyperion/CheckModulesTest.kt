package dev.zt64.hyperion

import dev.zt64.hyperion.di.appModule
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.test.KoinTest
import org.koin.test.verify.verify
import kotlin.test.Test

class CheckModulesTest : KoinTest {
    @OptIn(KoinExperimentalAPI::class)
    @Test
    fun verifyKoinApp() {
        appModule.verify()
    }
}