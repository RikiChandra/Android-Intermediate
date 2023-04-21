package com.example.sharingapp.widget


import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.bumptech.glide.Glide
import com.example.sharingapp.R
import com.example.sharingapp.api.ApiConfig
import com.example.sharingapp.responses.Story
import com.example.sharingapp.setting.SharedPreference
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking


class WidgetRemoteViewsFactory(private val context: Context, private val intent: Intent) :
    RemoteViewsService.RemoteViewsFactory {

    private var items: List<Story> = emptyList()
    private lateinit var dataStore: DataStore<Preferences>
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    override fun onCreate() {
        dataStore = context.applicationContext.dataStore
    }

    override fun onDataSetChanged() {
        // mengambil data dari server menggunakan retrofit
        val apiService = ApiConfig.getApiService()
        val sharedPreferences = SharedPreference.getInstance(
            context.applicationContext.dataStore
        )

        val token = runBlocking {
            sharedPreferences.ambilToken().first()
        }

//        if (token.isNotEmpty()) {
//            val call = apiService.getStories("Bearer $token", 1, 10, null)
//            val response = call.execute()
//
//            if (response.isSuccessful) {
//                items = (response.body()?.listStory ?: emptyList()) as List<Story>
//            }
//        }
    }

    override fun onDestroy() {}

    override fun getCount(): Int = items.size

    override fun getViewAt(position: Int): RemoteViews {
        val item = items[position]
        val views = RemoteViews(context.packageName, R.layout.widget_item)
        val bitmap = Glide.with(context)
            .asBitmap()
            .load(item.photoUrl)
            .submit()
            .get()
        views.setImageViewBitmap(R.id.imageWidget, bitmap)
        views.setTextViewText(R.id.story_title, item.name)
        return views
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(position: Int): Long = position.toLong()

    override fun hasStableIds(): Boolean = true
}

