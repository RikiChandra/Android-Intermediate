package com.example.sharingapp.viewmodel



import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.*
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import com.example.sharingapp.*
import com.example.sharingapp.data.AuthRepository
import com.example.sharingapp.data.StoryRepository
import com.example.sharingapp.responses.Story
import com.example.sharingapp.view.main.MainViewModel
import com.example.sharingapp.view.story.StoryAdapter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var storyRepository: StoryRepository

    @Mock
    private lateinit var authRepository: AuthRepository

    @Mock
    private lateinit var viewModel: MainViewModel


    @Test
    fun `when load stories successfully then ensure data is not null`() = runTest {
        val expectedData = MutableLiveData<String?>()
        expectedData.value = FakeDummyGeneratorData.getBearerToken()

        `when`(
            authRepository.getToken()
        ).thenReturn(expectedData)

        val actualData =MainViewModel(storyRepository, authRepository).isToken().getOrAwaitValue()

        verify(authRepository).getToken()

        assertNotNull(actualData)
        assertEquals(FakeDummyGeneratorData.getBearerToken(), actualData)
    }


    @Test
    fun `when get all data stories `() = runTest{
        val fakeData = FakeDummyGeneratorData.getFakeStories()
        val data = FakePagingSource.snapshot(fakeData)

        val story = MutableLiveData<PagingData<Story>>()
        story.value = data

        `when`(viewModel.stories()).thenReturn(story)

        val actualData = viewModel.stories().getOrAwaitValue()
        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            mainDispatcher = mainDispatcherRule.testDispatcher,
            workerDispatcher = mainDispatcherRule.testDispatcher
        )

        differ.submitData(actualData)

        advanceUntilIdle()

        verify(viewModel).stories()
        assertNotNull(differ.snapshot())
        assertEquals(differ.snapshot().size, fakeData.size)



    }

    private val noopListUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }


}








