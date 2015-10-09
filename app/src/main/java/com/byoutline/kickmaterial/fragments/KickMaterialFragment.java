package com.byoutline.kickmaterial.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import butterknife.ButterKnife;
import com.byoutline.kickmaterial.activities.KickMaterialBaseActivity;
import com.byoutline.secretsauce.utils.LogUtils;


public abstract class KickMaterialFragment extends Fragment {

    private static final String TAG = LogUtils.makeLogTag(KickMaterialFragment.class);

    protected HostActivity hostActivity;

    public KickMaterialFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            hostActivity = (HostActivity) activity;
        } catch (ClassCastException e) {
            throw new IllegalStateException(activity.getClass().getSimpleName()
                    + " does not implement " + HostActivity.class.getSimpleName()
                    + " interface");
        }
    }


    public void showBackButtonInActionbar(boolean show) {
        KickMaterialBaseActivity baseActivity = (KickMaterialBaseActivity) getActivity();
        if (baseActivity != null) {
            baseActivity.setDisplayHomeAsUpEnabled(show);
        }
    }


    protected void setUpListeners() {
        //empty by default

    }

    @Override
    public void onDetach() {
        super.onDetach();
        hostActivity = new StubHostActivity();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpValidators();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        setActionbar();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        if (!isAdded()) {
            hostActivity.hideKeyboard();
        }
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public String getScreenName() {
        return getClass().getSimpleName();
    }

    private void setActionbar() {
        KickMaterialBaseActivity baseActivity = (KickMaterialBaseActivity) getActivity();
        if (baseActivity != null) {
            if (!TextUtils.isEmpty(getFragmentActionbarName())) {
                baseActivity.setTitle(getFragmentActionbarName());
            }
//            baseActivity.setDisplayHomeAsUpEnabled(showBackButtonInActionbar());
        }
    }

    public void setActionbarTitle(String title) {
        KickMaterialBaseActivity baseActivity = (KickMaterialBaseActivity) getActivity();
        if (baseActivity != null) {
            if (!TextUtils.isEmpty(title)) {
                baseActivity.setTitle(title);
            }
        }
    }


    public abstract String getFragmentActionbarName();

    public abstract boolean showBackButtonInActionbar();

    protected void setUpValidators() {

    }

    public interface HostActivity extends com.byoutline.secretsauce.activities.HostActivityV4 {
        void enableActionBarAutoHide(final RecyclerView listView);

        void showActionbar(boolean show, boolean animate);

        void setToolbarAlpha(float alpha);

    }

    private static class StubHostActivity extends com.byoutline.secretsauce.activities.StubHostActivityV4 implements HostActivity {

        @Override
        public void enableActionBarAutoHide(RecyclerView listView) {
        }

        @Override
        public void showActionbar(boolean show, boolean animate) {

        }

        @Override
        public void setToolbarAlpha(float alpha) {

        }
    }

    public void fakeOnResume() {
        setActionbarTitle(getFragmentActionbarName());
    }
}
