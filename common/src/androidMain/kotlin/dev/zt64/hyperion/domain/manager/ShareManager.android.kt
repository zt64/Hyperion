package dev.zt64.hyperion.domain.manager

import android.app.Application
import android.content.Intent
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

actual class ShareManager : KoinComponent {
    private val application: Application = get()

    actual fun share(content: String, label: String?) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"

            putExtra(Intent.EXTRA_TEXT, content)
            putExtra(Intent.EXTRA_TITLE, label)
        }

        application.startActivity(
            Intent.createChooser(shareIntent, null).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
        )
    }
}