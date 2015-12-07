package com.byoutline.kickmaterial.activities;

import android.animation.Animator;
import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.view.ViewCompat;
import android.support.v7.graphics.Palette;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.byoutline.kickmaterial.KickMaterialApp;
import com.byoutline.kickmaterial.R;
import com.byoutline.kickmaterial.adapters.ProjectsAdapter;
import com.byoutline.kickmaterial.databinding.ActivityProjectDetailsBinding;
import com.byoutline.kickmaterial.events.ProjectDetailsFetchedEvent;
import com.byoutline.kickmaterial.model.Project;
import com.byoutline.kickmaterial.model.ProjectDetails;
import com.byoutline.kickmaterial.model.ProjectIdAndSignature;
import com.byoutline.kickmaterial.utils.AnimatorUtils;
import com.byoutline.kickmaterial.utils.LUtils;
import com.byoutline.kickmaterial.utils.LruCacheWithPlaceholders;
import com.byoutline.kickmaterial.utils.PaletteAndAplaTransformation;
import com.byoutline.kickmaterial.views.ObservableScrollView;
import com.byoutline.ottocachedfield.ObservableCachedFieldWithArg;
import com.byoutline.secretsauce.activities.WebViewActivityV7;
import com.byoutline.secretsauce.activities.WebViewFlickrActivity;
import com.byoutline.secretsauce.utils.ViewUtils;
import com.software.shell.fab.ActionButton;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import org.parceler.Parcels;
import timber.log.Timber;

import javax.inject.Inject;
import java.util.List;

/**
 * @author Pawel Karczewski <pawel.karczewski at byoutline.com> on 2015-01-03
 */
public class ProjectDetailsActivity extends KickMaterialBaseActivity implements ObservableScrollView.Callbacks {

    private static final String EXTRA_PROJECT = "DetailActivity:project";
    private static final int MAX_TRANSITION_DELAY = 800;
    private static final int ACTION_BUTTON_VISIBILITY_ANIM_DELAY = MAX_TRANSITION_DELAY + 200;

    @Inject
    ObservableCachedFieldWithArg<ProjectDetails, ProjectIdAndSignature> projectDetailsField;
    @Inject
    Bus bus;
    @Inject
    LruCacheWithPlaceholders picassoCache;

    int minTitlesMarginTop;

    private Project project;
    private ProjectDetails projectDetails;
    private int maxTitlesMarginTop;
    private int maxTitlesMarginLeft;
    private int maxParallaxValue;
    private int titleFontMaxSize;
    private int titleFontMinSize;
    private int maxTitlePaddingRight;
    private int imageWidth;
    private int imageHeight;
    private ActivityProjectDetailsBinding binding;

    public static void launch(Activity context, Project project, View... sharedViews) {
        final Bundle options;
        if (LUtils.hasL()) {
            options = getSharedElementsBundle(context, sharedViews);
        } else {
            options = new Bundle();
        }
        Intent intent = new Intent(context, ProjectDetailsActivity.class);

        Parcelable wrapped = Parcels.wrap(project);
        intent.putExtra(EXTRA_PROJECT, wrapped);
        // Preload big photo
        Picasso.with(context).load(project.getBigPhotoUrl());
        ActivityCompat.startActivity(context, intent, options);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_project_details);
        ButterKnife.bind(this);
        KickMaterialApp.component.inject(this);
        binding.setProject(projectDetailsField.observable());
        supportPostponeEnterTransition();
        handleArguments();

