package com.victor_sml.playlistmaker.library.ui.view

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment.DIRECTORY_PICTURES
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.victor_sml.playlistmaker.R
import com.victor_sml.playlistmaker.common.Constants.BIG_ARTWORK_RADIUS_DP
import com.victor_sml.playlistmaker.common.models.Playlist
import com.victor_sml.playlistmaker.common.ui.BindingFragment
import com.victor_sml.playlistmaker.common.utils.Transliterator.toLatin
import com.victor_sml.playlistmaker.common.utils.Utils.dpToPx
import com.victor_sml.playlistmaker.common.utils.Utils.toUnique
import com.victor_sml.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.victor_sml.playlistmaker.library.ui.stateholders.NewPlaylistViewModel
import com.victor_sml.playlistmaker.library.ui.stateholders.PlaylistCreationState
import com.victor_sml.playlistmaker.library.ui.stateholders.PlaylistCreationState.Fail
import com.victor_sml.playlistmaker.library.ui.stateholders.PlaylistCreationState.Success
import java.io.File
import java.io.FileOutputStream
import org.koin.androidx.viewmodel.ext.android.viewModel

class NewPlaylistFragment : BindingFragment<FragmentNewPlaylistBinding>() {
    private val viewModel by viewModel<NewPlaylistViewModel>()

    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
    private var imageFileUri: Uri? = null
    private var title: String = ""
    private var description: String = ""

    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            processBackPress()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(this, backPressedCallback)

        pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                val imageCornerRadius = dpToPx(BIG_ARTWORK_RADIUS_DP, requireContext())

                Glide.with(this)
                    .load(uri)
                    .placeholder(R.drawable.default_artwork)
                    .transform(CenterCrop(), RoundedCorners(imageCornerRadius))
                    .into(binding.ivPlaylistCover)

                imageFileUri = uri
            }
        }
    }

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentNewPlaylistBinding =
        FragmentNewPlaylistBinding.inflate(inflater, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()

        viewModel.getCreationState().observe(viewLifecycleOwner) { state ->
            processCreationState(state)
        }
    }

    private fun setListeners() {
        binding.tiPlaylistTitle.doOnTextChanged { text, _, _, _ ->
            binding.apply {
                btnNewPlaylist.isEnabled = !text.isNullOrEmpty()
                tilPlaylistTitle.inputTextChangeHandler(text)
                title = text.toString()
            }
        }

        binding.tiPlaylistDescription.doOnTextChanged { text, _, _, _ ->
            binding.apply {
                tilPlaylistDescription.inputTextChangeHandler(text)
                description = text.toString()
            }
        }

        binding.ivPlaylistCover.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.btnNewPlaylist.setOnClickListener {
            savePlaylist(createPlaylist())
        }
    }

    private fun TextInputLayout.inputTextChangeHandler(text: CharSequence?) {
        if (text.isNullOrEmpty()) this.setInputStrokeColor(EMPTY_INPUT_STROKE_COLOR_ID)
        else this.setInputStrokeColor(FILLED_IN_INPUT_STROKE_COLOR_ID)
    }

    private fun TextInputLayout.setInputStrokeColor(colorStateListId: Int) {
        this.defaultHintTextColor =
            resources.getColorStateList(colorStateListId, null)

        this.setBoxStrokeColorStateList(
            resources.getColorStateList(colorStateListId, null)
        )
    }

    private fun processCreationState(state: PlaylistCreationState) {
        when (state) {
            is Success -> {
                showMessage(state.message)
                findNavController().popBackStack()
            }
            is Fail ->
                showMessage(state.message)
        }
    }

    private fun getImageFileName(title: String, prefix: String, extension: String): String =
        prefix + title.lowercase().toLatin() + extension

    private fun getImageDirectoryPath(): File {
        val imageDirectoryPath = File(
            requireContext().getExternalFilesDir(DIRECTORY_PICTURES),
            IMAGE_DIRECTORY
        )

        if (!imageDirectoryPath.exists()) imageDirectoryPath.mkdirs()
        return imageDirectoryPath
    }

    private fun getImageFile(title: String): File {
        val fileName = getImageFileName(title, IMAGE_FILE_PREFIX, IMAGE_FILE_EXTENSION)
        val file = File(getImageDirectoryPath(), fileName)

        return if (file.exists()) file.toUnique()
        else file
    }

    private fun saveImageToPrivateStorage(uri: Uri, file: File): String? {
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)

        val isImageSaved = BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)

        return if (isImageSaved) file.path
        else null
    }

    private fun createPlaylist(): Playlist {
        val imagePath = imageFileUri?.let { imageFileUri ->
            saveImageToPrivateStorage(imageFileUri, getImageFile(title))
        }
        return Playlist(0, title, imagePath, description)
    }

    private fun savePlaylist(playlist: Playlist) {
        viewModel.addPlaylist(playlist)
    }

    private fun processBackPress() {
        if (title.isNotEmpty() || description.isNotEmpty() || imageFileUri != null) {
            showDialog()
        } else findNavController().popBackStack()
    }

    private fun showDialog() {
        val title = resources.getString(R.string.new_playlist_dialog_title)
        val message = resources.getString(R.string.new_playlist_dialog_body)
        val cancellation = resources.getString(R.string.cancellation)
        val complete = resources.getString(R.string.complete)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setNeutralButton(cancellation) { _, _ ->
            }
            .setPositiveButton(complete) { _, _ ->
                findNavController().popBackStack()
            }
            .show()
    }

    private fun showMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val IMAGE_DIRECTORY = "playlist_covers"
        const val IMAGE_FILE_PREFIX = "playlist_cover_"
        const val IMAGE_FILE_EXTENSION = ".jpg"

        const val FILLED_IN_INPUT_STROKE_COLOR_ID = R.color.filled_in_outlined_input_stroke_color
        const val EMPTY_INPUT_STROKE_COLOR_ID = R.color.empty_outlined_input_stroke_color
    }
}