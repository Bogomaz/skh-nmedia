package ru.netology.nmedia.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.databinding.FragmentNewPostBinding
import ru.netology.nmedia.utils.StringArg
import ru.netology.nmedia.viewmodel.PostViewModel

class NewPostFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentNewPostBinding.inflate(layoutInflater, container, false)
        val viewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)

        arguments?.textArg?.let(binding.newText :: setText)

        //Обработчик сохранения
        binding.savePost.setOnClickListener {
            // Если пытаются сохранить пустой текст,
            // то возвращается RESULT_CANCELED
            if (!binding.newText.text.isNullOrBlank()) {
                viewModel.save(binding.newText.text.toString())
                findNavController().navigateUp()
            }
        }
//        // Добавить крестик на панель управления.
//        binding.topAppBar.setNavigationIcon(R.drawable.ic_close_editing)
//        binding.topAppBar.setNavigationOnClickListener {
//            setResult(RESULT_CANCELED)
//            finish()
//        }
//
//        // Получаем из интента текст поста.
//        val postText = intent.getStringExtra(Intent.EXTRA_TEXT)
//        // Если текст есть, то это режим редактирования поста
//        // Ставим соотв. заголовок и заполняем поле текстом поста.
//        if (postText != null) {
//            binding.topAppBar.title = getString(R.string.edited_post_title)
//            binding.newText.setText(postText)
//        }
//        // Если текста нет, то это режим создания поста.
//        // Ставим соотв. заголовок и поле ввода текста оставляем пустым
//        else {
//            binding.topAppBar.title = getString(R.string.created_post_title)
//        }
//
//        AndroidUtils.showKeyboard(binding.newText)
        return binding.root
    }

    companion object {
        var Bundle.textArg: String? by StringArg
    }
}