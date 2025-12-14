package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.netology.nmedia.model.Post
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryInMemory

private val emptyPost = Post(
    date = (System.currentTimeMillis() / 1000).toInt(),
    author = "Студент Нетологии",
    text = "",
    commentsCount = 0,
    likesCount = 0,
    viewsCount = 0,
    repostsCount = 0
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository = PostRepositoryInMemory(application)

    val data = repository.getAll()
    val edited = MutableLiveData(emptyPost)
    fun likeById(id: Int) {
        repository.likeById(id)
    }

    fun repostById(id: Int) {
        repository.repostById(id)
    }

    fun save(newText: String) {
        edited.value?.let { post ->
            val trimmedText = newText.trim()
            if (trimmedText != post.text) {
                repository.save(
                    post.copy(text = trimmedText)
                )
                edited.value = emptyPost
            }
        }
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun removeById(id: Int) {
        repository.removeById(id)
    }
}