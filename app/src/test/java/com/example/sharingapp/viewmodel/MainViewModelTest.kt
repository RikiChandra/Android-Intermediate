package com.example.sharingapp.viewmodel



import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.*
import androidx.paging.AsyncPagingDataDiffer
import androidx.recyclerview.widget.ListUpdateCallback
import com.example.sharingapp.*
import com.example.sharingapp.data.AuthRepository
import com.example.sharingapp.data.StoryRepository
import com.example.sharingapp.responses.Story
import com.example.sharingapp.view.main.MainViewModel
import com.example.sharingapp.view.story.StoryAdapter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
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


    private lateinit var viewModel: MainViewModel


    @Before
    fun setUp() {
        viewModel = MainViewModel(storyRepository, authRepository)
    }


    @Test
    fun `when get authentication token successfully not null`() = runTest {
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
    fun `when fetching all stories, the first returned data is correct`() = runTest {
        // arrange
        val fakeData = FakeDummyGeneratorData.getFakeStories()
        val data = FakePagingSource.snapshot(fakeData)
        val liveData = MutableLiveData(data)
        `when`(storyRepository.getStory()).thenReturn(liveData)

        // act
        val actualData = viewModel.stories().getOrAwaitValue()
        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = updateCallback,
            mainDispatcher = mainDispatcherRule.testDispatcher,
            workerDispatcher = mainDispatcherRule.testDispatcher
        )
        differ.submitData(actualData)
        advanceUntilIdle()

        // assert
        verify(storyRepository).getStory()
        assertNotNull(differ.snapshot())
        assertEquals(fakeData.size, differ.snapshot().size)

        val firstItemInFakeData = fakeData[0]
        val firstItemInActualData = differ.snapshot().firstOrNull()

        assertNotNull(firstItemInActualData)
        assertEquals(firstItemInFakeData.id, firstItemInActualData?.id)
        assertEquals(firstItemInFakeData.name, firstItemInActualData?.name)
        assertEquals(firstItemInFakeData.description, firstItemInActualData?.description)
        assertEquals(firstItemInFakeData.createdAt, firstItemInActualData?.createdAt)
        assertEquals(firstItemInFakeData.photoUrl, firstItemInActualData?.photoUrl)
        assertEquals(firstItemInFakeData.lat, firstItemInActualData?.lat)
        assertEquals(firstItemInFakeData.lon, firstItemInActualData?.lon)
    }



    @Test
    fun `when no story data available`() = runTest {
        val fakeData = emptyList<Story>()
        val data = FakePagingSource.snapshot(fakeData)
        val flow = flowOf(data).asLiveData()

        `when`(storyRepository.getStory()).thenReturn(flow)

        val actualData = viewModel.stories().getOrAwaitValue()
        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = updateCallback,
            mainDispatcher = mainDispatcherRule.testDispatcher,
            workerDispatcher = mainDispatcherRule.testDispatcher
        )

        differ.submitData(actualData)

        advanceUntilIdle()

        verify(storyRepository).getStory()
        assertNotNull(differ.snapshot())
        assertEquals(differ.snapshot().size, 0)
    }




    private val updateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }


}




