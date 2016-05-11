package com.byoutline.kickmaterial.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.byoutline.kickmaterial.KickMaterialApp;
import com.byoutline.kickmaterial.R;
import com.byoutline.kickmaterial.adapters.RewardAdapter;
import com.byoutline.kickmaterial.adapters.RewardClickListener;
import com.byoutline.kickmaterial.managers.LoginManager;
import com.byoutline.kickmaterial.model.ProjectDetails;
import com.byoutline.kickmaterial.utils.LUtils;
import com.byoutline.kickmaterial.views.CategoriesListSeparator;
import com.byoutline.secretsauce.utils.ViewUtils;
import com.byoutline.secretsauce.views.RoundedImageView;
import com.squareup.otto.Bus;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import org.parceler.Parcels;
import timber.log.Timber;

import javax.annotation.Nonnull;
import javax.inject.Inject;

/**
 * @author Pawel Karczewski <pawel.karczewski at byoutline.com> on 2015-01-03
 */
public class RewardsListActivity extends KickMaterialBaseActivity implements RewardClickListener {

    private static final String PROJECT_ARG = "project_arg";
    public static final float TRANSLATION_IMAGE_RATION = 0.3f;

    @Inject
    Bus bus;
    @Inject
    LoginManager loginManager;


    @BindView(R.id.category_circle_iv)
    ImageView categoryCircleIv;
    @BindView(R.id.categories_header_ll)
    View headerContainer;
    @BindView(R.id.rewards_list_image_container)
    View imageContainer;
    @BindView(R.id.category_circle_reveal_iv)
    ImageView categoryCircleRevealIv;
    @BindView(R.id.selected_category_iv)
    RoundedImageView selectedCategoryIv;

    @BindView(R.id.select_category_tv)
    TextView selectCategoryTv;
    @BindView(R.id.categories_rv)
    RecyclerView rewardsListRv;

    private RewardAdapter adapter;
    private ProjectDetails project;
    private int summaryScrolledValue;


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void launch(@Nonnull Activity activity, @Nonnull ProjectDetails project, View sharedElement) {
        final Bundle options = KickMaterialBaseActivity.getSharedElementsBundle(activity, sharedElement);
        Intent intent = new Intent(activity, RewardsListActivity.class);
        Parcelable wrapped = Parcels.wrap(project);
        intent.putExtra(PROJECT_ARG, wrapped);
        ActivityCompat.startActivity(activity, intent, options);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_rewards_list);
        ButterKnife.bind(this);
        KickMaterialApp.component.inject(this);
        handleArguments();
        loadProjectData();
        setUpAdapters();
        adapter.setItems(project.rewards);
        setUpListeners();
    }

    private void setUpListeners() {
        rewardsListRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                summaryScrolledValue += dy;
                imageContainer.setTranslationY(-TRANSLATION_IMAGE_RATION * summaryScrolledValue);
                headerContainer.setTranslationY(-summaryScrolledValue);
            }
        });
    }

    private void loadProjectData() {
        selectCategoryTv.setText(R.string.select_pledge);
        selectCategoryTv.setBackgroundColor(Color.TRANSPARENT);
        categoryCircleIv.setVisibility(View.GONE);
        Picasso.with(getApplicationContext()).load(project.getPhotoUrl()).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                selectedCategoryIv.setImageBitmap(bitmap);
                LUtils.toGrayscale(selectedCategoryIv);
                selectedCategoryIv.getDrawable().setColorFilter(ContextCompat.getColor(RewardsListActivity.this, R.color.green_dark), PorterDuff.Mode.MULTIPLY);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                selectedCategoryIv.setImageResource(0);
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                selectedCategoryIv.setImageResource(0);
            }
        });
    }

    private void handleArguments() {
        Bundle args = getIntent().getExtras();
        if (args != null && args.containsKey(PROJECT_ARG)) {
            project = Parcels.unwrap(args.getParcelable(PROJECT_ARG));
        } else {
            Timber.e("Project not passed");
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        bus.register(this);

        rewardsListRv.post(() -> rewardsListRv.startAnimation(LUtils.loadAnimationWithLInterpolator(getApplicationContext(), R.anim.slide_from_bottom)));
    }

    @Override
    public void onPause() {
        bus.unregister(this);
        super.onPause();
    }

    private void setUpAdapters() {
        /** NEW ADAPTER **/
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rewardsListRv.setLayoutManager(layoutManager);
        rewardsListRv.addItemDecoration(new CategoriesListSeparator(getApplicationContext(), 0));
        adapter = new RewardAdapter(this, this);
        rewardsListRv.setAdapter(adapter);
    }


    @Override
    public void rewardClicked(int position) {
        ViewUtils.showDebugToast("RewardClicked " + position);
    }

    @Override
    public void setToolbarAlpha(float alpha) {

    }

    @OnClick(R.id.close_categories_iv)
    public void onCloseCategories() {
        ActivityCompat.finishAfterTransition(this);
    }
}
