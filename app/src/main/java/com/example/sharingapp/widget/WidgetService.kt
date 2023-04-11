package com.example.sharingapp.widget

import android.content.Intent
import android.widget.RemoteViewsService

class WidgetService : RemoteViewsService() {

    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        return intent?.let {
            WidgetRemoteViewsFactory(
                applicationContext,
                it
            )
        } ?: throw IllegalArgumentException("Intent cannot be null")
    }
}
