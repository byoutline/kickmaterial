package com.byoutline.kickmaterial.activities

import android.app.Activity
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.SharedElementCallback
import android.support.v4.view.ViewCompat
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import butterknife.ButterKnife
import butterknife.OnClick
import com.byoutline.kickmaterial.KickMaterialApp
import com.byoutline.kickmaterial.R
import com.byoutline.kickmaterial.adapters.ProjectsAdapter
import com.byoutline.kickmaterial.databinding.ActivityProjectDetailsBinding
import com.byoutline.kickmaterial.events.ProjectDetailsFetchedEvent
import com.byoutline.kickmaterial.model.Project
import com.byoutline.kickmaterial.model.ProjectDetails
import com.byoutline.kickmaterial.model.ProjectIdAndSignature
import com.byoutline.kickmaterial.utils.AnimatorUtils
import com.byoutline.kickmaterial.utils.LUtils
import com.byoutline.kickmaterial.utils.LruCacheWithPlaceholders
import com.byoutline.kickmaterial.utils.PaletteAndAplaTransformation
import com.byoutline.kickmaterial.views.ObservableScrollView
import com.byoutline.observablecachedfield.ObservableCachedFieldWithArg
import com.byoutline.secretsauce.activities.WebViewActivityV7
import com.byoutline.secretsauce.activities.WebViewFlickrActivity
import com.byoutline.secretsauce.utils.ViewUtils
import com.squareup.otto.Bus
import com.squareup.otto.Subscribe
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import timber.log.Timber
import javax.inject.Inject

/**
 * @author Pawel Karczewski <pawel.karczewski at byoutline.com> on 2015-01-03
 */
class ProjectDetailsActivity : KickMaterialBaseActivity(), ObservableScrollView.Callbacks {

    @Inject
    lateinit var projectDetailsField: ObservableCachedFieldWithArg<ProjectDetails, ProjectIdAndSignature>
    @Inject
    lateinit var bus: Bus
    @Inject
    lateinit var picassoCache: LruCacheWithPlaceholders

    private var minTitlesMarginTop: Int = 0

    private var project: Project? = null
    private var projectDetails: ProjectDetails? = null
    private var maxTitlesMarginTop: Int = 0
    private var maxTitlesMarginLeft: Int = 0
    private var maxParallaxValue: Int = 0
    private var titleFontMaxSize: Int = 0
    private var titleFontMinSize: Int = 0
    private var maxTitlePaddingRight: Int = 0
    private var imageWidth: Int = 0
    private var imageHeight: Int = 0
    private var binding: ActivityProjectDetailsBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<ActivityProjectDetailsBinding>(this, R.layout.activity_project_details)
        ButterKnife.bind(this)
        KickMaterialApp.component.inject(this)
        binding!!.project = projectDetailsField.observable()
        supportPostponeEnterTransition()
        handleArguments()

