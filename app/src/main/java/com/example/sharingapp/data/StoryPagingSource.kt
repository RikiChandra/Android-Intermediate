package com.example.sharingapp.data

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.sharingapp.api.ApiService
import com.example.sharingapp.responses.Story
import com.example.sharingapp.setting.SharedPreference
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import retrofit2.awaitResponse

class StoryPagingSource(private val apiService: ApiService, private val sharedPreference: SharedPreference) : PagingSource<Int, Story>() {

    private companion object {
        const val STARTING_PAGE_INDEX = 1
    }

    override fun getRefreshKey(state: PagingState<Int, Story>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                    ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Story> {
        val token = sharedPreference.ambilToken().firstOrNull()
        val bearer = "Bearer $token"
        return try {
            if (token.isNullOrEmpty()) {
                LoadResult.Error(Exception("Token is null or empty"))
            } else {
                val position = params.key ?: STARTING_PAGE_INDEX
                val response = apiService.getStories(bearer, position, params.loadSize, null).awaitResponse().body()
                val stories = response?.listStory as List<Story>
                LoadResult.Page(
                    data = stories,
                    prevKey = if (position == STARTING_PAGE_INDEX) null else position - 1,
                    nextKey = if (stories.isEmpty()) null else position + 1
                )
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }.also {
            Log.d("TAG", "load: $it")
        }
    }

}