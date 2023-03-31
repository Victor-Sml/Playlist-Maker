package com.victor_sml.playlistmaker

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.view.OrientationEventListener
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.victor_sml.playlistmaker.databinding.ActivityPlayerBinding
import com.victor_sml.playlistmaker.utils.dpToPx
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerBinding
    private var track: Track? = null
    private var handler = Handler(Looper.getMainLooper())
    private var mediaPlayer = MediaPlayer()
    private var playerState = PlayerState.STATE_DEFAULT
    private var playerProgress: Int = 0
    private var isInterrupted: Boolean = false // Указывает проигрывался ли трек во время вызова onPause().
    private var isNotified: Boolean = false // Указывает было ли показанно пользователю сообщение о невозможности воспроизвести трек.

    private val playbackProgress: Runnable = object : Runnable {
        override fun run() {
            setPlaybackProgress(mediaPlayer.currentPosition)
            handler.postDelayed(this, PLAYBACK_PROGRESS_DELAY)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val orientationListener = object : OrientationEventListener(applicationContext) {
            override fun onOrientationChanged(p0: Int) {
                isInterrupted = savedInstanceState?.getBoolean(IS_INTERRUPTED) ?: isInterrupted
                this.disable()
            }
        }
        orientationListener.enable()

        track = intent.getParcelableExtra(TRACK_FOR_PLAYER)

        initViews()
        initPlayerDetails(savedInstanceState)
        preparePlayer()
        setListeners()
    }

    override fun onPause() {
        super.onPause()
        if (playerState == PlayerState.STATE_STARTED) {
            pausePlayer()
            isInterrupted = true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.apply {
            putParcelable(PLAYER_STATE, playerState)
            putInt(PLAYBACK_PROGRESS, playerProgress)
            putBoolean(IS_NOTIFIED, isNotified)
            putBoolean(IS_INTERRUPTED, isInterrupted)
        }
    }

    private fun initViews() {
        val artworkCornerRadius = dpToPx(BIG_ARTWORK_RADIUS, this)

        Glide.with(this).load(track?.getCoverArtwork())
            .placeholder(R.drawable.default_artwork)
            .centerCrop()
            .transform(RoundedCorners(artworkCornerRadius))
            .into(binding.ivArtwork)

        // Заменяем пустые поля из track на "n/a"
        val setValue: (String?) -> String = {
            it ?: getString(R.string.not_applicable)
        }

        binding.run {
            tvTrackName.text = setValue(track?.trackName)
            tvArtistName.text = setValue(track?.artistName)
            tvTrackTimeValue.text = setValue(track?.trackTime)
            tvAlbumValue.text = setValue(track?.collectionName)
            tvReleaseDataValue.text = setValue(track?.releaseDate)
            tvGenreValue.text = setValue(track?.primaryGenreName)
            tvCountryValue.text = setValue(track?.country)
        }
    }

    private fun initPlayerDetails(savedInstanceState: Bundle?) {
        playerProgress = savedInstanceState?.getInt(PLAYBACK_PROGRESS) ?: 0
        playerState =
            savedInstanceState?.getParcelable("PLAYBACK_PROGRESS") ?: PlayerState.STATE_DEFAULT
        isNotified = savedInstanceState?.getBoolean(IS_NOTIFIED) ?: isNotified
    }

    private fun setListeners() {
        binding.fabPlaybackControl.setOnClickListener {
            playbackControl()
        }

        binding.libraryToolbar.setNavigationOnClickListener {
            this.finish()
        }
    }

    fun setPlaybackProgress(progress: Int): Int {
        binding.tvPlaybackProgress.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(progress)
        return progress
    }

    private fun preparePlayer() {
        // При отсутствии previewUrl в track, сообщаем пользователю, что трек не доступен.
        val showMessage = {
            if (!isNotified) {
                Toast.makeText(this, getString(R.string.nothing_to_play_toast), Toast.LENGTH_LONG)
                    .show()
                isNotified = true
            }
        }

        val prepare: (String) -> Unit = {
            mediaPlayer.setDataSource(it)
            mediaPlayer.prepareAsync()
            setPlaybackProgress(playerProgress)

            mediaPlayer.setOnPreparedListener {
                binding.fabPlaybackControl.isEnabled = true
                playerState = PlayerState.STATE_PREPARED
                mediaPlayer.seekTo(playerProgress)
                //Если в момент смены ориентации экрана проигрывался трек, то возобновляем его воспроизведение.
                if (isInterrupted) {
                    playbackControl()
                }
            }

            mediaPlayer.setOnCompletionListener {
                handler.removeCallbacks(playbackProgress)
                playerState = PlayerState.STATE_PREPARED
                binding.fabPlaybackControl.setImageDrawable(getDrawable(R.drawable.ic_play))
                playerProgress = setPlaybackProgress(DEFAULT_PLAYBACK_PROGRESS)
            }
        }

        track?.previewUrl?.let { prepare(it) } ?: showMessage()
    }

    private fun startPlayer() {
        mediaPlayer.start()
        binding.fabPlaybackControl.setImageDrawable(getDrawable(R.drawable.ic_pause))
        playbackProgress.run()
        playerState = PlayerState.STATE_STARTED
        isInterrupted = false
    }

    private fun pausePlayer() {
        handler.removeCallbacks(playbackProgress)
        mediaPlayer.pause()
        binding.fabPlaybackControl.setImageDrawable(getDrawable(R.drawable.ic_play))
        playerState = PlayerState.STATE_PAUSED
        playerProgress = mediaPlayer.currentPosition
    }

    private fun playbackControl() {
        when (playerState) {
            PlayerState.STATE_PREPARED, PlayerState.STATE_PAUSED -> startPlayer()
            PlayerState.STATE_STARTED -> pausePlayer()
            else -> {}
        }
    }

    @Parcelize
    private enum class PlayerState : Parcelable {
        STATE_DEFAULT,
        STATE_PREPARED,
        STATE_STARTED,
        STATE_PAUSED
    }

    companion object {
        private const val DEFAULT_PLAYBACK_PROGRESS = 0
        private const val PLAYBACK_PROGRESS_DELAY = 300L
        private const val PLAYER_STATE = "player state"
        private const val PLAYBACK_PROGRESS = "playback progress"
        private const val IS_NOTIFIED = "isNotified"
        private const val IS_INTERRUPTED = "isInterrupted"
        const val BIG_ARTWORK_RADIUS = 8
    }
}