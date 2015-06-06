package com.melodicloud.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.ViewGroup;

import com.melodicloud.R;
import com.melodicloud.common.Global;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

/**
 * Created by AhmedSalem on 1/18/15.
 */
@EActivity
public abstract class BaseActivity extends ActionBarActivity {


    String lastRequestCacheKey;

    /**
     * progress view
     */
    @ViewById(R.id.main_progress_view)
    ViewGroup progressView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Global.appContext = this;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    @AfterViews
    protected void onViewsLoadedInBase() {
        hideProgressView();

        onViewsLoaded();
    }


    public abstract void onViewsLoaded();


    public void hideProgressView() {
        if (progressView != null) progressView.setVisibility(View.GONE);
    }


    /**
     * Show progress View
     */
    public void showProgressView() {
        if (progressView != null) progressView.setVisibility(View.VISIBLE);
    }
}
