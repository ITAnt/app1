package com.jancar.radio.listener.utils;

import android.os.Build;
import android.util.Log;
import android.view.View;

import com.jancar.radio.listener.utils.v14.ViewHelper14;
import com.jancar.radio.listener.utils.v16.ViewHelper16;


public class ViewHelperFactory {
    private static final String LOG_TAG = "ViewHelper";
    
    public static final ViewHelper create(final View view) {
        final int sdk_INT = Build.VERSION.SDK_INT;
        if (sdk_INT >= 16) {
            return (ViewHelper)new ViewHelper16(view);
        }
        if (sdk_INT >= 14) {
            return (ViewHelper)new ViewHelper14(view);
        }
        return (ViewHelper)new ViewHelperDefault(view);
    }
    
    public abstract static class ViewHelper
    {
        protected View view;
        
        protected ViewHelper(final View view) {
            this.view = view;
        }
        
        public abstract boolean isHardwareAccelerated();
        
        public abstract void postOnAnimation(final Runnable p0);
        
        public abstract void setScrollX(final int p0);
    }
    
    public static class ViewHelperDefault extends ViewHelper
    {
        public ViewHelperDefault(final View view) {
            super(view);
        }
        
        @Override
        public boolean isHardwareAccelerated() {
            return false;
        }
        
        @Override
        public void postOnAnimation(final Runnable runnable) {
            this.view.post(runnable);
        }
        
        @Override
        public void setScrollX(final int n) {
            Log.d("ViewHelper", "setScrollX: " + n);
            this.view.scrollTo(n, this.view.getScrollY());
        }
    }
}
