package ru.netology.nmedia.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.model.Post
import ru.netology.nmedia.service.DateTimeService
import ru.netology.nmedia.service.PostService


typealias OnLikeListener = (Post) -> Unit
typealias OnRepostListener = (Post) -> Unit
typealias OnRemoveById = (Post) -> Unit

// Интерфейс PostListener содержит все методы, которые позволяют манипулировать постом в ленте
interface PostListener {
    fun onEdit(post: Post)
    fun onRemove(post: Post)
    fun onLike(post: Post)
    fun onRepost(post: Post)
}

@RequiresApi(Build.VERSION_CODES.O)
class PostsAdapter(
    private val listener: PostListener,
) : ListAdapter<Post, PostViewHolder>(
    PostDiffUtils
) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PostViewHolder {
        val view = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(view, listener)
    }

    override fun onBindViewHolder(
        holder: PostViewHolder,
        position: Int
    ) {
        val post = getItem(position)
        holder.bind(post)
    }
}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val listener: PostListener
) : RecyclerView.ViewHolder(binding.root) {

    @RequiresApi(Build.VERSION_CODES.O)
    fun bind(post: Post) {
        binding.apply {
            author.text = post.author
            avatar.setImageResource(R.drawable.avatar)
            published.text = DateTimeService.formatUnixTime(post.date)
            content.text = post.text
            likesCount.text = PostService.convertNumberIntoText(post.likesCount)
            viewsCount.text = PostService.convertNumberIntoText(post.viewsCount)
            commentsCount.text = PostService.convertNumberIntoText(post.commentsCount)
            sharesCount.text = PostService.convertNumberIntoText(post.repostsCount)
            likes.setImageResource(
                selectImageResource(post.isLiked)
            )
            likes.setOnClickListener {
                listener.onLike(post)
            }

            shares.setOnClickListener {
                listener.onRepost(post)
            }
            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.post_menu)
                    setOnMenuItemClickListener { menuItem ->
                        when (menuItem.itemId) {
                            R.id.remove -> {
                                listener.onRemove(post)
                                true
                            }

                            R.id.edit -> {
                                listener.onEdit(post)
                                true
                            }

                            else -> false
                        }
                    }
                    show()
                }
            }
        }
    }

    // Определяет, какая иконка будет выводиться.
    fun selectImageResource(isLiked: Boolean): Int {
        return when (isLiked) {
            true -> R.drawable.ic_like_filled
            false -> R.drawable.ic_like
        }
    }
}

object PostDiffUtils : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(
        oldItem: Post,
        newItem: Post
    ) = oldItem.id == newItem.id

    override fun areContentsTheSame(
        oldItem: Post,
        newItem: Post
    ) = oldItem == newItem

}