package com.example.notetakingapp

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import app.cash.turbine.test
import com.example.notetakingapp.DAOs.PostDao
import com.example.notetakingapp.Database.AppDatabase
import com.example.notetakingapp.Models.Post
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
@SmallTest
class PostDaoTest {
    private lateinit var database: AppDatabase
    private lateinit var postDao: PostDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        postDao = database.postDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertPost() = runTest(StandardTestDispatcher()) {
        val post = Post(
            id = 1, title = "Prova", content = "content prova"
        )
        postDao.insert(post)

        postDao.getPosts().test {
            val result = this.awaitItem()
            assertTrue(result.size == 1)
            assertTrue(result[0].id == 1)
            assertTrue(result[0].title == "Prova")
            assertTrue(result[0].content == "content prova")
        }
    }


    @Test
    fun getPostById() = runTest {
        val posts = listOf(
            Post(id = 1, title = "Prova", content = "content prova"),
            Post(id = 2, title = "Prova2", content = "content prova2"),
            Post(id = 3, title = "Prova3", content = "content prova3"),
        )

        postDao.insert(posts[0])
        postDao.insert(posts[1])
        postDao.insert(posts[2])

        postDao.getPostById(posts[2].id).test {
            val result = this.awaitItem()
            assertTrue(result.id==posts[2].id)
            assertTrue(result.title == posts[2].title)
            assertTrue(result.content == posts[2].content)
        }
    }


    @Test
    fun getAllPosts() = runTest {
        val posts = listOf(
            Post(id = 1, title = "Prova", content = "content prova"),
            Post(id = 2, title = "Prova2", content = "content prova2"),
            Post(id = 3, title = "Prova3", content = "content prova3"),
        )

        postDao.insert(posts[0])
        postDao.insert(posts[1])
        postDao.insert(posts[2])

        postDao.getPosts().test {
            val result = this.awaitItem()
            assertTrue(result.size==3)
        }
    }


    @Test
    fun updatePost() = runTest {
        val post = Post(
            id = 1, title = "Prova", content = "content prova"
        )

        postDao.insert(post)

        post.title = "Provo a modificare il titolo"
        postDao.update(post)

        postDao.getPostById(post.id).test {
            val result = this.awaitItem()
            assertTrue(result.title=="Provo a modificare il titolo")
        }
    }


    @Test
    fun deletePost() = runTest {
        val post = Post(
            id = 1, title = "Prova", content = "content prova"
        )

        postDao.insert(post)
        postDao.delete(post)

        postDao.getPosts().test {
            val result = this.awaitItem()
            assertTrue(result.isEmpty())
        }
    }


    @Test
    fun deleteAllPosts() = runTest {
        val posts = listOf(
            Post(id = 1, title = "Prova", content = "content prova"),
            Post(id = 2, title = "Prova2", content = "content prova2"),
            Post(id = 3, title = "Prova3", content = "content prova3"),
        )

        postDao.insert(posts[0])
        postDao.insert(posts[1])
        postDao.insert(posts[2])

        postDao.deleteAll()

        postDao.getPosts().test {
            val result = this.awaitItem()
            assertTrue(result.isEmpty())
        }
    }


}