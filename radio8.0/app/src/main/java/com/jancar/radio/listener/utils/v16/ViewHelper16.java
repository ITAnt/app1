package com.jancar.radio.listener.utils.v16;

import android.annotation.TargetApi;
import android.view.View;

import com.jancar.radio.listener.utils.v14.ViewHelper14;


public class ViewHelper16 extends ViewHelper14
{
    public ViewHelper16(final View view) {
        super(view);
    }
    
    @TargetApi(16)
    @Override
    public void postOnAnimation(final Runnable runnable) {
        this.view.postOnAnimation(runnable);
    }
}
