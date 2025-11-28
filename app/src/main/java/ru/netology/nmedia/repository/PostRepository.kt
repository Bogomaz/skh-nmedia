package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.model.Post

interface PostRepository  {
    fun getAll(): LiveData<List<Post>>
    fun likeById(id: Int)

    fun repostById(id: Int)

}