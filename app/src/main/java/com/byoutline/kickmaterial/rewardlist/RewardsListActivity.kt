package com.byoutline.kickmaterial.rewardlist

import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.databinding.DataBindingUtil
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
import com.byoutline.kickmaterial.KickMaterialApp
import com.byoutline.kickmaterial.R
import com.byoutline.kickmaterial.databinding.FragmentRewardsListBinding
import com.byoutline.kickmaterial.login.LoginManager
import com.byoutline.kickmaterial.model.ProjectDetails
import com.byoutline.kickmaterial.selectcategory.CategoriesListSeparator
import com.byoutline.kickmaterial.utils.KickMaterialBaseActivity
import com.byoutline.kickmaterial.utils.LUtils
import com.squareup.otto.Bus
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import javax.inject.Inject

/**
 * @author Pawel Karczewski <pawel.karczewski at byoutline.com> on 2015-01-03
 */
class RewardsListActivity : KickMaterialBaseActivity() {

    @Inject
    lateinit var bus: Bus
    @Inject
    lateinit var loginManager: LoginManager
    @Inject
    lateinit var viewModel: RewardListViewModel

    private lateinit var rewardsListRv: RecyclerView
    private lateinit var project: ProjectDetails
    private var summaryScrolledValue: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        KickMaterialApp.component.inject(this)
        val binding = DataBindingUtil.setContentView<FragmentRewardsListBinding>(this, R.layout.fragment_rewards_list)
        binding.model = viewModel
        rewardsListRv = binding.categoriesRv
        project = intent.extras.getParcelable(PROJECT_ARG)!!

        loadProjectData(binding)
        binding.categoriesRv.addItemDecoration(CategoriesListSeparator(applicationContext, 0))
        viewModel.setItems(project.rewards!!)
        setUpListeners(binding)
    }

    private fun setUpListeners(binding: FragmentRewardsListBinding) {
        binding.categoriesRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                summaryScrolledValue += dy
                binding.rewardsListImageContainer.translationY = -TRANSLATION_IMAGE_RATION * summaryScrolledValue
                binding.categoriesHeaderLl.translationY = (-summaryScrolledValue).toFloat()
            }
        })
        binding.closeCategoriesIv.setOnClickListener { ActivityCompat.finishAfterTransition(this) }
    }

    private fun loadProjectData(binding: FragmentRewardsListBinding) {
        with(binding) {
            selectCategoryTv.setText(R.string.select_pledge)
            selectCategoryTv.setBackgroundColor(Color.TRANSPARENT)
            categoryCircleIv.visibility = View.GONE
            Picasso.with(applicationContext).load(project.photoUrl).into(object : Target {
                override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
                    selectedCategoryIv.setImageBitmap(bitmap)
                    LUtils.toGrayscale(binding.selectedCategoryIv)
                    selectedCategoryIv.drawable.setColorFilter(ContextCompat.getColor(this@RewardsListActivity, R.color.green_dark), PorterDuff.Mode.MULTIPLY)
                }

                override fun onBitmapFailed(errorDrawable: Drawable) {
                    selectedCategoryIv.setImageResource(0)
                }

                override fun onPrepareLoad(placeHolderDrawable: Drawable) {
                    selectedCategoryIv.setImageResource(0)
                }
            })
        }
    }

    public override fun onResume() {
        super.onResume()
        bus.register(this)

        rewardsListRv.post { rewardsListRv.startAnimation(LUtils.loadAnimationWithLInterpolator(applicationContext, R.anim.slide_from_bottom)) }
    }

    override fun onPause() {
        bus.unregister(this)
        super.onPause()
    }


    override fun setToolbarAlpha(alpha: Float) {

    }


    companion object {

        private const val PROJECT_ARG = "project_arg"
        const val TRANSLATION_IMAGE_RATION = 0.3f


        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        fun launch(activity: Activity, project: ProjectDetails, sharedElement: View) {
            val options = getSharedElementsBundle(activity, sharedElement)
            val intent = Intent(activity, RewardsListActivity::class.java)
            intent.putExtra(PROJECT_ARG, project)
            ActivityCompat.startActivity(activity, intent, options)
        }
    }
}
