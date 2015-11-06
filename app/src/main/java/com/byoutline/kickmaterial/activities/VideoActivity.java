package com.byoutline.kickmaterial.activities;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;
import android.widget.VideoView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.byoutline.kickmaterial.R;
import com.byoutline.kickmaterial.model.ProjectDetails;
import com.byoutline.kickmaterial.views.VideoController;
import com.byoutline.secretsauce.utils.LogUtils;

/**
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
public class VideoActivity extends KickMaterialBaseActivity {
    public static final String BUNDLE_VIDEO_URL = "bundle_video_url";
    public static final String BUNDLE_ALT_VIDEO_URL = "bundle_alt_video_url";
    public static final String BUNDLE_WEBVIEW_URL = "bundle_web_view_url";
    private static final String TAG = LogUtils.makeLogTag(VideoActivity.class);

    @Bind(R.id.video_view)
    VideoView videoView;


    public static void showActivity(Context context, ProjectDetails projectDetails) {
        showActivity(context, projectDetails.getVideoUrl(), projectDetails.getAltVideoUrl(), projectDetails.getPledgeUrl());
    }

    public static void showActivity(Context context, String videoUrl, String altVideoUrl, String webviewUrl) {
        Intent intent = new Intent(context, VideoActivity.class);
        intent.putExtra(VideoActivity.BUNDLE_VIDEO_URL, videoUrl);
        intent.putExtra(VideoActivity.BUNDLE_ALT_VIDEO_URL, altVideoUrl);
        intent.putExtra(VideoActivity.BUNDLE_WEBVIEW_URL, webviewUrl);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        ButterKnife.bind(this);
        if (savedInstanceState == null) {
            setDataFromArgs();
        }
    }

    @Override
    protected boolean shouldBlockOrientationOnBuggedAndroidVersions() {
        // This activity is locked in landscape on all devices.
        return false;
    }

    private void setDataFromArgs() {
        Intent intent = getIntent();
        if (intent == null) {
            LogUtils.LOGE(TAG, "Null intent"); // NOI18E
            return;
        }
        Bundle args = intent.getExtras();
        if (args == null) {
            LogUtils.LOGE(TAG, "Null args"); // NOI18E
            return;
        }
        String videoUrl = args.getString(BUNDLE_VIDEO_URL);
        String altVideoUrl = args.getString(BUNDLE_ALT_VIDEO_URL);
        String webviewUrl = args.getString(BUNDLE_WEBVIEW_URL);
        Uri uri = Uri.parse(videoUrl);
        videoView.setMediaController(new VideoController(this, webviewUrl));
        videoView.setVideoURI(uri);
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            boolean tryAltVideo = !TextUtils.isEmpty(altVideoUrl);

            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                if (tryAltVideo) {
                    tryAltVideo = false;
                    videoView.setVideoURI(Uri.parse(altVideoUrl));
                    videoView.start();
                    return true;
                }
                return false;
            }
        });
        videoView.setOnCompletionListener(mediaPlayer -> finish());
        videoView.requestFocus();
    }

    @Override
    protected void onStart() {
        super.onStart();
        videoView.start();
    }

    @Override
    public void setToolbarAlpha(float alpha) {

    }
}
