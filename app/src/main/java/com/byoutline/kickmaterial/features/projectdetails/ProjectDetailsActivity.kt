package com.byoutline.kickmaterial.features.projectdetails

import android.app.Activity
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.os.Bundle
import android.support.annotation.ColorInt
import android.support.v4.app.ActivityCompat
import android.support.v4.app.SharedElementCallback
import android.support.v4.view.ViewCompat
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import com.byoutline.kickmaterial.KickMaterialApp
import com.byoutline.kickmaterial.R
import com.byoutline.kickmaterial.databinding.ActivityProjectDetailsBinding
import com.byoutline.kickmaterial.features.rewardlist.RewardsListActivity
import com.byoutline.kickmaterial.model.Project
import com.byoutline.kickmaterial.transitions.SharedViews
import com.byoutline.kickmaterial.utils.KickMaterialBaseActivity
import com.byoutline.kickmaterial.utils.LUtils
import com.byoutline.secretsauce.activities.WebViewActivityV7
import com.byoutline.secretsauce.activities.WebViewFlickrActivity
import com.byoutline.secretsauce.utils.ViewUtils
import com.byoutline.secretsauce.utils.showToast
import com.squareup.picasso.Picasso
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.sdk25.listeners.onClick
import javax.inject.Inject

/**
 * @author Pawel Karczewski <pawel.karczewski at byoutline.com> on 2015-01-03
 */
class ProjectDetailsActivity : KickMaterialBaseActivity(), DelayedTransitionActivity {

    @Inject
    lateinit var transitionHelperFactory: ProjectDetailsTransitionHelperFactory
    lateinit var transitionHelper: ProjectDetailsTransitionHelper


    private lateinit var binding: ActivityProjectDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_project_details)
        KickMaterialApp.component.inject(this)
        transitionHelper = transitionHelperFactory.create(intent.extras.getParcelable(EXTRA_PROJECT), projectDetailsActivity = this)
        binding.model = transitionHelper
        binding.project = transitionHelper.projectDetailsField.observable()
        supportPostponeEnterTransition()
        setUpListeners()

        binding.scrollView.addCallbacks(ProjectDetailsScrollListener(this, binding))

        binding.detailsContainer.startAnimation(AnimationUtils.loadAnimation(this@ProjectDetailsActivity, R.anim.slide_from_bottom))
        if (LUtils.hasL()) {
            animateAlphaAfterTransition(binding.projectSubtitleTv)
        }

        ViewCompat.setElevation(binding.detailsContainer, ViewUtils.convertDpToPixel(4f, this))
        transitionHelper.loadProjectPhoto(binding.projectPhotoIv)
        launchPostTransitionAnimations()
    }

    private fun launchPostTransitionAnimations() {
        if (LUtils.hasL()) {
            ActivityCompat.setEnterSharedElementCallback(this, object : SharedElementCallback() {
                override fun onSharedElementEnd(sharedElementNames: List<String>?, sharedElements: List<View>?, sharedElementSnapshots: List<View>?) {
                    binding.detailsContainer.postDelayed({
                        binding.detailsContainer.startAnimation(LUtils.loadAnimationWithLInterpolator(this@ProjectDetailsActivity, R.anim.slide_from_top))
                        binding.scrollView.startAnimation(LUtils.loadAnimationWithLInterpolator(this@ProjectDetailsActivity, R.anim.slide_from_top_long))
                    }, 0)
                }
            })
        }
    }

    override fun startPostponedEnterTrans()
            = supportStartPostponedEnterTransition()

    override fun setDetailsContainerBgColor(@ColorInt color: Int)
            = binding.detailsContainer.setBackgroundColor(color)

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.project_details, menu)
        return true
    }

    private fun animateAlphaAfterTransition(view: View) {
        view.alpha = 0f
        ActivityCompat.setEnterSharedElementCallback(this, object : SharedElementCallback() {
            override fun onSharedElementEnd(sharedElementNames: List<String>?, sharedElements: List<View>?, sharedElementSnapshots: List<View>?) {
                val alphaAnimator = getAlphaAnimator(view)
                alphaAnimator.duration = 600
                alphaAnimator.start()
                ActivityCompat.setEnterSharedElementCallback(this@ProjectDetailsActivity, null)
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }

            R.id.back_project -> {
                showRewardList()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showRewardList() {
        transitionHelper.executeIfCachedProjectDetailsAreAvailable({ details ->
            RewardsListActivity.launch(this, details, binding.playVideoBtn)
        }, displayErrorAction = { showToast(R.string.retry_getting_project_details) })
    }

    public override fun onResume() {
        super.onResume()
        title = " "
        toolbar?.setBackgroundColor(Color.TRANSPARENT)

        transitionHelper.postProjectDetails()
    }

    override fun onDestroy() {
        Picasso.with(this).cancelRequest(binding.projectPhotoIv)
        super.onDestroy()
    }

    override fun setToolbarAlpha(alpha: Float) {}

    private fun setUpListeners() {
        with(binding) {
            projectCommentsLl.onClick { showWebView(transitionHelper.project.commentsUrl) }
            projectUpdatesLl.onClick { showWebView(transitionHelper.project.updatesUrl) }
            readMoreBtn.onClick {
                val MAX_DESCRIPTION_LINES = 1000
                binding.projectDescriptionTv.maxLines = MAX_DESCRIPTION_LINES
                ViewUtils.showView(readMoreBtn, false)
            }
            playVideoBtn.onClick {
                transitionHelper.executeIfCachedProjectDetailsAreAvailable({ details ->
                    VideoActivity.showActivity(this@ProjectDetailsActivity, details)
                }, displayErrorAction = { showToast(R.string.retry_getting_project_details) })
            }
            listOf(projectAuthorNameLabelTv as View, authorPhotoIv, projectAuthorNameTv).forEach {
                it.onClick { showWebView(transitionHelper.project.authorUrl, Intent(this@ProjectDetailsActivity, WebViewFlickrActivity::class.java)) }
            }
        }
    }

    private fun showWebView(url: String, intent: Intent = Intent(this, WebViewActivityV7::class.java)) {
        intent.putExtra(WebViewActivityV7.BUNDLE_URL, url)
        startActivity(intent)
    }
}

private const val EXTRA_PROJECT = "DetailActivity:project"

fun Activity.startProjectDetailsActivity(project: Project, sharedViews: SharedViews)
    = intentFor<ProjectDetailsActivity>(EXTRA_PROJECT to project)
        .let {
            // Preload big photo
            Picasso.with(this).load(project.bigPhotoUrl)
            val options = KickMaterialBaseActivity.getSharedElementsBundle(this, *sharedViews.asArray())
            ActivityCompat.startActivity(this, it, options)
        }
