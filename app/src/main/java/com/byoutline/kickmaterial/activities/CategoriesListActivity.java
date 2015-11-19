package com.byoutline.kickmaterial.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.OvershootInterpolator;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.byoutline.cachedfield.CachedFieldWithArg;
import com.byoutline.kickmaterial.KickMaterialApp;
import com.byoutline.kickmaterial.R;
import com.byoutline.kickmaterial.adapters.CategoryClickListener;
import com.byoutline.kickmaterial.databinding.ActivityCategoryListBinding;
import com.byoutline.kickmaterial.managers.CategoriesListViewModel;
import com.byoutline.kickmaterial.model.Category;
import com.byoutline.kickmaterial.model.DiscoverQuery;
import com.byoutline.kickmaterial.model.DiscoverResponse;
import com.byoutline.kickmaterial.utils.AnimatorUtils;
import com.byoutline.kickmaterial.utils.LUtils;
import com.byoutline.kickmaterial.views.CategoriesListSeparator;
import com.byoutline.secretsauce.utils.ViewUtils;
import org.parceler.Parcels;
import timber.log.Timber;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.List;


/**
 * @author Pawel Karczewski <pawel.karczewski at byoutline.com> on 2015-01-03
 */
public class CategoriesListActivity extends KickMaterialBaseActivity implements CategoryClickListener {

    public static final String ARG_CATEGORY = "ARG_CATEGORY";
    public static final int REVEAL_ANIM_DURATION = 400;
    public static final int FINISH_ANIMATION_DURATION = REVEAL_ANIM_DURATION + 100;
    public static final int RESULT_CATEGORY_SELECTED = 13;
    public static final int RESULT_CATEGORY_SELECTION_CANCELED = 17;
    public static final int DEFAULT_REQUEST_CODE = 101;
    private static final String INSTANCE_STATE_SUMMARY_SCROLLED = "INSTANCE_STATE_SUMMARY_SCROLLED";

    @Inject
    CachedFieldWithArg<DiscoverResponse, DiscoverQuery> discoverField;
    @Inject
    CategoriesListViewModel viewModel;

    private Animator revealAnimation;
    private Category category;
    private int summaryScrolledValue;
    private ActivityCategoryListBinding binding;

    public static void launch(@Nonnull Activity context, @Nonnull Category category, View sharedElement) {
        final Bundle options = KickMaterialBaseActivity.getSharedElementsBundle(context, sharedElement);
        Intent intent = new Intent(context, CategoriesListActivity.class);
        intent.putExtra(ARG_CATEGORY, Parcels.wrap(category));
        ActivityCompat.startActivityForResult(context, intent, DEFAULT_REQUEST_CODE, options);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_category_list);

