package com.byoutline.secretsauce.activities

import android.os.Bundle
import android.view.KeyEvent
import android.webkit.WebView
import android.webkit.WebViewClient

/**
 * WebView that hides flick redirect (which normally require pressing back twice
 * to get back to application)

 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
class WebViewFlickrActivity : WebViewActivityV7() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Do not launch external browser for redirect.
        webview.setWebViewClient(object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return false
            }
        })
    }

    /**
     * Overridden to use [.onBackPressed] instead of webview back
     * if there is only one element left in history.

     * @param keyCode
     * *
     * @param event
     * *
     * @return
     */
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        val canGoBack = webview.canGoBackOrForward(-2)
        val isBack = keyCode == KeyEvent.KEYCODE_BACK
        if (isBack && canGoBack) {
            webview.goBack()
            return true
        } else if (isBack) {
            onBackPressed()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}
