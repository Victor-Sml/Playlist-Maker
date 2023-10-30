package com.victor_sml.playlistmaker.library.playlistEditor.ui.view

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment.DIRECTORY_PICTURES
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.victor_sml.playlistmaker.R
import com.victor_sml.playlistmaker.common.Constants.BIG_ARTWORK_RADIUS_DP
import com.victor_sml.playlistmaker.common.domain.models.Playlist
import com.victor_sml.playlistmaker.common.ui.nonBottomNavFragment.NonBottomNavFragmentImpl
import com.victor_sml.playlistmaker.common.utils.Transliterator.toLatin
import com.victor_sml.playlistmaker.common.utils.Utils.dpToPx
import com.victor_sml.playlistmaker.common.utils.Utils.toUnique
import com.victor_sml.playlistmaker.common.utils.debounce
import com.victor_sml.playlistmaker.databinding.FragmentPlaylistEditorBinding
import com.victor_sml.playlistmaker.library.playlistEditor.ui.stateholders.PlaylistCreationState
import com.victor_sml.playlistmaker.library.playlistEditor.ui.stateholders.PlaylistCreationState.Fail
import com.victor_sml.playlistmaker.library.playlistEditor.ui.stateholders.PlaylistCreationState.Success
import com.victor_sml.playlistmaker.library.playlistEditor.ui.stateholders.PlaylistEditorViewModel
import java.io.File
import java.io.FileOutputStream
import kotlin.concurrent.thread
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistEditorFragment : NonBottomNavFragmentImpl<FragmentPlaylistEditorBinding>() {
    private val viewModel by viewModel<PlaylistEditorViewModel>()

    private var title: String = ""
    private var description: String = ""
    private var imageFileUri: Uri? = null

    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var onSaveClickDebounce: (Unit) -> Unit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setOnBackPressedCallback()
        initPickMedia()
    }

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentPlaylistEditorBinding =
        FragmentPlaylistEditorBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeCreationState()
        initializeUI()
    }

    private fun observeCreationState() {
        viewModel.getCreationState().observe(viewLifecycleOwner) { state ->
            processCreationState(state)
        }
    }

    private fun initializeUI() {
        applyWindowInsets()
        setDebounce()
        setListeners()
    }
    private fun applyWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.llRoot) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

            view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = insets.top
                leftMargin = insets.left
                rightMargin = insets.right
                bottomMargin = insets.bottom
            }

            windowInsets
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.svEditArea) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.ime())

            view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = insets.top
                leftMargin = insets.left
                rightMargin = insets.right
                bottomMargin = insets.bottom
            }

            WindowInsetsCompat.CONSUMED
        }
    }

    private fun setDebounce() {
        onSaveClickDebounce =
            debounce(DEBOUNCE_DELAY_MILLIS, viewLifecycleOwner.lifecycleScope) {
                val fileSizeMB = imageFileUri?.getFileSizeMB() ?: 0f

                if (fileSizeMB > LOAD_INDICATION_FILE_SIZE_MB)
                    binding.progressIndicator.isVisible = true

                thread { savePlaylist(createPlaylist()) }
            }
    }

    private fun setListeners() {
        binding.tiPlaylistTitle.doOnTextChanged { text, _, _, _ ->
            with(binding) {
                btnSavePlaylist.isEnabled = !text.isNullOrEmpty()
                tilPlaylistTitle.processInputTextChange(text)
            }
            title = text.toString()
        }

        binding.tiPlaylistDescription.doOnTextChanged { text, _, _, _ ->
            binding.tilPlaylistDescription.processInputTextChange(text)
            description = text.toString()
        }

        binding.tiPlaylistDescription.setOnFocusChangeListener { view, isFocused ->
            view as TextInputEditText
            if (isFocused) view.setSelection(view.length())
        }

        binding.ivPlaylistCover.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.btnSavePlaylist.setOnClickListener {
            onSaveClickDebounce(Unit)
        }

        binding.tbPlaylistEditor.setNavigationOnClickListener {
            processBackPress()
        }
    }

    private fun initPickMedia() {
        pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                binding.ivPlaylistCover.setPlaylistCover(uri)
                imageFileUri = uri
            }
        }
    }

    private fun ImageView.setPlaylistCover(imageUri: Uri) {
        Glide.with(this)
            .load(imageUri)
            .placeholder(R.drawable.default_artwork)
            .transform(CenterCrop(), RoundedCorners(BIG_ARTWORK_RADIUS_DP.dpToPx()))
            .into(this)
    }

    private fun setOnBackPressedCallback() {
        val backPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                processBackPress()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(this, backPressedCallback)
    }

    private fun processBackPress() {
        if (title.isNotEmpty() || description.isNotEmpty() || imageFileUri != null) {
            showDialog()
        } else findNavController().popBackStack()
    }

    private fun TextInputLayout.processInputTextChange(text: CharSequence?) {
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

    private fun createPlaylist(): Playlist {
        val imagePath = imageFileUri?.let { imageFileUri ->
            saveImageToPrivateStorage(imageFileUri, getImageFile(title))
        }

        return Playlist(0, title, imagePath, description, DEFAULT_NUMBER_OF_TRACKS)
    }

    private fun savePlaylist(playlist: Playlist) {
        viewModel.addPlaylist(playlist)
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

    private fun getImageFile(title: String): File {
        val fileName = getImageFileName(title)
        val file = File(getImageDirectoryPath(), fileName)

        return if (file.exists()) file.toUnique()
        else file
    }

    private fun getImageFileName(title: String): String =
        IMAGE_FILE_PREFIX + title.lowercase().toLatin() + IMAGE_FILE_EXTENSION

    private fun getImageDirectoryPath(): File {
        val imageDirectoryPath = File(
            requireContext().getExternalFilesDir(DIRECTORY_PICTURES),
            IMAGE_DIRECTORY
        )

        if (!imageDirectoryPath.exists()) imageDirectoryPath.mkdirs()
        return imageDirectoryPath
    }

    private fun processCreationState(state: PlaylistCreationState) {
        when (state) {
            is Success -> {
                binding.progressIndicator.isVisible = false
                showMessage(state.message)
                findNavController().popBackStack()
            }

            is Fail -> {
                binding.progressIndicator.isVisible = false
                showMessage(state.message)
            }
        }
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
        Snackbar.make(binding.llRoot, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun Uri.getFileSizeMB(): Float {
        requireContext().contentResolver.query(this, null, null, null, null)?.use { cursor ->
            val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
            cursor.moveToFirst()

            return cursor.getLong(sizeIndex) / (1024f * 1024f)
        }
        return 0f
    }

    companion object {
        const val IMAGE_DIRECTORY = "playlist_covers"
        const val IMAGE_FILE_PREFIX = "playlist_cover_"
        const val IMAGE_FILE_EXTENSION = ".jpg"

        const val FILLED_IN_INPUT_STROKE_COLOR_ID = R.color.filled_in_outlined_input_stroke_color
        const val EMPTY_INPUT_STROKE_COLOR_ID = R.color.empty_outlined_input_stroke_color

        const val DEFAULT_NUMBER_OF_TRACKS = 0

        const val DEBOUNCE_DELAY_MILLIS = 2000L

        const val LOAD_INDICATION_FILE_SIZE_MB = 2
    }
}