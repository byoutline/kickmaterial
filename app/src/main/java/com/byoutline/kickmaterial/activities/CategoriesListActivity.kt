package com.byoutline.kickmaterial.activities

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.SharedElementCallback
import android.support.v4.content.ContextCompat
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.OvershootInterpolator
import butterknife.ButterKnife
import butterknife.OnClick
import com.byoutline.cachedfield.CachedFieldWithArg
import com.byoutline.kickmaterial.KickMaterialApp
import com.byoutline.kickmaterial.R
import com.byoutline.kickmaterial.adapters.CategoryClickListener
import com.byoutline.kickmaterial.databinding.ActivityCategoryListBinding
import com.byoutline.kickmaterial.managers.CategoriesListViewModel
import com.byoutline.kickmaterial.model.Category
import com.byoutline.kickmaterial.model.DiscoverQuery
import com.byoutline.kickmaterial.model.DiscoverResponse
import com.byoutline.kickmaterial.utils.AnimatorUtils
import com.byoutline.kickmaterial.utils.LUtils
import com.byoutline.kickmaterial.views.CategoriesListSeparator
import com.byoutline.secretsauce.utils.ViewUtils

import timber.log.Timber
import javax.inject.Inject


/**
 * @author Pawel Karczewski <pawel.karczewski at byoutline.com> on 2015-01-03
 */
class CategoriesListActivity : KickMaterialBaseActivity(), CategoryClickListener {

    @Inject
    lateinit var discoverField: CachedFieldWithArg<DiscoverResponse, DiscoverQuery>
    @Inject
    lateinit var viewModel: CategoriesListViewModel

    private var revealAnimation: Animator? = null
    private var category: Category? = null
    private var summaryScrolledValue: Int = 0
    private var binding: ActivityCategoryListBinding? = null

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bind = DataBindingUtil.setContentView<ActivityCategoryListBinding>(this, R.layout.activity_category_list)
        binding = bind

