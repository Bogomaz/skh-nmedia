package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.model.Post
import ru.netology.nmedia.service.PostService

class PostRepositorySQLiteImpl(
    private val dao: PostDao
) : PostRepository {
    private var posts = emptyList<Post>()
    private val data = MutableLiveData(posts)

    init {
        posts = dao.getAll()
        data.value = posts
    }

    override fun getAll(): LiveData<List<Post>> = data

    override fun save(post: Post) {
        val id = post.id
        val saved = dao.save(post)
        posts = if (id == 0) {
            listOf(saved) + posts
        } else {
            posts.map {
                if (it.id != id) it else saved
            }
        }
        data.value = posts
    }

    override fun likeById(id: Int) {
        dao.likeById(id)
        posts = posts.map {
            if (it.id != id) it else it.copy(
                isLiked = !it.isLiked,
                likesCount = if (it.isLiked) it.likesCount - 1 else it.likesCount + 1
            )
        }
        data.value = posts
    }

    override fun repost(parentId: Int, text: String) {
        val parentPost = posts.firstOrNull{ it.id == parentId } ?: return

        val newPost = parentPost.copy(
            id = 0,
            parentId = parentId,
            text = text,
            likesCount = 0,
            commentsCount = 0,
            repostsCount = 0,
            isLiked = false,
            viewsCount = 0,
        )

        val inserted = dao.save(newPost)
        dao.incrementRepostCount(parentId)

        posts = posts
            .map { post ->
                if (post.id != parentId) post
                else post.copy(repostsCount = post.repostsCount + 1)
            }
            .let { updated ->
                listOf(inserted) + updated
            }

        data.value = posts
    }

    override fun removeById(id: Int) {
        dao.removeById(id)
        posts = posts.filter { it.id != id }
        data.value = posts
    }
}