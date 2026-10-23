
package com.openplay.media

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.PlaybackException
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

class MainActivity : Activity() {

    private var player: ExoPlayer? = null
    private lateinit var playerView: PlayerView
    private lateinit var status: TextView
    private lateinit var playButton: Button

    private val filePicker =
        registerForActivityResult(
            ActivityResultContracts.OpenDocument()
        ) { uri ->
            if (uri != null) {
                try {
                    player?.setMediaItem(MediaItem.fromUri(uri))
                    player?.prepare()
                    player?.play()
                    status.text = "Playing selected media"
                    playButton.text = "Pause"
                } catch (e: Exception) {
                    status.text = "Unable to open this file"
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = Color.rgb(18, 18, 18)
        window.navigationBarColor = Color.rgb(18, 18, 18)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 24, 20, 20)
            setBackgroundColor(Color.rgb(18, 18, 18))
        }

        val title = TextView(this).apply {
            text = "OPENPLAY"
            textSize = 25f
            setTextColor(Color.rgb(120, 230, 150))
            gravity = Gravity.CENTER
        }
        root.addView(title)

        status = TextView(this).apply {
            text = "Choose an audio or video file"
            textSize = 15f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            setPadding(8, 20, 8, 20)
        }
        root.addView(status)

        playerView = PlayerView(this)
        root.addView(
            playerView,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        val openButton = Button(this).apply {
            text = "Open Media File"
            setOnClickListener {
                filePicker.launch(arrayOf("*/*"))
            }
        }
        root.addView(openButton)

        playButton = Button(this).apply {
            text = "Pause"
            setOnClickListener {
                val p = player ?: return@setOnClickListener
                if (p.isPlaying) {
                    p.pause()
                    text = "Play"
                } else {
                    p.play()
                    text = "Pause"
                }
            }
        }
        root.addView(playButton)

        setContentView(root)

        player = ExoPlayer.Builder(this).build()
        playerView.player = player

        player?.addListener(object : Player.Listener {
            override fun onPlayerError(
                error: PlaybackException
            ) {
                status.text =
                    "Cannot play this format on this device"
            }
        })
    }

    override fun onStop() {
        super.onStop()
        player?.release()
        player = null
    }
}
