package ru.netology.nmedia.viewmodel

import androidx.lifecycle.ViewModel
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryInMemoy

class PostViewModel: ViewModel() {
    private val repository: PostRepository = PostRepositoryInMemoy()

    val data = repository.getAll()

    fun likeById(id: Int){
        repository.likeById(id)
    }

    fun repostById(id: Int){
        repository.repostById(id)
    }
}