package com.byoutline.kickmaterial.features.projectdetails

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.Window
import android.view.WindowManager
import android.widget.VideoView
import com.byoutline.kickmaterial.R
import com.byoutline.kickmaterial.databinding.ActivityVideoBinding
import com.byoutline.kickmaterial.model.ProjectDetails
import com.byoutline.secretsauce.databinding.bindContentView
import com.byoutline.secretsauce.utils.LogUtils

/**
 * Displays fullscreen video. Since it has neither fragments nor toolbar we do not extend
 * [com.byoutline.kickmaterial.utils.AutoHideToolbarActivity]
 */
class VideoActivity : AppCompatActivity() {

    lateinit var videoView: VideoView

    public override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        super.onCreate(savedInstanceState)
        val binding: ActivityVideoBinding = bindContentView(R.layout.activity_video)
        videoView = binding.videoView
        if (savedInstanceState == null) {
            setDataFromArgs()
        }
    }

    private fun setDataFromArgs() {
        val intent = intent
        if (intent == null) {
            LogUtils.LOGE(TAG, "Null intent") // NOI18E
            return
        }
        val args = intent.extras
        if (args == null) {
            LogUtils.LOGE(TAG, "Null args") // NOI18E
            return
        }
        val videoUrl = args.getString(BUNDLE_VIDEO_URL)
        val altVideoUrl = args.getString(BUNDLE_ALT_VIDEO_URL)
        val webviewUrl = args.getString(BUNDLE_WEBVIEW_URL)
        val uri = Uri.parse(videoUrl)
        videoView.setMediaController(VideoController(this, webviewUrl))
        videoView.setVideoURI(uri)
        videoView.setOnErrorListener(object : MediaPlayer.OnErrorListener {
            internal var tryAltVideo = !TextUtils.isEmpty(altVideoUrl)

            override fun onError(mediaPlayer: MediaPlayer, i: Int, i1: Int): Boolean {
                if (tryAltVideo) {
                    tryAltVideo = false
                    videoView.setVideoURI(Uri.parse(altVideoUrl))
                    videoView.start()
                    return true
                }
                return false
            }
        })
        videoView.setOnCompletionListener { finish() }
        videoView.requestFocus()
    }

    override fun onStart() {
        super.onStart()
        videoView.start()
    }

    companion object {
        const val BUNDLE_VIDEO_URL = "bundle_video_url"
        const val BUNDLE_ALT_VIDEO_URL = "bundle_alt_video_url"
        const val BUNDLE_WEBVIEW_URL = "bundle_web_view_url"
        private val TAG = LogUtils.makeLogTag(VideoActivity::class.java)


        fun showActivity(context: Context, projectDetails: ProjectDetails) {
            showActivity(context, projectDetails.videoUrl, projectDetails.altVideoUrl, projectDetails.pledgeUrl)
        }

        fun showActivity(context: Context, videoUrl: String, altVideoUrl: String, webviewUrl: String) {
            val intent = Intent(context, VideoActivity::class.java).apply {
                putExtra(BUNDLE_VIDEO_URL, videoUrl)
                putExtra(BUNDLE_ALT_VIDEO_URL, altVideoUrl)
                putExtra(BUNDLE_WEBVIEW_URL, webviewUrl)
            }
            context.startActivity(intent)
        }
    }
}
