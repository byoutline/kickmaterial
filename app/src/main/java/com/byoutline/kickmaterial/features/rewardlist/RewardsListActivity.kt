package com.byoutline.kickmaterial.features.rewardlist

import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import com.byoutline.kickmaterial.R
import com.byoutline.kickmaterial.databinding.FragmentRewardsListBinding
import com.byoutline.kickmaterial.features.selectcategory.CategoriesListSeparator
import com.byoutline.kickmaterial.model.ProjectDetails
import com.byoutline.kickmaterial.utils.AutoHideToolbarActivity
import com.byoutline.kickmaterial.utils.ContainerTranslationScrollListener
import com.byoutline.kickmaterial.utils.LUtils
import com.byoutline.kickmaterial.utils.getSharedElementsBundle
import com.byoutline.secretsauce.databinding.bindContentView
import com.byoutline.secretsauce.lifecycle.lazyViewModel
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import org.jetbrains.anko.sdk25.listeners.onClick

/**
 * @author Pawel Karczewski <pawel.karczewski at byoutline.com> on 2015-01-03
 */
class RewardsListActivity : AutoHideToolbarActivity() {

    private val viewModel: RewardListViewModel by lazyViewModel()

    private lateinit var rewardsListRv: RecyclerView
    private lateinit var project: ProjectDetails

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: FragmentRewardsListBinding = bindContentView(R.layout.fragment_rewards_list)
        binding.model = viewModel
        rewardsListRv = binding.categoriesRv
        project = intent.extras.getParcelable(PROJECT_ARG)!!

        loadProjectData(binding)
        binding.categoriesRv.addItemDecoration(CategoriesListSeparator(applicationContext, 0))
        viewModel.setItems(project.rewards!!)
        setUpListeners(binding)
    }

    private fun setUpListeners(binding: FragmentRewardsListBinding) {
        with(binding) {
            categoriesRv.addOnScrollListener(ContainerTranslationScrollListener(-RewardsListActivity.TRANSLATION_IMAGE_RATION,
                    rewardsListImageContainer, categoriesHeaderLl))
        }
        binding.closeCategoriesIv.onClick { ActivityCompat.finishAfterTransition(this) }
    }

    private fun loadProjectData(binding: FragmentRewardsListBinding) {
        with(binding) {
            selectCategoryTv.setText(R.string.select_pledge)
            selectCategoryTv.setBackgroundColor(Color.TRANSPARENT)
            categoryCircleIv.visibility = View.GONE
            Picasso.with(applicationContext).load(project.photoUrl).into(object : Target {
                override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom?) {
                    selectedCategoryIv.setImageBitmap(bitmap)
                    LUtils.toGrayscale(binding.selectedCategoryIv)
                    selectedCategoryIv.drawable.setColorFilter(ContextCompat.getColor(this@RewardsListActivity, R.color.green_dark), PorterDuff.Mode.MULTIPLY)
                }

                override fun onBitmapFailed(errorDrawable: Drawable?) {
                    selectedCategoryIv.setImageResource(0)
                }

                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                    selectedCategoryIv.setImageResource(0)
                }
            })
        }
    }

    public override fun onResume() {
        super.onResume()
        rewardsListRv.post { rewardsListRv.startAnimation(LUtils.loadAnimationWithLInterpolator(applicationContext, R.anim.slide_from_bottom)) }
    }

    override fun setToolbarAlpha(alpha: Float) {}

    companion object {

        private const val PROJECT_ARG = "project_arg"
        const val TRANSLATION_IMAGE_RATION = 0.3f


        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        fun launch(activity: Activity, project: ProjectDetails, sharedElement: View) {
            val options = activity.getSharedElementsBundle(sharedElement)
            val intent = Intent(activity, RewardsListActivity::class.java)
            intent.putExtra(PROJECT_ARG, project)
            ActivityCompat.startActivity(activity, intent, options)
        }
    }
}
