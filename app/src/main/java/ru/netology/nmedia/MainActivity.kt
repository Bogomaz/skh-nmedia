package ru.netology.nmedia

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.viewmodel.PostViewModel
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.adapter.PostListener
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.model.Post
import ru.netology.nmedia.utils.AndroidUtils

class MainActivity : AppCompatActivity() {

    val viewModel: PostViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Создаём адаптер, передаём ему объект PostListener
        //Переопределяем все методы  так, чтобы в них вызывались функции их вьюшки
        val adapter = PostsAdapter(object : PostListener {
            override fun onEdit(post: Post) {
                viewModel.edit(post)
            }

            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)
            }

            override fun onLike(post: Post) {
                viewModel.likeById(post.id)
            }

            override fun onRepost(post: Post) {
                viewModel.repostById(post.id)
            }
        })
        binding.postList.adapter = adapter // созданный адаптер помещаем в Recycler View с постами

        // Переводит ленту вверх, чтобы пользователь сразу
        // видел добавленный пост
        adapter.registerAdapterDataObserver(
            object : RecyclerView.AdapterDataObserver() {
                override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                    if (positionStart == 0) {
                        binding.postList.smoothScrollToPosition(0)
                    }
                }
            }
        )

        viewModel.data.observe(this)
        { posts ->
            adapter.submitList(posts)
        }

        // Подписываемся на изменения редактируемого поста
        viewModel.edited.observe(this)
        { post ->
            binding.apply {
                if (post.id > 0) {
                    //Режим редактирования: показываем блок, заполняем текст,
                    // ставим фокус и открываем клавиатуру
                    newText.setText(post.text)
                    editedGroup.visibility = View.VISIBLE
                    editedPostTitle.text = post.text
                    newText.requestFocus()
                    AndroidUtils.showKeyboard(binding.newText)
                }
                // Режим отмены: скрываем блок, очищаем поле и убираем фокус
                else {
                    editedGroup.visibility = View.GONE
                    newText.setText("")
                    newText.clearFocus()
                }
            }
        }


        // Обработчик кнопки Отменить редактирование
        binding.cancelEdit.setOnClickListener() {
            viewModel.cancelEdit()
        }

        //Обработчик кнопки Сохранить
        binding.save.setOnClickListener()
        {
            with(binding.newText) {
                val newText = text.toString()
                if (newText.isBlank()) {
                    Toast.makeText(context, R.string.empty_post_text, Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                viewModel.save(newText)
                AndroidUtils.hideKeyboard(this)
            }
        }
    }
}

