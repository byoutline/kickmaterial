package com.byoutline.kickmaterial.activities

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
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.byoutline.kickmaterial.KickMaterialApp
import com.byoutline.kickmaterial.R
import com.byoutline.kickmaterial.adapters.RewardAdapter
import com.byoutline.kickmaterial.adapters.RewardClickListener
import com.byoutline.kickmaterial.managers.LoginManager
import com.byoutline.kickmaterial.model.ProjectDetails
import com.byoutline.kickmaterial.utils.LUtils
import com.byoutline.kickmaterial.views.CategoriesListSeparator
import com.byoutline.secretsauce.utils.ViewUtils
import com.byoutline.secretsauce.views.RoundedImageView
import com.squareup.otto.Bus
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import timber.log.Timber
import javax.inject.Inject

/**
 * @author Pawel Karczewski <pawel.karczewski at byoutline.com> on 2015-01-03
 */
class RewardsListActivity : KickMaterialBaseActivity(), RewardClickListener {

    @Inject
    lateinit var bus: Bus
    @Inject
    lateinit var loginManager: LoginManager


     @JvmField @BindView(R.id.category_circle_iv)
    var categoryCircleIv: ImageView? = null
     @JvmField @BindView(R.id.categories_header_ll)
    var headerContainer: View? = null
     @JvmField @BindView(R.id.rewards_list_image_container)
    var imageContainer: View? = null
     @JvmField @BindView(R.id.category_circle_reveal_iv)
    var categoryCircleRevealIv: ImageView? = null
     @JvmField @BindView(R.id.selected_category_iv)
    var selectedCategoryIv: RoundedImageView? = null

     @JvmField @BindView(R.id.select_category_tv)
    var selectCategoryTv: TextView? = null
     @JvmField @BindView(R.id.categories_rv)
    var rewardsListRv: RecyclerView? = null

    private var adapter: RewardAdapter? = null
    private var project: ProjectDetails? = null
    private var summaryScrolledValue: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_rewards_list)
        ButterKnife.bind(this)
        KickMaterialApp.component.inject(this)
        handleArguments()
        loadProjectData()
        setUpAdapters()
        adapter!!.setItems(project!!.rewards!!)
        setUpListeners()
    }

    private fun setUpListeners() {
        rewardsListRv!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                summaryScrolledValue += dy
                imageContainer!!.translationY = -TRANSLATION_IMAGE_RATION * summaryScrolledValue
                headerContainer!!.translationY = (-summaryScrolledValue).toFloat()
            }
        })
    }

    private fun loadProjectData() {
        selectCategoryTv!!.setText(R.string.select_pledge)
        selectCategoryTv!!.setBackgroundColor(Color.TRANSPARENT)
        categoryCircleIv!!.visibility = View.GONE
        Picasso.with(applicationContext).load(project!!.photoUrl).into(object : Target {
            override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
                selectedCategoryIv!!.setImageBitmap(bitmap)
                LUtils.toGrayscale(selectedCategoryIv!!)
                selectedCategoryIv!!.drawable.setColorFilter(ContextCompat.getColor(this@RewardsListActivity, R.color.green_dark), PorterDuff.Mode.MULTIPLY)
            }

            override fun onBitmapFailed(errorDrawable: Drawable) {
                selectedCategoryIv!!.setImageResource(0)
            }

            override fun onPrepareLoad(placeHolderDrawable: Drawable) {
                selectedCategoryIv!!.setImageResource(0)
            }
        })
    }

    private fun handleArguments() {
        val args = intent.extras
        if (args != null && args.containsKey(PROJECT_ARG)) {
            project = args.getParcelable<ProjectDetails>(PROJECT_ARG)
        } else {
            Timber.e("Project not passed")
        }
    }


    public override fun onResume() {
        super.onResume()
        bus.register(this)

        rewardsListRv!!.post { rewardsListRv!!.startAnimation(LUtils.loadAnimationWithLInterpolator(applicationContext, R.anim.slide_from_bottom)) }
    }

    override fun onPause() {
        bus.unregister(this)
        super.onPause()
    }

    private fun setUpAdapters() {
        /** NEW ADAPTER  */
        val layoutManager = LinearLayoutManager(this)
        rewardsListRv!!.layoutManager = layoutManager
        rewardsListRv!!.addItemDecoration(CategoriesListSeparator(applicationContext, 0))
        adapter = RewardAdapter(this, this)
        rewardsListRv!!.adapter = adapter
    }


    override fun rewardClicked(position: Int) {
        ViewUtils.showDebugToast("RewardClicked " + position)
    }

    override fun setToolbarAlpha(alpha: Float) {

    }

    @OnClick(R.id.close_categories_iv)
    fun onCloseCategories() {
        ActivityCompat.finishAfterTransition(this)
    }

    companion object {

        private const val PROJECT_ARG = "project_arg"
        const val TRANSLATION_IMAGE_RATION = 0.3f


        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        fun launch(activity: Activity, project: ProjectDetails, sharedElement: View) {
            val options = KickMaterialBaseActivity.getSharedElementsBundle(activity, sharedElement)
            val intent = Intent(activity, RewardsListActivity::class.java)
            intent.putExtra(PROJECT_ARG, project)
            ActivityCompat.startActivity(activity, intent, options)
        }
    }
}
