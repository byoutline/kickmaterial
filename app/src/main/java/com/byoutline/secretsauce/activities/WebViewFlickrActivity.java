package com.byoutline.secretsauce.activities;

import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * WebView that hides flick redirect (which normally require pressing back twice
 * to get back to application)
 *
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
public class WebViewFlickrActivity extends WebViewActivityV7 {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Do not launch external browser for redirect.
        webview.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return false;
            }
        });
    }

    /**
     * Overridden to use {@link #onBackPressed()} instead of webview back
     * if there is only one element left in history.
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean canGoBack = webview.canGoBackOrForward(-2);
        boolean isBack = keyCode == KeyEvent.KEYCODE_BACK;
        if (isBack && canGoBack) {
            webview.goBack();
            return true;
        } else if (isBack) {
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
