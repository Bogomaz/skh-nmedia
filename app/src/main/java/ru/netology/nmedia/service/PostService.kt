package ru.netology.nmedia.service

import ru.netology.nmedia.model.Post

object PostService {
    var posts = mutableListOf<Post>()
    private var currentPostId = 1

    //Принимает объект Post
    //Добавляет пост
    //Возвращает только что добавленный пост
    fun addOnePost(post: Post): Post {
        val newPost = post.copy(id = currentPostId++)
        posts += newPost
        return posts.last();
    }

    //Принимает список постов
    //Добавляет эти посты
    //Возвращает количество добавленных постов.
    fun addPostList(newPostsList: List<Post>): List<Post> {
        val postsToAdd = newPostsList.map { post ->
            post.copy(id = currentPostId++)
        }
        posts += postsToAdd
        return postsToAdd;
    }

    //Принимает id поста
    //Возвращает запрошенный пост, или генерит исключение
    fun getById(postId: Int): Post {
        val post = posts.firstOrNull() { it.id == postId }
        if (post == null) {
            throw RuntimeException("The post $postId doesn't exist")
        }
        return post;
    }

    //Принимает объект Post
    //Находит в массиве запись с тем же id, что и у post и обновляет все свойства;
    //Если пост с таким id не найден, то ничего не происходит и возвращается false, в противном случае – возвращается true.
    fun update(post: Post): Post {
        val index = posts.indexOfFirst{it.id == post.id}
        return if(index == -1){
            throw RuntimeException("The post ${post.id} doesn't exist")
        }else{
            posts[index] = post
            posts[index]
        }
    }

    //Принимает пост по id
    //Возвращает true, если удаление прошло успешно
    fun removeById(postId: Int): Boolean {
        val removed = posts.removeIf { it.id == postId }
        if (!removed) {
            throw RuntimeException("The post $postId doesn't exist")
        }
        return removed
    }


    fun clear() {
        posts.clear()
        currentPostId = 1
    }

    // Принимает id поста.
    // Изменяет лайк и меняет состояние "лайкнутости"
    // Возвращает новое количество лайков
    fun likeHandler(postId: Int): Post {
        val post = getById(postId)
        val updatedPost = post.copy(
            likesCount = if (post.isLiked) post.likesCount - 1 else post.likesCount + 1,
            isLiked = !post.isLiked
        )
        update(updatedPost)
        return updatedPost
    }

    // Принимает id пользователя и id поста.
    // Cтавит отметку о том, что данный пользователь поставил/снял лайк
    // Возвращает новое количество лайков
    fun repostHandler(postId: Int): Post {
        val post = getById(postId)
        val updatedPost = post.copy(
            repostsCount = post.repostsCount + 1,
        )
        update(updatedPost)
        return updatedPost
    }

    //Принимает число с количеством взаимодействий с постом
    //Возвращает строку, пригодную для вывода.
    fun convertNumberIntoText(number: Int): String {
        return when {
            number >= 1_000_000 -> formatNumberWithSuffix(number / 1_000_000.0, "M")
            number >= 10_000 -> "${number / 1_000} K"
            number >= 1_000 -> formatNumberWithSuffix(number / 1_000.0, "K")
            else -> number.toString()
        }
    }

    // Принимает число и суффикс, указывающий на размерность числа
    // Возвращает строку с нужным числом знаков после запятой и заданным суффиксом
    private fun formatNumberWithSuffix(value: Double, suffix: String): String {
        // Отрезать лишние цифры без округления
        val truncated = (value * 10).toInt() / 10.0
        val formatted = "%.1f".format(truncated).replace(",", ".")

        // Убрать .0, если число целое
        return if (formatted.endsWith(".0")) {
            formatted.dropLast(2) + " $suffix"
        } else {
            "$formatted $suffix"
        }
    }
}