        injectViewsAndSetUpToolbar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false); // Hide default toolbar title
        binding.scrollView.addCallbacks(this);
        minTitlesMarginTop = ViewUtils.dpToPx(32, getApplicationContext());
        maxTitlesMarginTop = getResources().getDimensionPixelSize(R.dimen.titles_container_margin_top) - getResources().getDimensionPixelSize(R.dimen.status_bar_height);


        maxTitlesMarginLeft = ViewUtils.dpToPx(32, getApplicationContext());
        maxTitlePaddingRight = ViewUtils.dpToPx(72, getApplicationContext());
        maxParallaxValue = getResources().getDimensionPixelSize(R.dimen.project_details_photo_height) / 3;
        titleFontMaxSize = getResources().getDimensionPixelSize(R.dimen.font_21);
        titleFontMinSize = getResources().getDimensionPixelSize(R.dimen.font_16);
        imageHeight = getResources().getDimensionPixelSize(R.dimen.project_details_photo_height);
        imageWidth = (int) (imageHeight * ProjectsAdapter.IMAGE_RATIO);
        binding.detailsContainer.startAnimation(AnimationUtils.loadAnimation(ProjectDetailsActivity.this, R.anim.slide_from_bottom));
        loadProjectData();
        launchPostTransitionAnimations();
    }

    private void launchPostTransitionAnimations() {
        if (LUtils.hasL()) {
            ActivityCompat.setEnterSharedElementCallback(this, new SharedElementCallback() {
                @Override
                public void onSharedElementEnd(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots) {
                    binding.detailsContainer.postDelayed(() -> {
                        binding.detailsContainer.startAnimation(LUtils.loadAnimationWithLInterpolator(ProjectDetailsActivity.this, R.anim.slide_from_top));
                        binding.scrollView.startAnimation(LUtils.loadAnimationWithLInterpolator(ProjectDetailsActivity.this, R.anim.slide_from_top_long));
                    }, 0);
                }
            });
        }
//        categoriesRv.post(() -> categoriesRv.startAnimation(LUtils.loadAnimationWithLInterpolator(getApplicationContext(), R.anim.slide_from_bottom)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.project_details, menu);
        return true;
    }

    private void loadProjectData() {
        ViewUtils.setTextOrClear(binding.projectSubtitleTv, getString(R.string.project_details_made_by, project.getAuthorName()));
        if (LUtils.hasL()) {
            animateAlphaAfterTransition(binding.projectSubtitleTv);
        }

        binding.projectBackingProgress.setText(project.isFunded() ? R.string.funded : R.string.backing_in_progress);

        binding.projectItemBigProgressSb.setProgress((int) project.getPercentProgress());
        ProjectsAdapter.setProjectDetailsInfo(binding.projectItemBigGatheredMoneyTv, binding.projectItemBigPledgedOfTv,
                binding.projectItemBigDaysLeft, binding.projectItemTimeLeftTypeTv, project);

        // TODO: animate elevation on scroll.
        ViewCompat.setElevation(binding.detailsContainer, ViewUtils.convertDpToPixel(4, ProjectDetailsActivity.this));

        loadProjectPhoto();
    }

    private void loadProjectPhoto() {
        Bitmap bitmap = picassoCache.getPlaceholder(project.getBigPhotoUrl());
        boolean bigPhotoMustBeFetched = bitmap == null;
        if (bigPhotoMustBeFetched) {
            bitmap = picassoCache.getPlaceholder(project.getPhotoUrl());
            boolean placeholderAlreadyFetched = bitmap != null;
            if (placeholderAlreadyFetched) {
                binding.projectPhotoIv.setImageBitmap(bitmap);
            }
        }
        // Make sure that transition starts soon even if image is not ready.
        binding.projectPhotoIv.postDelayed(this::supportStartPostponedEnterTransition, MAX_TRANSITION_DELAY);
        Picasso.with(this)
                .load(project.getBigPhotoUrl())
                .resize(imageWidth, imageHeight)
                .onlyScaleDown()
                .centerCrop()
                .transform(PaletteAndAplaTransformation.instance())
                .into(binding.projectPhotoIv, new Callback() {
                    @Override
                    public void onSuccess() {
                        Bitmap bitmap = ((BitmapDrawable) binding.projectPhotoIv.getDrawable()).getBitmap(); // Ew!
                        Palette palette = PaletteAndAplaTransformation.getPalette(bitmap);
                        binding.detailsContainer.setBackgroundColor(palette.getDarkVibrantColor(Color.BLACK));
                        supportStartPostponedEnterTransition();
                    }

                    @Override
                    public void onError() {
                        supportStartPostponedEnterTransition();
                    }
                });
    }

    private void animateAlphaAfterTransition(final View view) {
        view.setAlpha(0);
        ActivityCompat.setEnterSharedElementCallback(this, new SharedElementCallback() {
            @Override
            public void onSharedElementEnd(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots) {
                Animator alphaAnimator = AnimatorUtils.getAlphaAnimator(view);
                alphaAnimator.setDuration(600);
                alphaAnimator.start();
                ActivityCompat.setEnterSharedElementCallback(ProjectDetailsActivity.this, null);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.back_project:
                showRewardList();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showRewardList() {
        if (projectDetails == null) {
            ViewUtils.showToast("Getting rewards failed. Retrying");
            postProjectDetails();
        } else {
            RewardsListActivity.launch(this, projectDetails, binding.playVideoBtn);
//            showWebView(project.getPledgeUrl());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setTitle(" ");
        bus.register(this);
        toolbar.setBackgroundColor(Color.TRANSPARENT);

        postProjectDetails();
    }

    private void postProjectDetails() {
        ProjectIdAndSignature params = ProjectIdAndSignature.create(project.id, project.getDetailsQueryMap());
        projectDetailsField.postValue(params);
    }

    @Override
    public void onPause() {
        bus.unregister(this);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Picasso.with(this).cancelRequest(binding.projectPhotoIv);
        super.onDestroy();
    }

    private void handleArguments() {
        Bundle args = getIntent().getExtras();
        if (args != null) {
            project = Parcels.unwrap(args.getParcelable(EXTRA_PROJECT));
        }
    }

    @Override
    public void setToolbarAlpha(float alpha) {
    }

    @OnClick(R.id.project_comments_ll)
    public void onCommentsClicked() {
        showWebView(project.getCommentsUrl());
    }

    @OnClick(R.id.project_updates_ll)
    public void onUpdatesClicked() {
        showWebView(project.getUpdatesUrl());
    }

    @OnClick(R.id.read_more_btn)
    public void readMorePressed(View view) {
        int MAX_DESCRIPTION_LINES = 1000;
        binding.projectDescriptionTv.setMaxLines(MAX_DESCRIPTION_LINES);

        ViewUtils.showView(view, false);
//        showWebView(project.getProjectUrl());
    }

    @OnClick(R.id.play_video_btn)
    public void playVideo() {
        if (projectDetails == null) {
            ViewUtils.showToast("Getting project details failed. Retrying");
            postProjectDetails();
        } else {
            VideoActivity.showActivity(this, projectDetails);
        }
    }

    @OnClick({R.id.project_author_name_label_tv, R.id.author_photo_iv, R.id.project_author_name_tv})
    public void authorClicked() {
        showWebView(project.getAuthorUrl(), new Intent(this, WebViewFlickrActivity.class));
    }

    private void showWebView(String url) {
        Intent intent = new Intent(this, WebViewActivityV7.class);
        showWebView(url, intent);
    }

    private void showWebView(String url, Intent intent) {
        intent.putExtra(WebViewActivityV7.BUNDLE_URL, url);
        startActivity(intent);
    }

    @Override
    public void onScrollChanged(int deltaX, int deltaY) {
        int scrollY = (int) (binding.scrollView.getScrollY() * 0.6f);
        float newTitleLeft = Math.min(maxTitlesMarginLeft, scrollY * 0.5f);
        float newTitleTop = Math.min(maxTitlesMarginTop, scrollY);
        int newTitlePaddingRight = Math.min(maxTitlePaddingRight, scrollY);

        binding.projectTitleTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, Math.max(titleFontMaxSize - scrollY * 0.05f, titleFontMinSize));
//        binding.projectTitleTv.setTextSize(TypedValue.COMPLEX_UNIT_PX,moveBase(minTitlesMarginTop,maxTitlesMarginTop,titleFontMaxSize,titleFontMinSize,scrollY));

        binding.projectTitleTv.setPadding(0, 0, newTitlePaddingRight, 0);

        binding.projectDetailsTitleContainer.setTranslationX(newTitleLeft);
        binding.projectDetailsTitleContainer.setTranslationY(-newTitleTop);
        binding.detailsContainer.setTranslationY(-newTitleTop);
        binding.playVideoBtn.setTranslationY(-newTitleTop);

        /** Content of scroll view is hiding to early during scroll so we move it also by
         * changing to padding */
        binding.scrollView.setPadding(0, (int) (newTitleTop * 0.6f), 0, 0);


        // Move background photo (parallax effect)
        int parallax = (int) (scrollY * .3f);
        if (maxParallaxValue > parallax) {
            binding.projectPhotoContainer.setTranslationY(-parallax);
        }
    }

    public int moveBase(int xmin, int xmax, int ymin, int ymax, int value) {
        return ymin + ((value - xmin) * (ymax - ymin) / (xmax - xmin));
    }

    private void animateActionButtonVisibility(final boolean videoExist) {
        ActionButton videoBtn = binding.playVideoBtn;
        videoBtn.setEnabled(videoExist);
        Runnable r = () -> {
            // Button alpha may change during transition, we may have to wait until its end
            // to check its state.
            Timber.w("Action btn alpha: " + videoBtn.getAlpha());
            boolean actionBtnShouldAnimate = (videoExist && videoBtn.getAlpha() != 1) || (!videoExist && videoBtn.getAlpha() != 0);
            if (!actionBtnShouldAnimate) {
                return;
            }
            Animator alphaAnimator = AnimatorUtils.getAlphaAnimator(videoBtn, !videoExist);
            alphaAnimator.setDuration(600);
            alphaAnimator.start();
        };
        // Wait just in case transition is still in progress.
        videoBtn.postDelayed(r, ACTION_BUTTON_VISIBILITY_ANIM_DELAY);

    }

    @Subscribe
    public void onProjectDetailsFetched(ProjectDetailsFetchedEvent event) {
        projectDetails = event.getResponse();
        final boolean videoExist = projectDetails.video != null;
        animateActionButtonVisibility(videoExist);
    }
}