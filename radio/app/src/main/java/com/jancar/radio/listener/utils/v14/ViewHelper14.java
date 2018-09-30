package com.jancar.radio.listener.utils.v14;

import android.annotation.TargetApi;
import android.view.View;

import com.jancar.radio.listener.utils.ViewHelperFactory;

public class ViewHelper14 extends ViewHelperFactory.ViewHelperDefault
{
    public ViewHelper14(final View view) {
        super(view);
    }
    
    @TargetApi(11)
    @Override
    public boolean isHardwareAccelerated() {
        return this.view.isHardwareAccelerated();
    }
    
    @TargetApi(14)
    @Override
    public void setScrollX(final int scrollX) {
        this.view.setScrollX(scrollX);
    }
}
