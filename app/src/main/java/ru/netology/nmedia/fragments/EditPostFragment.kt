package ru.netology.nmedia.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentEditPostBinding
import ru.netology.nmedia.utils.AndroidUtils
import ru.netology.nmedia.viewmodel.PostViewModel
import ru.netology.nmedia.utils.postText

class EditPostFragment : Fragment() {
    private val args: EditPostFragmentArgs by navArgs()
    private val postText: String by lazy { args.postText }
    private var _binding: FragmentEditPostBinding? = null // Nullable-ссылка на binding
    private val binding get() = _binding!! // кастомный геттер, который точно возвращает не null
    private val viewModel: PostViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val postText = arguments?.postText.orEmpty()
        val isEdit = postText.isNotBlank()


        if (isEdit) {
            binding.newText.setText(postText)
            AndroidUtils.showKeyboard(binding.newText)
        }

        binding.topAppBar.title = getString(
            if (isEdit) R.string.edited_post_title else R.string.edited_post_title
        )

        binding.savePost.setOnClickListener {
            val text = binding.newText.text?.toString().orEmpty()
            if (text.isNotBlank()) {
                viewModel.save(text)
                findNavController().navigateUp()
            }
        }

        binding.cancelEdit.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // очистить binding в конце жизни фрагмента
    }

}