package com.jancar.settings.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.jancar.settings.R;

/**
 * ClassName:DspBalance
 *
 * @author Kim
 * @version 1.0
 * @function:平衡调节
 * @Date: 2016-6-27 下午2:13:33
 * @Copyright: Copyright (c) 2016
 */
public class DspBalance extends View {
    private Paint mHorizontalPaint = new Paint();
    private Paint mVerticalPaint = new Paint();
    private Bitmap mObjPoint;
    private float mfFad, mfBal;
    private float mObjWidthParent, mObjHeightParent;

    private int w_ajust, y_ajust;
    private int miDefFad, miDefBal;

    private OnTouchListener mObjTouchListener = null;

    public DspBalance(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public static Bitmap resizeBitmap(Bitmap bitmap, int w, int h) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float scaleWidth = ((float) w) / width;
        float scaleHeight = ((float) h) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width,
                height, matrix, true);
        return resizedBitmap;
    }

    public DspBalance(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub

/*		mObjPoint.setWidth(mObjPoint.getWidth()/2);
        mObjPoint.setHeight(mObjPoint.get/2);*/
        mObjPoint = resizeBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.toyota_dsp_seekbar_point), 30, 30);
        w_ajust = mObjPoint.getWidth() / 2;
        y_ajust = mObjPoint.getHeight() / 2;
        mHorizontalPaint.setColor(Color.RED);
        mVerticalPaint.setColor(Color.RED);
    }

    public void setDefVal(int iMax) {
        this.miDefFad = iMax;

    }

    public void setMaxVal(int iMax) {
        this.miDefBal = iMax;

    }

    public void setBalanceVal(int iBal, int iFad) {
        this.move(iBal * this.getWidth() / miDefBal, iFad * getHeight() / miDefFad);
        invalidate();
        if (mObjTouchListener != null) {
            mObjTouchListener.onBalance(iBal, iFad);
        }
    }

    public void setBalanceVal(int iBal, int iFad, boolean isFromUser) {
        this.move(iBal * this.getWidth() / miDefBal, iFad * getHeight() / miDefFad);
        invalidate();
        if (mObjTouchListener != null) {
            mObjTouchListener.onBalance(iBal, iFad, isFromUser);
        }
    }

    public void updateBalance(int iBal, int iFad) {
        //	Toast.makeText(getContext(), ""+getWidth(), Toast.LENGTH_SHORT).show();
        this.move(iBal * this.getWidth() / miDefBal, iFad * this.getHeight() / miDefFad);
        invalidate();
    }

    public void setBalenceListener(OnTouchListener listener) {
        this.mObjTouchListener = listener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        canvas.drawLine(getWidth() / 2, 0, getWidth() / 2, getHeight(), mVerticalPaint);
        canvas.drawLine(0, getHeight() / 2, getWidth(), getHeight() / 2, mHorizontalPaint);
        canvas.drawBitmap(mObjPoint, mfBal - mObjPoint.getWidth() / 2, mfFad - mObjPoint.getHeight()
                / 2, null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO Auto-generated method stub
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);

        mObjWidthParent = width - 2 * w_ajust;
        mObjHeightParent = height - 2 * y_ajust;

        mfBal = getWidth() / 2 + (miDefBal * mObjWidthParent) / (miDefBal * 2);
        mfFad = getHeight() / 2 - (miDefFad * mObjHeightParent) / (miDefFad * 2);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                move(x, y);
                invalidate();
                if (mObjTouchListener != null) {
                    //	Toast.makeText(getContext(), ""+getWidth(), Toast.LENGTH_SHORT).show();
                    mObjTouchListener.onBalance(mfBal * miDefBal / (getWidth() - w_ajust * 2), mfFad * miDefFad / (getHeight() - w_ajust * 2));
                }
                break;
            default:
                break;
        }

        return true;
    }

    private void move(float x, float y) {
        mfBal = x;
        mfFad = y;

        if (x < w_ajust) {
            mfBal = w_ajust;
        } else if (x > this.getWidth() - w_ajust) {
            mfBal = this.getWidth() - w_ajust;
        }

        if (y < y_ajust) {
            mfFad = y_ajust;
        } else if (y > this.getHeight() - y_ajust) {
            mfFad = this.getHeight() - y_ajust;
        }
    }

    public interface OnTouchListener {
        void onBalance(float fBal, float fFad);

        void onBalance(int fBal, int fFad, boolean isFromUser);
    }
}
