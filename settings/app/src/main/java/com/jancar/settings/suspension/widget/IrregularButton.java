package com.jancar.settings.suspension.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageButton;

/**
 * 功能: 不规则按键
 * 用法:
 *      src: 指定点击有效区域,图片alpha非0则表示有效点击区域
 *      background: 按钮显示效果
 * @author hzGuo
 * @date 2018-9-20 21:10:17
 */
@SuppressLint("AppCompatCustomView")
class IrregularButton extends ImageButton {
    
    private Bitmap bmpTouch = null;
    
    public IrregularButton(Context context) {
        this(context, null);
    }
    
    public IrregularButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    
    public IrregularButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Drawable drawable = getDrawable();
        if (drawable != null) {
            bmpTouch = ((BitmapDrawable) drawable).getBitmap();
        } else {
            drawable = getBackground();
            if (drawable != null) {
                bmpTouch = ((BitmapDrawable) drawable).getBitmap();
            }
        }
        setImageDrawable(null);
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        try {
            if (bmpTouch != null) {
                int x = (int) (event.getX() / ((float) getWidth() / bmpTouch.getWidth()));
                int y = (int) (event.getY() / ((float) getHeight() / bmpTouch.getHeight()));
                int pixel = bmpTouch.getPixel(x, y);
                if (((pixel >> 24) & 0xff) > 0) {
                    return super.onTouchEvent(event);
                } else {
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return super.onTouchEvent(event);
    }
}
