package com.jancar.settings.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class EQSoundFiledView extends View {
    private Context context;
    private Paint paintLine;
    private float touchX, touchY;
    private static int START_X_LENGH = 0;
    private static int START_Y_LENGH = 0;
    private static int END_X_LENGH = 0;
    private static int END_Y_LENGH = 0;
    private static int MAX_LENGH = 16;
    private static int MAX_SIZE = 150;
    private int centerX, centerY;
    private int rCircle = 100;
    private Rect inRect = new Rect(START_X_LENGH, START_Y_LENGH, END_X_LENGH,
            END_Y_LENGH);
    private float xValue, yValue;
    private EQSoundFiledListener mEQSoundFiledListener;

    public void setxValue(float xValue) {
        this.xValue = xValue;
    }

    public void setyValue(float yValue) {
        this.yValue = yValue;
    }

    public interface EQSoundFiledListener {
        void notifyLeftRightPisionChange(float left, float right,boolean s);
    };

    public void setEQSoundFiledListener(
            EQSoundFiledListener mEQSoundFiledListener) {
        this.mEQSoundFiledListener = mEQSoundFiledListener;
    }

    public EQSoundFiledView(Context context) {
        super(context);
        initView(context);
    }

    public EQSoundFiledView(Context context, AttributeSet attrs,
                            int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public EQSoundFiledView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        this.context = context;
        paintLine = new Paint();
        paintLine.setColor(0xffffffff);
        paintLine.setStrokeWidth((float) 0.1);
        paintLine.setAntiAlias(true);
        paintLine.setAlpha(0xff);
        Style style = Style.FILL_AND_STROKE;
        paintLine.setStyle(style);
        touchX = centerX - rCircle;
        touchY = centerY - rCircle;
//		initData();
//		calculateXYValue();
    }

    private void initData() {
        touchX = centerX;
        touchY = centerY;
        START_X_LENGH = centerX - rCircle;
        START_Y_LENGH = centerY - rCircle;
        END_X_LENGH = centerX +  rCircle;
        END_Y_LENGH = centerY +  rCircle;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int heigh = measureHeight(heightMeasureSpec);
        int width = measureWidth(widthMeasureSpec);
        getCenterPoint(width, heigh);
    }

    private int measureHeight(int measureSpec) {

        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        int result = 500;
        if (specMode == MeasureSpec.AT_MOST) {

            result = specSize;
        } else if (specMode == MeasureSpec.EXACTLY) {

            result = specSize;
        }

        return result;
    }

    private int measureWidth(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        int result = 500;
        if (specMode == MeasureSpec.AT_MOST) {
            result = specSize;
        }

        else if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        }
        return result;
    }

    private void getCenterPoint(int x, int y) {
        centerX = x / 2;
        centerY = y / 2;
        initData();
        calculateXYValue();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawLine(centerX-rCircle, centerY, END_X_LENGH, centerY, paintLine);
        canvas.drawLine(centerX, centerY-rCircle, centerX, END_Y_LENGH, paintLine);
        paintLine.setTextSize(30);
        canvas.drawText(xValue + "", touchX, 31, paintLine);
        canvas.drawText(yValue + "", END_X_LENGH, touchY, paintLine);
        paintLine.setColor(Color.GREEN);
        canvas.drawCircle(centerX, centerY, 10, paintLine);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (inRect.contains(x, y)) {
                    doInRectActition(x, y,false);
                } else {
                    return false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (inRect.contains(x, y)) {
                    doInRectActition(x, y,false);
                } else {
                    doOutRectAction(x, y,false);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (inRect.contains(x, y)) {
                    doInRectActition(x, y,true);
                } else {
                    doOutRectAction(x, y,true);
                }
                break;
            default:
                break;
        }

        return true;
    }

    private void doInRectActition(int x, int y,boolean s) {
        touchX = x;
        touchY = y;
        calculateXYValue();
        if (mEQSoundFiledListener != null) {
            mEQSoundFiledListener.notifyLeftRightPisionChange(xValue, yValue,s);
        }
        ;
        invalidate();
    }

    private void doOutRectAction(int x, int y,boolean s) {
        if (x <= START_X_LENGH && y <= START_Y_LENGH) {
            touchX = START_X_LENGH;
            touchY = START_Y_LENGH;
        } else if (x <= START_X_LENGH && y > START_Y_LENGH && y <= END_Y_LENGH) {
            touchX = START_X_LENGH;
            touchY = y;
        } else if (x <= START_X_LENGH && y > END_Y_LENGH) {
            touchX = START_X_LENGH;
            touchY = END_Y_LENGH;
        } else if (x > START_X_LENGH && x < END_X_LENGH && y <= START_Y_LENGH) {
            touchX = x;
            touchY = START_Y_LENGH;
        } else if (x > START_X_LENGH && x < END_X_LENGH && y >= END_Y_LENGH) {
            touchX = x;
            touchY = END_Y_LENGH;
        } else if (x >= END_X_LENGH && y <= START_Y_LENGH) {
            touchX = END_X_LENGH;
            touchY = START_Y_LENGH;
        } else if (x >= END_X_LENGH && y > START_Y_LENGH && y <= END_Y_LENGH) {
            touchX = END_X_LENGH;
            touchY = y;
        } else if (x >= END_X_LENGH && y >= END_Y_LENGH) {
            touchX = END_X_LENGH;
            touchY = END_Y_LENGH;
        }
        calculateXYValue();
        if (mEQSoundFiledListener != null) {
            mEQSoundFiledListener.notifyLeftRightPisionChange(xValue, yValue,s);
        }
        ;
        invalidate();
    }

    private void calculateXYValue() {
        float xLengh = touchX - START_X_LENGH;
        float yLengh =touchY - START_Y_LENGH;
        xValue = xLengh * MAX_LENGH / MAX_SIZE;
        yValue = yLengh * MAX_LENGH / MAX_SIZE;
        xValue = xValue - 7;
        yValue = yValue - 7;
    }

}