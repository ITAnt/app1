package com.jancar.radio.listener.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.widget.Toast;

public abstract class AbsToast
{
    protected Toast mToast;
    
    public final void cancelToast() {
        if (this.mToast != null) {
            this.mToast.cancel();
            this.mToast = null;
        }
    }
    
    public final void showToast(@StringRes final int n, @NonNull final Context context) {
        if (context != null) {
            this.showToast(context.getText(n), context);
        }
    }
    
    public abstract void showToast(final CharSequence p0, final Context p1);
}
