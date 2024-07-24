package dev.zt64.hyperion.domain.manager

import android.app.Application
import android.content.Intent
import androidx.core.net.toUri
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

internal actual class UrlManager : KoinComponent {
    private val application: Application = get()

    actual fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, url.toUri()).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        application.startActivity(intent)
    }
}