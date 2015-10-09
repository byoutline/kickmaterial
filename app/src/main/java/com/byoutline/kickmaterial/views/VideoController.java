package com.byoutline.kickmaterial.views;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.MediaController;
import com.byoutline.secretsauce.activities.WebViewActivityV7;
import com.byoutline.secretsauce.utils.ViewUtils;

public class VideoController extends MediaController {

    private final String url;
    private Button searchButton;

    public VideoController(Context context, AttributeSet attrs, String url) {
        super(context, attrs);
        this.url = url;
    }

    public VideoController(Context context, boolean useFastForward, String url) {
        super(context, useFastForward);
        this.url = url;
    }

    public VideoController(Context context, String url) {
        super(context);
        this.url = url;
    }

    @Override
    public void setAnchorView(View view) {
        super.setAnchorView(view);

        searchButton = new Button(getContext());
        searchButton.setText("Back This project");
        searchButton.setOnClickListener(ignored -> {
            Intent intent = new Intent(getContext(), WebViewActivityV7.class);
            intent.putExtra(WebViewActivityV7.BUNDLE_URL, url);
            getContext().startActivity(intent);
        });
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.RIGHT;
        params.topMargin = ViewUtils.dpToPx(4, getContext());
        addView(searchButton, params);
    }
}