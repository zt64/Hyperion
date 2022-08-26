package com.hyperion.ui.viewmodel

import android.app.Application
import android.content.Intent
import androidx.lifecycle.ViewModel

class PlaylistViewModel(
    private val application: Application
) : ViewModel() {
    fun sharePlaylist() {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"

//            putExtra(Intent.EXTRA_TEXT, video!!.shareUrl)
//            putExtra(Intent.EXTRA_TITLE, video!!.title)
        }

        application.startActivity(Intent.createChooser(shareIntent, null).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        })
    }
}