        injectViewsAndSetUpToolbar();
        KickMaterialApp.component.inject(this);
        ButterKnife.bind(this);
        handleArguments();
        setUpAdapters(binding);
        setUpListeners();
        launchPostTransitionAnimations();
    }

    private void setUpListeners() {
        binding.categoriesRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                summaryScrolledValue += dy;
                binding.circleImageContainer.setTranslationY(-0.5f * summaryScrolledValue);
                binding.categoriesHeaderLl.setTranslationY(-summaryScrolledValue);
            }
        });
    }

    private void launchPostTransitionAnimations() {
        if (category != null) {
            int color = ContextCompat.getColor(this, category.colorResId);
            binding.categoryCircleIv.setColorFilter(color);
            binding.selectedCategoryIv.setImageResource(category.drawableResId);
            binding.selectCategoryTv.setBackgroundColor(color);
            binding.selectCategoryTv.getBackground().setAlpha(85);
        }
        if (LUtils.hasL()) {
            binding.closeCategoriesIv.setScaleX(0);
            binding.closeCategoriesIv.setScaleY(0);
            ActivityCompat.setEnterSharedElementCallback(this, new SharedElementCallback() {
                @Override
                public void onSharedElementEnd(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots) {
                    binding.closeCategoriesIv.postDelayed(() -> {
                        // remove listener, we do not want to trigger this animation on exit
                        ActivityCompat.setEnterSharedElementCallback(CategoriesListActivity.this, null);
                        if (isFinishing()) {
                            return;
                        }

                        AnimatorSet closeCategoryAnim = AnimatorUtils.getScaleAnimator(binding.closeCategoriesIv, 0, 1);
                        closeCategoryAnim.setInterpolator(new OvershootInterpolator());
                        closeCategoryAnim.start();
                    }, 160);
                }
            });
        }
        binding.categoriesRv.post(() -> binding.categoriesRv.startAnimation(LUtils.loadAnimationWithLInterpolator(getApplicationContext(), R.anim.slide_from_bottom)));
    }

    private void handleArguments() {
        Bundle args = getIntent().getExtras();
        if (args != null && args.containsKey(ARG_CATEGORY)) {
            category = Parcels.unwrap(args.getParcelable(ARG_CATEGORY));
        } else {
            Timber.e("Category not passed");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        showActionbar(false, false);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(INSTANCE_STATE_SUMMARY_SCROLLED, summaryScrolledValue);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            summaryScrolledValue = savedInstanceState.getInt(INSTANCE_STATE_SUMMARY_SCROLLED);
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    private void setUpAdapters(ActivityCategoryListBinding binding) {
        binding.categoriesRv.addItemDecoration(new CategoriesListSeparator(this.getApplicationContext()));

        int itemColor = R.color.green_primary;
        if (category != null) {
            itemColor = category.colorResId;
        }
        int bgColor = ContextCompat.getColor(this, itemColor);
        viewModel.setAllCategoriesBgColor(bgColor);
        binding.setCategoryClickListener(this);
        binding.setViewModel(viewModel);
    }


    @Override
    public void categoryClicked(View view, Category category) {
        View checkedView = view.findViewById(R.id.checked_view);
        ViewUtils.showView(checkedView, true);
        categoryClicked(category);
    }

    public void categoryClicked(Category category) {
        animateCategoryColor(category);
        // start loading data from API during animation
        discoverField.postValue(DiscoverQuery.getDiscoverQuery(category, 1));
    }


    private Category animateCategoryColor(Category clickedCategory) {
        final int color = ContextCompat.getColor(this, clickedCategory.colorResId);

        binding.selectedCategoryIv.setImageResource(clickedCategory.drawableResId);
        binding.categoryCircleRevealIv.setColorFilter(color);

        animateCircleReveal(color, clickedCategory);
        return clickedCategory;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void animateCircleReveal(final int color, Category category) {
        // get the center for the clipping circle

        int cx = (binding.categoryCircleRevealIv.getLeft() + binding.categoryCircleRevealIv.getRight()) / 2;
        int cy = (binding.categoryCircleRevealIv.getTop() + binding.categoryCircleRevealIv.getBottom());


        int finalRadius = Math.max(binding.categoryCircleRevealIv.getWidth(), binding.categoryCircleRevealIv.getHeight());


        if (LUtils.hasL()) {
            if (revealAnimation != null && revealAnimation.isRunning()) {
                revealAnimation.end();
            }
            revealAnimation = ViewAnimationUtils.createCircularReveal(binding.categoryCircleRevealIv, cx, cy, 0.4f * finalRadius, finalRadius);
            revealAnimation.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    ViewUtils.showView(binding.categoryCircleRevealIv, true);
                    binding.selectCategoryTv.setBackgroundColor(Color.TRANSPARENT);
                    LUtils.setStatusBarColor(CategoriesListActivity.this, color);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    if (binding.categoryCircleIv != null) {
                        binding.categoryCircleIv.setColorFilter(color);
                        binding.selectCategoryTv.setBackgroundColor(color);
                        binding.selectCategoryTv.getBackground().setAlpha(85);
                        binding.categoryCircleRevealIv.setVisibility(View.INVISIBLE);
                    }
                }
            });
            revealAnimation.setDuration(REVEAL_ANIM_DURATION);
            revealAnimation.setInterpolator(new FastOutSlowInInterpolator());
            revealAnimation.start();
        } else {
            binding.categoryCircleIv.setColorFilter(color);
        }
        if (category != null) {
            finishWithResult(category);
        }
    }

    private void finishWithResult(Category category) {
        runFinishAnimation(() -> {
            Intent intent = new Intent();
            intent.putExtra(ARG_CATEGORY, Parcels.wrap(category));
            setResult(RESULT_CATEGORY_SELECTED, intent);
            ActivityCompat.finishAfterTransition(CategoriesListActivity.this);
        });
    }

    private void finishWithoutResult() {
        runFinishAnimation(() -> {
            setResult(RESULT_CATEGORY_SELECTION_CANCELED);
            ActivityCompat.finishAfterTransition(CategoriesListActivity.this);
        });
    }

    private void runFinishAnimation(Runnable finishAction) {
        if (summaryScrolledValue > 0) {
            binding.categoriesRv.smoothScrollToPosition(0);
        }

        ViewUtils.showView(binding.selectCategoryTv, false);
        ObjectAnimator imageFade = ObjectAnimator.ofFloat(binding.selectedCategoryIv, View.ALPHA, 1, 0);
        AnimatorSet set = new AnimatorSet();
        AnimatorSet closeButtonScale = AnimatorUtils.getScaleAnimator(binding.closeCategoriesIv, 1, 0.1f);
        set.playTogether(closeButtonScale, imageFade);
        set.setDuration(FINISH_ANIMATION_DURATION);
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (revealAnimation != null) {
                    revealAnimation.cancel();
                }
                ViewUtils.showView(binding.categoryCircleRevealIv, false);

                binding.closeCategoriesIv.setScaleX(0);
                binding.closeCategoriesIv.setScaleY(0);
                finishAction.run();
            }
        });
        binding.categoriesRv.startAnimation(LUtils.loadAnimationWithLInterpolator(getApplicationContext(), R.anim.slide_to_bottom));
        set.start();
    }

    @Override
    public void onBackPressed() {
        // overwrite back to cancel reveal animation and launch transition.
        finishWithoutResult();
    }

    @OnClick(R.id.close_categories_iv)
    public void onCloseCategories() {
        finishWithoutResult();
    }

    @Override
    public void setToolbarAlpha(float alpha) {
    }
}