        injectViewsAndSetUpToolbar()
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false) // Hide default toolbar title
        binding!!.scrollView.addCallbacks(this)
        minTitlesMarginTop = ViewUtils.dpToPx(32f, applicationContext)
        maxTitlesMarginTop = resources.getDimensionPixelSize(R.dimen.titles_container_margin_top) - resources.getDimensionPixelSize(R.dimen.status_bar_height)


        maxTitlesMarginLeft = ViewUtils.dpToPx(32f, applicationContext)
        maxTitlePaddingRight = ViewUtils.dpToPx(72f, applicationContext)
        maxParallaxValue = resources.getDimensionPixelSize(R.dimen.project_details_photo_height) / 3
        titleFontMaxSize = resources.getDimensionPixelSize(R.dimen.font_21)
        titleFontMinSize = resources.getDimensionPixelSize(R.dimen.font_16)
        imageHeight = resources.getDimensionPixelSize(R.dimen.project_details_photo_height)
        imageWidth = (imageHeight * ProjectsAdapter.IMAGE_RATIO).toInt()
        binding!!.detailsContainer.startAnimation(AnimationUtils.loadAnimation(this@ProjectDetailsActivity, R.anim.slide_from_bottom))
        loadProjectData()
        launchPostTransitionAnimations()
    }

    private fun launchPostTransitionAnimations() {
        if (LUtils.hasL()) {
            ActivityCompat.setEnterSharedElementCallback(this, object : SharedElementCallback() {
                override fun onSharedElementEnd(sharedElementNames: List<String>?, sharedElements: List<View>?, sharedElementSnapshots: List<View>?) {
                    binding!!.detailsContainer.postDelayed({
                        binding!!.detailsContainer.startAnimation(LUtils.loadAnimationWithLInterpolator(this@ProjectDetailsActivity, R.anim.slide_from_top))
                        binding!!.scrollView.startAnimation(LUtils.loadAnimationWithLInterpolator(this@ProjectDetailsActivity, R.anim.slide_from_top_long))
                    }, 0)
                }
            })
        }
        //        categoriesRv.post(() -> categoriesRv.startAnimation(LUtils.loadAnimationWithLInterpolator(getApplicationContext(), R.anim.slide_from_bottom)));
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.project_details, menu)
        return true
    }

    private fun loadProjectData() {
        ViewUtils.setTextOrClear(binding!!.projectSubtitleTv, getString(R.string.project_details_made_by, project!!.authorName))
        if (LUtils.hasL()) {
            animateAlphaAfterTransition(binding!!.projectSubtitleTv)
        }

        binding!!.projectBackingProgress.setText(if (project!!.isFunded) R.string.funded else R.string.backing_in_progress)

        binding!!.projectItemBigProgressSb.progress = project!!.percentProgress.toInt()
        ProjectsAdapter.setProjectDetailsInfo(binding!!.projectItemBigGatheredMoneyTv, binding!!.projectItemBigPledgedOfTv,
                binding!!.projectItemBigDaysLeft, binding!!.projectItemTimeLeftTypeTv, project!!)

        // TODO: animate elevation on scroll.
        ViewCompat.setElevation(binding!!.detailsContainer, ViewUtils.convertDpToPixel(4f, this@ProjectDetailsActivity))

        loadProjectPhoto()
    }

    private fun loadProjectPhoto() {
        val bitmap = picassoCache.getPlaceholder(project!!.bigPhotoUrl) ?: picassoCache.getPlaceholder(project!!.photoUrl)
        val placeholderAlreadyFetched = bitmap != null
        if (placeholderAlreadyFetched) {
            binding!!.projectPhotoIv.setImageBitmap(bitmap)
        }
        // Make sure that transition starts soon even if image is not ready.
        binding!!.projectPhotoIv.postDelayed({ this.supportStartPostponedEnterTransition() }, MAX_TRANSITION_DELAY.toLong())
        Picasso.with(this)
                .load(project!!.bigPhotoUrl)
                .resize(imageWidth, imageHeight)
                .onlyScaleDown()
                .centerCrop()
                .transform(PaletteAndAplaTransformation.instance())
                .into(binding!!.projectPhotoIv, object : Callback {
                    override fun onSuccess() {
                        val bitmap = (binding!!.projectPhotoIv.drawable as BitmapDrawable).bitmap // Ew!
                        val palette = PaletteAndAplaTransformation.getPalette(bitmap)
                        binding!!.detailsContainer.setBackgroundColor(palette!!.getDarkVibrantColor(Color.BLACK))
                        supportStartPostponedEnterTransition()
                    }

                    override fun onError() {
                        supportStartPostponedEnterTransition()
                    }
                })
    }

    private fun animateAlphaAfterTransition(view: View) {
        view.alpha = 0f
        ActivityCompat.setEnterSharedElementCallback(this, object : SharedElementCallback() {
            override fun onSharedElementEnd(sharedElementNames: List<String>?, sharedElements: List<View>?, sharedElementSnapshots: List<View>?) {
                val alphaAnimator = AnimatorUtils.getAlphaAnimator(view)
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
        if (projectDetails == null) {
            ViewUtils.showToast("Getting rewards failed. Retrying")
            postProjectDetails()
        } else {
            RewardsListActivity.launch(this, projectDetails!!, binding!!.playVideoBtn)
            //            showWebView(project.getPledgeUrl());
        }
    }

    public override fun onResume() {
        super.onResume()
        title = " "
        bus.register(this)
        toolbar.setBackgroundColor(Color.TRANSPARENT)

        postProjectDetails()
    }

    private fun postProjectDetails() {
        val params = ProjectIdAndSignature.create(project!!.id, project!!.detailsQueryMap)
        projectDetailsField.postValue(params)
    }

    override fun onPause() {
        bus.unregister(this)
        super.onPause()
    }

    override fun onDestroy() {
        Picasso.with(this).cancelRequest(binding!!.projectPhotoIv)
        super.onDestroy()
    }

    private fun handleArguments() {
        val args = intent.extras
        if (args != null) {
            project = args.getParcelable<Project>(EXTRA_PROJECT)
        }
    }

    override fun setToolbarAlpha(alpha: Float) {}

    @OnClick(R.id.project_comments_ll)
    fun onCommentsClicked() {
        showWebView(project!!.commentsUrl)
    }

    @OnClick(R.id.project_updates_ll)
    fun onUpdatesClicked() {
        showWebView(project!!.updatesUrl)
    }

    @OnClick(R.id.read_more_btn)
    fun readMorePressed(view: View) {
        val MAX_DESCRIPTION_LINES = 1000
        binding!!.projectDescriptionTv.maxLines = MAX_DESCRIPTION_LINES

        ViewUtils.showView(view, false)
        //        showWebView(project.getProjectUrl());
    }

    @OnClick(R.id.play_video_btn)
    fun playVideo() {
        if (projectDetails == null) {
            ViewUtils.showToast("Getting project details failed. Retrying")
            postProjectDetails()
        } else {
            VideoActivity.showActivity(this, projectDetails!!)
        }
    }

    @OnClick(R.id.project_author_name_label_tv, R.id.author_photo_iv, R.id.project_author_name_tv)
    fun authorClicked() {
        showWebView(project!!.authorUrl, Intent(this, WebViewFlickrActivity::class.java))
    }

    private fun showWebView(url: String) {
        val intent = Intent(this, WebViewActivityV7::class.java)
        showWebView(url, intent)
    }

    private fun showWebView(url: String, intent: Intent) {
        intent.putExtra(WebViewActivityV7.BUNDLE_URL, url)
        startActivity(intent)
    }

    override fun onScrollChanged(deltaX: Int, deltaY: Int) {
        val scrollY = (binding!!.scrollView.scrollY * 0.6f).toInt()
        val newTitleLeft = Math.min(maxTitlesMarginLeft.toFloat(), scrollY * 0.5f)
        val newTitleTop = Math.min(maxTitlesMarginTop, scrollY).toFloat()
        val newTitlePaddingRight = Math.min(maxTitlePaddingRight, scrollY)

        binding!!.projectTitleTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, Math.max(titleFontMaxSize - scrollY * 0.05f, titleFontMinSize.toFloat()))
        //        binding.projectTitleTv.setTextSize(TypedValue.COMPLEX_UNIT_PX,moveBase(minTitlesMarginTop,maxTitlesMarginTop,titleFontMaxSize,titleFontMinSize,scrollY));

        binding!!.projectTitleTv.setPadding(0, 0, newTitlePaddingRight, 0)

        binding!!.projectDetailsTitleContainer.translationX = newTitleLeft
        binding!!.projectDetailsTitleContainer.translationY = -newTitleTop
        binding!!.detailsContainer.translationY = -newTitleTop
        binding!!.playVideoBtn.translationY = -newTitleTop

        /** Content of scroll view is hiding to early during scroll so we move it also by
         * changing to padding  */
        binding!!.scrollView.setPadding(0, (newTitleTop * 0.6f).toInt(), 0, 0)


        // Move background photo (parallax effect)
        val parallax = (scrollY * .3f).toInt()
        if (maxParallaxValue > parallax) {
            binding!!.projectPhotoContainer.translationY = (-parallax).toFloat()
        }
    }

    fun moveBase(xmin: Int, xmax: Int, ymin: Int, ymax: Int, value: Int): Int {
        return ymin + (value - xmin) * (ymax - ymin) / (xmax - xmin)
    }

    private fun animateActionButtonVisibility(videoExist: Boolean) {
        val videoBtn = binding!!.playVideoBtn
        videoBtn.isEnabled = videoExist
        val r = Runnable {
            // Button alpha may change during transition, we may have to wait until its end
            // to check its state.
            Timber.w("Action btn alpha: " + videoBtn.alpha)
            val actionBtnShouldAnimate = videoExist && videoBtn.alpha != 1f || !videoExist && videoBtn.alpha != 0f
            if (!actionBtnShouldAnimate) {
                return@Runnable
            }
            val alphaAnimator = AnimatorUtils.getAlphaAnimator(videoBtn, !videoExist)
            alphaAnimator.duration = 600
            alphaAnimator.start()
        }
        // Wait just in case transition is still in progress.
        videoBtn.postDelayed(r, ACTION_BUTTON_VISIBILITY_ANIM_DELAY.toLong())

    }

    @Subscribe
    fun onProjectDetailsFetched(event: ProjectDetailsFetchedEvent) {
        projectDetails = event.response
        val videoExist = projectDetails!!.video != null
        animateActionButtonVisibility(videoExist)
    }

    companion object {

        private const val EXTRA_PROJECT = "DetailActivity:project"
        private const val MAX_TRANSITION_DELAY = 800
        private const val ACTION_BUTTON_VISIBILITY_ANIM_DELAY = MAX_TRANSITION_DELAY + 200

        fun launch(context: Activity, project: Project, vararg sharedViews: View) {
            val options: Bundle
            if (LUtils.hasL()) {
                options = KickMaterialBaseActivity.getSharedElementsBundle(context, *sharedViews)
            } else {
                options = Bundle()
            }
            val intent = Intent(context, ProjectDetailsActivity::class.java)

            intent.putExtra(EXTRA_PROJECT, project)
            // Preload big photo
            Picasso.with(context).load(project.bigPhotoUrl)
            ActivityCompat.startActivity(context, intent, options)
        }
    }
}