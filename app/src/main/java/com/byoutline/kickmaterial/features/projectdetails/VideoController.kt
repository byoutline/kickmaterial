package com.byoutline.kickmaterial.features.projectdetails

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.MediaController
import com.byoutline.secretsauce.activities.WebViewActivityV7
import com.byoutline.secretsauce.utils.ViewUtils

class VideoController : MediaController {

    private val url: String
    private var searchButton: Button? = null

    constructor(context: Context, attrs: AttributeSet, url: String) : super(context, attrs) {
        this.url = url
    }

    constructor(context: Context, useFastForward: Boolean, url: String) : super(context, useFastForward) {
        this.url = url
    }

    constructor(context: Context, url: String) : super(context) {
        this.url = url
    }

    override fun setAnchorView(view: View) {
        super.setAnchorView(view)

        searchButton = Button(context).apply {
            text = "Back This project"
            setOnClickListener { _ ->
                val intent = Intent(context, WebViewActivityV7::class.java)
                intent.putExtra(WebViewActivityV7.BUNDLE_URL, url)
                context.startActivity(intent)
            }
        }
        val params = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT)
        params.gravity = Gravity.RIGHT
        params.topMargin = ViewUtils.dpToPx(4f, context)
        addView(searchButton, params)
    }
}