package com.byoutline.kickmaterial.features.selectcategory

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.OvershootInterpolator
import com.byoutline.kickmaterial.R
import com.byoutline.kickmaterial.databinding.ActivityCategoryListBinding
import com.byoutline.kickmaterial.model.Category
import com.byoutline.kickmaterial.utils.*
import com.byoutline.secretsauce.databinding.bindContentView
import com.byoutline.secretsauce.lifecycle.lazyViewModelWithAutoLifecycle
import com.byoutline.secretsauce.utils.ViewUtils
import org.jetbrains.anko.sdk25.listeners.onClick


/**
 * @author Pawel Karczewski <pawel.karczewski at byoutline.com> on 2015-01-03
 */
class CategoriesListActivity : AutoHideToolbarActivity(), CategoryClickListener {

    private val viewModel by lazyViewModelWithAutoLifecycle(CategoriesListViewModel::class)

    private var revealAnimation: Animator? = null
    private var category: Category? = null
    private lateinit var binding: ActivityCategoryListBinding
    private lateinit var scrollListener: ContainerTranslationScrollListener

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = bindContentView(R.layout.activity_category_list)
        category = intent.extras.getParcelable(ARG_CATEGORY)

        setUpAdapters()
        setUpListeners()
        launchPostTransitionAnimations()
    }

    private fun setUpListeners() {
        with(binding) {
            scrollListener = ContainerTranslationScrollListener(-0.5f,
                    binding.circleImageContainer, binding.categoriesHeaderLl)
            categoriesRv.addOnScrollListener(scrollListener)
            closeCategoriesIv.onClick { finishWithoutResult() }
        }
    }

    private fun launchPostTransitionAnimations() {
        val category = this.category
        if (category != null) {
            val color = ContextCompat.getColor(this, category.colorResId)
            with(binding) {
                categoryCircleIv.setColorFilter(color)
                selectedCategoryIv.setImageResource(category.drawableResId)
                selectCategoryTv.setBackgroundColor(color)
                selectCategoryTv.background.alpha = 85
            }
        }
        if (LUtils.hasL()) {
            binding.closeCategoriesIv.scaleX = 0f
            binding.closeCategoriesIv.scaleY = 0f
            setEnterSharedElementCallbackCompat {
                binding.closeCategoriesIv.postDelayed({
                    // remove listener, we do not want to trigger this animation on exit
                    ActivityCompat.setEnterSharedElementCallback(this@CategoriesListActivity, null)
                    if (isFinishing) {
                        return@postDelayed
                    }

                    val closeCategoryAnim = AnimatorUtils.getScaleAnimator(binding.closeCategoriesIv, 0f, 1f)
                    closeCategoryAnim.interpolator = OvershootInterpolator()
                    closeCategoryAnim.start()
                }, 160)
            }
        }
        binding.categoriesRv.post { binding.categoriesRv.startAnimation(LUtils.loadAnimationWithLInterpolator(applicationContext, R.anim.slide_from_bottom)) }
    }

    public override fun onResume() {
        super.onResume()
        showToolbar(false, false)
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(INSTANCE_STATE_SUMMARY_SCROLLED, scrollListener.summaryScrolledValue)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            scrollListener.summaryScrolledValue = savedInstanceState.getInt(INSTANCE_STATE_SUMMARY_SCROLLED)
        }
        super.onRestoreInstanceState(savedInstanceState)
    }

    private fun setUpAdapters() {
        binding.categoriesRv.addItemDecoration(CategoriesListSeparator(this.applicationContext))

        val itemColor = category?.colorResId ?: R.color.green_primary
        val bgColor = ContextCompat.getColor(this, itemColor)
        viewModel.setAllCategoriesBgColor(bgColor)
        binding.viewModel = viewModel
    }


    override fun categoryClicked(view: View, category: Category) {
        val checkedView = view.findViewById<View>(R.id.checked_view)
        ViewUtils.showView(checkedView, true)
        categoryClicked(category)
    }

    private fun categoryClicked(category: Category) {
        animateCategoryColor(category)
        // start loading data from API during animation
        viewModel.preloadCategory(category)
    }


    private fun animateCategoryColor(clickedCategory: Category): Category {
        val color = ContextCompat.getColor(this, clickedCategory.colorResId)

        binding.selectedCategoryIv.setImageResource(clickedCategory.drawableResId)
        binding.categoryCircleRevealIv.setColorFilter(color)

        animateCircleReveal(color, clickedCategory)
        return clickedCategory
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun animateCircleReveal(color: Int, category: Category?) {
        // get the center for the clipping circle

        val cx = (binding.categoryCircleRevealIv.left + binding.categoryCircleRevealIv.right) / 2
        val cy = binding.categoryCircleRevealIv.top + binding.categoryCircleRevealIv.bottom


        val finalRadius = Math.max(binding.categoryCircleRevealIv.width, binding.categoryCircleRevealIv.height)


        if (LUtils.hasL()) {
            revealAnimation?.end()
            val anim = ViewAnimationUtils.createCircularReveal(binding.categoryCircleRevealIv, cx, cy, 0.4f * finalRadius, finalRadius.toFloat())
            revealAnimation = anim
            anim.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator) {
                    super.onAnimationStart(animation)
                    ViewUtils.showView(binding.categoryCircleRevealIv, true)
                    binding.selectCategoryTv.setBackgroundColor(Color.TRANSPARENT)
                    LUtils.setStatusBarColor(this@CategoriesListActivity, color)
                }

                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    with(binding) {
                        categoryCircleIv.setColorFilter(color)
                        selectCategoryTv.setBackgroundColor(color)
                        selectCategoryTv.background.alpha = 85
                        categoryCircleRevealIv.visibility = View.INVISIBLE
                    }
                }
            })
            anim.duration = REVEAL_ANIM_DURATION.toLong()
            anim.interpolator = FastOutSlowInInterpolator()
            anim.start()
        } else {
            binding.categoryCircleIv.setColorFilter(color)
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
        if (scrollListener.summaryScrolledValue > 0) {
            binding.categoriesRv.smoothScrollToPosition(0)
        }

        ViewUtils.showView(binding.selectCategoryTv, false)
        val imageFade = ObjectAnimator.ofFloat<View>(binding.selectedCategoryIv, View.ALPHA, 1f, 0f)
        val set = AnimatorSet()
        val closeButtonScale = AnimatorUtils.getScaleAnimator(binding.closeCategoriesIv, 1f, 0.1f)
        set.playTogether(closeButtonScale, imageFade)
        set.duration = FINISH_ANIMATION_DURATION.toLong()
        set.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                revealAnimation?.cancel()
                ViewUtils.showView(binding.categoryCircleRevealIv, false)

                binding.closeCategoriesIv.scaleX = 0f
                binding.closeCategoriesIv.scaleY = 0f
                finishAction.run()
            }
        })
        binding.categoriesRv.startAnimation(LUtils.loadAnimationWithLInterpolator(applicationContext, R.anim.slide_to_bottom))
        set.start()
    }

    override fun onBackPressed() {
        // overwrite back to cancel reveal animation and launch transition.
        finishWithoutResult()
    }

    override fun setToolbarAlpha(alpha: Float) {}

    companion object {

        const val REVEAL_ANIM_DURATION = 400
        const val FINISH_ANIMATION_DURATION = REVEAL_ANIM_DURATION + 100
        const val RESULT_CATEGORY_SELECTED = 13
        const val RESULT_CATEGORY_SELECTION_CANCELED = 17
        const val DEFAULT_REQUEST_CODE = 101
        private const val INSTANCE_STATE_SUMMARY_SCROLLED = "INSTANCE_STATE_SUMMARY_SCROLLED"

        fun launch(context: Activity, category: Category, sharedElement: View) {
            val options = context.getSharedElementsBundle(sharedElement)
            val intent = Intent(context, CategoriesListActivity::class.java)
                    .apply { putExtra(ARG_CATEGORY, category) }
            ActivityCompat.startActivityForResult(context, intent, DEFAULT_REQUEST_CODE, options)
        }
    }
}

const val ARG_CATEGORY = "ARG_CATEGORY"
