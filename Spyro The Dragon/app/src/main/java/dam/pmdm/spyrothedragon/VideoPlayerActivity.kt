package dam.pmdm.spyrothedragon

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.MediaController
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import dam.pmdm.spyrothedragon.databinding.ActivityVideoPlayerBinding

class VideoPlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVideoPlayerBinding
    private var videoLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        binding = ActivityVideoPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val resId = resources.getIdentifier("videospyro", "raw", packageName)

        if (resId == 0) {
            Toast.makeText(this, getString(R.string.easter_egg_no_video), Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val mediaController = MediaController(this)
        mediaController.setAnchorView(binding.videoView)
        binding.videoView.setMediaController(mediaController)

        val videoUri = Uri.parse("android.resource://$packageName/$resId")
        binding.videoView.setVideoURI(videoUri)

        binding.videoView.setOnPreparedListener { mp ->
            mp.isLooping = false
            binding.videoView.start()
            binding.progressBar.visibility = View.GONE
            videoLoaded = true
        }

        binding.videoView.setOnCompletionListener {
            finish()
        }

        binding.videoView.setOnErrorListener { _, _, _ ->
            Toast.makeText(this, getString(R.string.easter_egg_video_error), Toast.LENGTH_SHORT).show()
            finish()
            true
        }

        binding.btnClose.setOnClickListener { finish() }
    }

    override fun onPause() {
        super.onPause()
        if (videoLoaded) binding.videoView.pause()
    }

    override fun onResume() {
        super.onResume()
        if (videoLoaded) binding.videoView.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.videoView.stopPlayback()
    }
}
