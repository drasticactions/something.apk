package com.salvadordalvik.something;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.salvadordalvik.fastlibrary.FastFragment;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

/**
 * Created by matthewshepard on 3/5/14.
 */
public abstract class SomeFragment extends FastFragment {
    private SmoothProgressBar progressBar;

    public SomeFragment(int layoutId) {
        super(layoutId);
    }

    public SomeFragment(int layoutId, int menuId) {
        super(layoutId, menuId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View frag = super.onCreateView(inflater, container, savedInstanceState);

        View progress = frag.findViewById(R.id.progressbar);
        if(progress instanceof SmoothProgressBar){
            progressBar = (SmoothProgressBar) progress;
        }
        return frag;
    }

    @Override
    public void setRefreshAnimation(boolean refreshing) {
        super.setRefreshAnimation(refreshing);
        if(progressBar != null){
            progressBar.setVisibility(refreshing ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    protected void setProgress(int newProgress) {
        if(progressBar != null){
            if(newProgress > 98){
                progressBar.setVisibility(View.GONE);
            }else{
                if(progressBar.getVisibility() != View.VISIBLE){
                    progressBar.setVisibility(View.VISIBLE);
                }
                progressBar.setSmoothProgressDrawableSpeed(newProgress/40f+0.1f);
            }
        }else{
            super.setProgress(newProgress);
        }
    }

    @Override
    public void onRefreshStarted(View view) {
        super.onRefreshStarted(view);
        if(progressBar != null){
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setSmoothProgressDrawableSpeed(1f);
        }
    }

    @Override
    public void onRefreshCompleted() {
        super.onRefreshCompleted();
        if(progressBar != null){
            progressBar.setVisibility(View.GONE);
        }
    }
}