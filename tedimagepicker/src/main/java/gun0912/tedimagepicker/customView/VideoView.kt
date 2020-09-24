package gun0912.tedimagepicker.customView

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import gun0912.tedimagepicker.R
import kotlinx.android.synthetic.main.video_view.view.*

class VideoView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private lateinit var player: SimpleExoPlayer
    private lateinit var mediaDataSourceFactory: ProgressiveMediaSource.Factory
    private var mUri: Uri? = null
    private var currentPos = 0L

    init {
        View.inflate(context, R.layout.video_view, this)
        initializePlayer()
    }

    private fun initializePlayer() {
        player = SimpleExoPlayer.Builder(context).build()
        val userAgent = Util.getUserAgent(playerView.context, playerView.context.getString(R.string.app_name))
        mediaDataSourceFactory = ProgressiveMediaSource
            .Factory(
                DefaultDataSourceFactory(playerView.context, userAgent),
                DefaultExtractorsFactory()
            )
        player.addListener(object : Player.EventListener {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                super.onPlayerStateChanged(playWhenReady, playbackState)
                if (isAttachedToWindow) {
                    when (playbackState) {
                        Player.STATE_BUFFERING -> progressBar.visibility = View.VISIBLE
                        else -> progressBar.visibility = View.GONE
                    }
                }
            }
        })
        playerView.setShutterBackgroundColor(Color.TRANSPARENT)
        playerView.player = player
        playerView.requestFocus()
    }

    fun setDataSource(uri: Uri) {
        mUri = uri
        val mediaSource = mediaDataSourceFactory.createMediaSource(mUri)
        player.prepare(mediaSource, false, false)
        player.playWhenReady = false
    }

    fun showLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    fun onPause() {
        currentPos = player.currentPosition
        if (player.playWhenReady) {
            player.playWhenReady = false
        }
    }

    fun onResume() {
        player.seekTo(currentPos)
    }

    fun releasePlayer() {
        player.release()
    }
}