        injectViewsAndSetUpToolbar()
        KickMaterialApp.component.inject(this)
        ButterKnife.bind(this)
        handleArguments()
        setUpAdapters(bind)
        setUpListeners()
        launchPostTransitionAnimations()
    }

    private fun setUpListeners() {
        binding!!.categoriesRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                summaryScrolledValue += dy
                binding!!.circleImageContainer.translationY = -0.5f * summaryScrolledValue
                binding!!.categoriesHeaderLl.translationY = (-summaryScrolledValue).toFloat()
            }
        })
    }

    private fun launchPostTransitionAnimations() {
        if (category != null) {
            val color = ContextCompat.getColor(this, category!!.colorResId)
            binding!!.categoryCircleIv.setColorFilter(color)
            binding!!.selectedCategoryIv.setImageResource(category!!.drawableResId)
            binding!!.selectCategoryTv.setBackgroundColor(color)
            binding!!.selectCategoryTv.background.alpha = 85
        }
        if (LUtils.hasL()) {
            binding!!.closeCategoriesIv.scaleX = 0f
            binding!!.closeCategoriesIv.scaleY = 0f
            ActivityCompat.setEnterSharedElementCallback(this, object : SharedElementCallback() {
                override fun onSharedElementEnd(sharedElementNames: List<String>?, sharedElements: List<View>?, sharedElementSnapshots: List<View>?) {
                    binding!!.closeCategoriesIv.postDelayed({
                        // remove listener, we do not want to trigger this animation on exit
                        ActivityCompat.setEnterSharedElementCallback(this@CategoriesListActivity, null)
                        if (isFinishing) {
                            return@postDelayed
                        }

                        val closeCategoryAnim = AnimatorUtils.getScaleAnimator(binding!!.closeCategoriesIv, 0f, 1f)
                        closeCategoryAnim.interpolator = OvershootInterpolator()
                        closeCategoryAnim.start()
                    }, 160)
                }
            })
        }
        binding!!.categoriesRv.post { binding!!.categoriesRv.startAnimation(LUtils.loadAnimationWithLInterpolator(applicationContext, R.anim.slide_from_bottom)) }
    }

    private fun handleArguments() {
        val args = intent.extras
        if (args != null && args.containsKey(ARG_CATEGORY)) {
            category = args.getParcelable<Category>(ARG_CATEGORY)
        } else {
            Timber.e("Category not passed")
        }
    }

    public override fun onResume() {
        super.onResume()
        showActionbar(false, false)
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(INSTANCE_STATE_SUMMARY_SCROLLED, summaryScrolledValue)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            summaryScrolledValue = savedInstanceState.getInt(INSTANCE_STATE_SUMMARY_SCROLLED)
        }
        super.onRestoreInstanceState(savedInstanceState)
    }

    private fun setUpAdapters(binding: ActivityCategoryListBinding) {
        binding.categoriesRv.addItemDecoration(CategoriesListSeparator(this.applicationContext))

        var itemColor = R.color.green_primary
        if (category != null) {
            itemColor = category!!.colorResId
        }
        val bgColor = ContextCompat.getColor(this, itemColor)
        viewModel!!.setAllCategoriesBgColor(bgColor)
        binding.categoryClickListener = this
        binding.viewModel = viewModel
    }


    override fun categoryClicked(view: View, category: Category) {
        val checkedView = view.findViewById(R.id.checked_view)
        ViewUtils.showView(checkedView, true)
        categoryClicked(category)
    }

    fun categoryClicked(category: Category) {
        animateCategoryColor(category)
        // start loading data from API during animation
        discoverField!!.postValue(DiscoverQuery.getDiscoverQuery(category, 1))
    }


    private fun animateCategoryColor(clickedCategory: Category): Category {
        val color = ContextCompat.getColor(this, clickedCategory.colorResId)

        binding!!.selectedCategoryIv.setImageResource(clickedCategory.drawableResId)
        binding!!.categoryCircleRevealIv.setColorFilter(color)

        animateCircleReveal(color, clickedCategory)
        return clickedCategory
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun animateCircleReveal(color: Int, category: Category?) {
        // get the center for the clipping circle

        val cx = (binding!!.categoryCircleRevealIv.left + binding!!.categoryCircleRevealIv.right) / 2
        val cy = binding!!.categoryCircleRevealIv.top + binding!!.categoryCircleRevealIv.bottom


        val finalRadius = Math.max(binding!!.categoryCircleRevealIv.width, binding!!.categoryCircleRevealIv.height)


        if (LUtils.hasL()) {
            if (revealAnimation != null && revealAnimation!!.isRunning) {
                revealAnimation!!.end()
            }
            revealAnimation = ViewAnimationUtils.createCircularReveal(binding!!.categoryCircleRevealIv, cx, cy, 0.4f * finalRadius, finalRadius.toFloat())
            revealAnimation!!.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator) {
                    super.onAnimationStart(animation)
                    ViewUtils.showView(binding!!.categoryCircleRevealIv, true)
                    binding!!.selectCategoryTv.setBackgroundColor(Color.TRANSPARENT)
                    LUtils.setStatusBarColor(this@CategoriesListActivity, color)
                }

                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    if (binding!!.categoryCircleIv != null) {
                        binding!!.categoryCircleIv.setColorFilter(color)
                        binding!!.selectCategoryTv.setBackgroundColor(color)
                        binding!!.selectCategoryTv.background.alpha = 85
                        binding!!.categoryCircleRevealIv.visibility = View.INVISIBLE
                    }
                }
            })
            revealAnimation!!.duration = REVEAL_ANIM_DURATION.toLong()
            revealAnimation!!.interpolator = FastOutSlowInInterpolator()
            revealAnimation!!.start()
        } else {
            binding!!.categoryCircleIv.setColorFilter(color)
        }
        if (category != null) {
            finishWithResult(category)
        }
    }

    private fun finishWithResult(category: Category) {
        runFinishAnimation(Runnable {
            val intent = Intent()
            intent.putExtra(ARG_CATEGORY, category)
            setResult(RESULT_CATEGORY_SELECTED, intent)
            ActivityCompat.finishAfterTransition(this@CategoriesListActivity)
        })
    }

    private fun finishWithoutResult() {
        runFinishAnimation(Runnable {
            setResult(RESULT_CATEGORY_SELECTION_CANCELED)
            ActivityCompat.finishAfterTransition(this@CategoriesListActivity)
        })
    }

    private fun runFinishAnimation(finishAction: Runnable) {
        if (summaryScrolledValue > 0) {
            binding!!.categoriesRv.smoothScrollToPosition(0)
        }

        ViewUtils.showView(binding!!.selectCategoryTv, false)
        val imageFade = ObjectAnimator.ofFloat<View>(binding!!.selectedCategoryIv, View.ALPHA, 1f, 0f)
        val set = AnimatorSet()
        val closeButtonScale = AnimatorUtils.getScaleAnimator(binding!!.closeCategoriesIv, 1f, 0.1f)
        set.playTogether(closeButtonScale, imageFade)
        set.duration = FINISH_ANIMATION_DURATION.toLong()
        set.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                if (revealAnimation != null) {
                    revealAnimation!!.cancel()
                }
                ViewUtils.showView(binding!!.categoryCircleRevealIv, false)

                binding!!.closeCategoriesIv.scaleX = 0f
                binding!!.closeCategoriesIv.scaleY = 0f
                finishAction.run()
            }
        })
        binding!!.categoriesRv.startAnimation(LUtils.loadAnimationWithLInterpolator(applicationContext, R.anim.slide_to_bottom))
        set.start()
    }

    override fun onBackPressed() {
        // overwrite back to cancel reveal animation and launch transition.
        finishWithoutResult()
    }

    @OnClick(R.id.close_categories_iv)
    fun onCloseCategories() {
        finishWithoutResult()
    }

    override fun setToolbarAlpha(alpha: Float) {}

    companion object {

        const val ARG_CATEGORY = "ARG_CATEGORY"
        const val REVEAL_ANIM_DURATION = 400
        const val FINISH_ANIMATION_DURATION = REVEAL_ANIM_DURATION + 100
        const val RESULT_CATEGORY_SELECTED = 13
        const val RESULT_CATEGORY_SELECTION_CANCELED = 17
        const val DEFAULT_REQUEST_CODE = 101
        private const val INSTANCE_STATE_SUMMARY_SCROLLED = "INSTANCE_STATE_SUMMARY_SCROLLED"

        fun launch(context: Activity, category: Category, sharedElement: View) {
            val options = KickMaterialBaseActivity.getSharedElementsBundle(context, sharedElement)
            val intent = Intent(context, CategoriesListActivity::class.java)
            intent.putExtra(ARG_CATEGORY, category)
            ActivityCompat.startActivityForResult(context, intent, DEFAULT_REQUEST_CODE, options)
        }
    }
}
