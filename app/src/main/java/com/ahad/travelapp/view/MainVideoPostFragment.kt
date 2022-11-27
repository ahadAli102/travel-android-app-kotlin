package com.ahad.travelapp.view

import android.content.res.Configuration
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import androidx.navigation.fragment.navArgs
import com.ahad.travelapp.R
import kotlinx.android.synthetic.main.fragment_main_video_post.*

class MainVideoPostFragment : Fragment() {
    private val args: MainVideoPostFragmentArgs by navArgs()
    private lateinit var simpleExoPlayer: SimpleExoPlayer
    private lateinit var mediaSource:MediaSource
    private lateinit var videoUrl:String
    private lateinit var _activity:MainActivity
    private lateinit var urlType:URLType

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_video_post, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        videoUrl = args.passedPostVideo
        Log.d(TAG, "onViewCreated: $videoUrl")
        _activity = (activity as MainActivity)
        _activity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        initPlayer()
    }

    companion object {
        private const val TAG="MyTag:MainActVidPoF"
    }
    private fun initPlayer(){
        simpleExoPlayer = SimpleExoPlayer.Builder(_activity).build()
        simpleExoPlayer.addListener(playerListener)
        playerView.player = simpleExoPlayer

        createMediaSource()

        simpleExoPlayer.setMediaSource(mediaSource)
        simpleExoPlayer.prepare()
    }
    private fun createMediaSource(){
        urlType = URLType.MP4
        urlType.url = videoUrl
        simpleExoPlayer.seekTo(0)
        val name = _activity.applicationInfo.name
        when(urlType){
            URLType.MP4 ->{
                val dataSourceFactory:DataSource.Factory = DefaultDataSourceFactory(
                    requireContext(),
                    Util.getUserAgent(requireContext(),name)
                )
                mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(
                    MediaItem.fromUri(Uri.parse(urlType.url))
                )
            }
            URLType.HLS ->{
//                val dataSourceFactory:DataSource.Factory = DefaultDataSourceFactory(
//                    this,
//                    Util.getUserAgent(this,applicationInfo.name)
//                )
//                mediaSource = HlsMediaSource.Factory(dataSourceFactory).createMediaSource(
//                    MediaItem.fromUri(Uri.parse(urlType.url))
//                )
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val constraintSet = ConstraintSet()
        constraintSet.connect(playerView.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID,ConstraintSet.TOP,0)
        constraintSet.connect(playerView.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID,ConstraintSet.BOTTOM,0)
        constraintSet.connect(playerView.id, ConstraintSet.START, ConstraintSet.PARENT_ID,ConstraintSet.START,0)
        constraintSet.connect(playerView.id, ConstraintSet.END, ConstraintSet.PARENT_ID,ConstraintSet.END,0)

        constraintSet.applyTo(constraintLayoutRoot)

        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            hideSystemUi()
        }
        else{
            showSystemUi()

            val layoutParams = playerView.layoutParams as ConstraintLayout.LayoutParams
            layoutParams.dimensionRatio = "16:9"
            _activity.window.decorView.requestLayout()
        }
    }

    private fun hideSystemUi(){
        _activity.actionBar?.hide()
        _activity.window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                )
    }
    private fun showSystemUi(){
        _activity.actionBar?.show()
        _activity.window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                )
    }

    override fun onResume() {
        super.onResume()
        simpleExoPlayer.playWhenReady = true
        simpleExoPlayer.play()
    }

    override fun onPause() {
        super.onPause()
        simpleExoPlayer.pause()
        simpleExoPlayer.playWhenReady = false
    }

    override fun onStop() {
        super.onStop()
        simpleExoPlayer.pause()
        simpleExoPlayer.playWhenReady = false
    }

    override fun onDestroy() {
        super.onDestroy()
        simpleExoPlayer.addListener(playerListener)
        simpleExoPlayer.stop()
        simpleExoPlayer.clearMediaItems()
        _activity.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private var playerListener = object :  Player.Listener{
        override fun onRenderedFirstFrame() {
            super.onRenderedFirstFrame()
            if(urlType == URLType.HLS){
                playerView.useController = false
            }
            if(urlType == URLType.MP4){
                playerView.useController = true
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            super.onPlayerError(error)
            Toast.makeText(_activity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
enum class URLType(var url:String){
    MP4(""), HLS("")
}