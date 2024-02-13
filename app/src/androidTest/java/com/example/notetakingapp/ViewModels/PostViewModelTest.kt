package com.example.notetakingapp.ViewModels

import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.example.notetakingapp.Models.Post
import com.example.notetakingapp.PostRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.mock


@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class PostViewModelTest {

    private lateinit var viewModel: PostViewModel
    private lateinit var repository : PostRepository




    private val posts = listOf(
        Post(id = 1, title = "Prova", content = "content prova"),
        Post(id = 2, title = "Prova2", content = "content prova2"),
        Post(id = 3, title = "Prova3", content = "content prova3"),
    )

    @Before
    fun setUp() {
        repository = mock(PostRepository::class.java)
        Mockito.`when`(repository.allPosts).thenReturn( flow {
            emit( posts )
        })

        viewModel = PostViewModel(repository)

    }

    @After
    fun tearDown() {
    }

    @Test
    fun getAllPosts() = runTest {
        viewModel.allPosts.test {
            assertTrue(this.awaitItem().size == 3)
        }
    }

    @Test
    fun insert() {
    }

    @Test
    fun update() {
    }

    @Test
    fun delete() {
    }

    @Test
    fun deleteAll() {
    }

    @Test
    fun getPostById() {
    }

}