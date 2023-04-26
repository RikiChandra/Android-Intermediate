package com.example.sharingapp.viewmodel



import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.*
import com.example.sharingapp.*
import com.example.sharingapp.data.AuthRepository
import com.example.sharingapp.data.StoryRepository
import com.example.sharingapp.view.main.MainViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

    private lateinit var viewModel: MainViewModel


    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        storyRepository = mock(StoryRepository::class.java)
        viewModel = MainViewModel(storyRepository, authRepository)
    }

    @Test
    fun `when load stories successfully then ensure data is not null`() = runTest {
        val expectedData = MutableLiveData<String?>()
        expectedData.value = FakeDummyGeneratorData.getBearerToken()

        `when`(
            authRepository.getToken()
        ).thenReturn(expectedData)

        val actualData = viewModel.isToken().getOrAwaitValue()

        verify(authRepository).getToken()

        assertNotNull(actualData)
        assertEquals(FakeDummyGeneratorData.getBearerToken(), actualData)
    }
}








