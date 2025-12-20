package ru.netology.nmedia.dao

import ru.netology.nmedia.model.Post

interface PostDao {
    fun getAll(): List<Post>
    fun save(post: Post): Post
    fun likeById(id: Int)
    fun removeById(id: Int)

    fun incrementRepostCount(id:Int)
}