/*
 * Copyright 2014 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.byoutline.kickmaterial.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.*;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.AnimRes;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.internal.view.menu.ActionMenuItemView;
import android.support.v7.internal.widget.TintImageView;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.byoutline.kickmaterial.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;


@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class LUtils {
    private static final int[] STATE_CHECKED = new int[]{android.R.attr.state_checked};
    private static final int[] STATE_UNCHECKED = new int[]{};

    private static Typeface sMediumTypeface;

    protected ActionBarActivity mActivity;
    private Handler mHandler = new Handler();

    private LUtils(ActionBarActivity activity) {
        mActivity = activity;
    }

    public static LUtils getInstance(ActionBarActivity activity) {
        return new LUtils(activity);
    }

    public static boolean hasL() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static void setStatusBarColor(Activity activity, int color) {
        if (!hasL() || activity == null) {
            return;
        }

        activity.getWindow().setStatusBarColor(color);
    }

    public static void setNavigationBarColor(Activity activity, int color) {
        if (!hasL() || activity == null) {
            return;
        }

        activity.getWindow().setNavigationBarColor(color);
    }

    public static void setProgressBarColor(ProgressBar progressBar, int newColor) {
        LayerDrawable ld = (LayerDrawable) progressBar.getProgressDrawable();
        ClipDrawable d1 = (ClipDrawable) ld.findDrawableByLayerId(android.R.id.progress);
        d1.setColorFilter(newColor, PorterDuff.Mode.SRC_ATOP);

    }

    public static void loadBitmapToRoundedImageView(Context context, String url, ImageView imageView, int defaultRes) {
        Picasso.with(context).load(url).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                imageView.setImageBitmap(bitmap);

            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                imageView.setImageResource(defaultRes);
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                imageView.setImageResource(defaultRes);
            }
        });
    }

    public static void toGrayscale(ImageView iv) {
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);

        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        iv.setColorFilter(filter);
    }


    public static Bitmap toGrayscale(Bitmap bmpOriginal) {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }

    public static void colorizeToolbar(Toolbar toolbarView, int toolbarIconsColor, Activity activity) {
        final PorterDuffColorFilter colorFilter
                = new PorterDuffColorFilter(toolbarIconsColor, PorterDuff.Mode.MULTIPLY);

        for (int i = 0; i < toolbarView.getChildCount(); i++) {
            final View v = toolbarView.getChildAt(i);

            //Step 1 : Changing the color of back button (or open drawer button).
            if (v instanceof ImageButton) {
                //Action Bar back button
                ((ImageButton) v).getDrawable().setColorFilter(colorFilter);
            }

            if (v instanceof ActionMenuView) {
                for (int j = 0; j < ((ActionMenuView) v).getChildCount(); j++) {

                    //Step 2: Changing the color of any ActionMenuViews - icons that
                    //are not back button, nor text, nor overflow menu icon.
                    final View innerView = ((ActionMenuView) v).getChildAt(j);

                    if (innerView instanceof ActionMenuItemView) {
                        int drawablesCount = ((ActionMenuItemView) innerView).getCompoundDrawables().length;
                        for (int k = 0; k < drawablesCount; k++) {
                            if (((ActionMenuItemView) innerView).getCompoundDrawables()[k] != null) {
                                final int finalK = k;

                                //Important to set the color filter in seperate thread,
                                //by adding it to the message queue
                                //Won't work otherwise.
                                innerView.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        ((ActionMenuItemView) innerView).getCompoundDrawables()[finalK].setColorFilter(colorFilter);
                                    }
                                });
                            }
                        }
                    }
                }
            }

            //Step 3: Changing the color of title and subtitle.
            toolbarView.setTitleTextColor(toolbarIconsColor);
            toolbarView.setSubtitleTextColor(toolbarIconsColor);

            //Step 4: Changing the color of the Overflow Menu icon.
            setOverflowButtonColor(activity, colorFilter);
        }
    }

    /**
     * It's important to set overflowDescription atribute in styles, so we can grab the reference
     * to the overflow icon. Check: res/values/styles.xml
     *
     * @param activity
     * @param colorFilter
     */
    public static void setOverflowButtonColor(final Activity activity, final PorterDuffColorFilter colorFilter) {
        final String overflowDescription = activity.getString(R.string.abc_action_menu_overflow_description);
        final ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
        final ViewTreeObserver viewTreeObserver = decorView.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                final ArrayList<View> outViews = new ArrayList<View>();
                decorView.findViewsWithText(outViews, overflowDescription,
                        View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
                if (outViews.isEmpty()) {
                    return;
                }
                TintImageView overflow = (TintImageView) outViews.get(0);
                overflow.setColorFilter(colorFilter);
                removeOnGlobalLayoutListener(decorView, this);
            }
        });
    }

    public static void removeOnGlobalLayoutListener(View v, ViewTreeObserver.OnGlobalLayoutListener listener) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            v.getViewTreeObserver().removeGlobalOnLayoutListener(listener);
        } else {
            v.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
        }
    }

    public static Animation loadAnimationWithLInterpolator(Context context, @AnimRes int animId) {
        return loadAnimationWithLInterpolator(context, animId, new LinearOutSlowInInterpolator());
    }

    public static Animation loadAnimationWithLInterpolator(Context context, @AnimRes int animId, android.view.animation.Interpolator interpolator) {
        Animation animation = AnimationUtils.loadAnimation(context, animId);
        animation.setInterpolator(interpolator);
        return animation;
    }

    public void startActivityWithTransition(Intent intent, final View clickedView,
                                            final String transitionName) {
        ActivityOptions options = null;
        if (hasL() && clickedView != null && !TextUtils.isEmpty(transitionName)) {
//            options = ActivityOptions.makeSceneTransitionAnimation(
//                    mActivity, clickedView, transitionName);
        }

        mActivity.startActivity(intent, (options != null) ? options.toBundle() : null);
    }

    public void setMediumTypeface(TextView textView) {
        if (hasL()) {
            if (sMediumTypeface == null) {
                sMediumTypeface = Typeface.create("sans-serif-medium", Typeface.NORMAL);
            }

            textView.setTypeface(sMediumTypeface);
        } else {
            textView.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
        }
    }

    public int getStatusBarColor() {
        if (!hasL()) {
            // On pre-L devices, you can have any status bar color so long as it's black.
            return Color.BLACK;
        }

        return mActivity.getWindow().getStatusBarColor();
    }


    public static int adjustAlpha(int color, float factor) {
        int alpha = Math.round(Color.alpha(color) * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }

}
