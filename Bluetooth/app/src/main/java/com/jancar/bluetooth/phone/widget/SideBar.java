package com.jancar.bluetooth.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;


/**
 * @anthor tzq
 * @time 2018/8/21
 * 联系人界面listview右侧索引
 */

public class SideBar extends View {
    // 触摸事件
    private OnTouchingLetterChangedListener onTouchingLetterChangedListener;
    // 26个字母
    public static String[] b = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L",
            "M", "N", "O", "P", "Q",
            "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "#"};


    private int choose = -1;// 选中
    private Paint paint = new Paint();

    private TextView mTextDialog;

    private Context mContext;
    //SideBar上字母绘制的矩形区域
    private Rect textRect = new Rect();

    public void setTextView(TextView mTextDialog) {
        this.mTextDialog = mTextDialog;
    }

    public SideBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
    }

    public SideBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SideBar(Context context) {
        this(context, null);
    }

    public void setLetter(String[] letters) {
        b = letters;
        invalidate();
    }


    /**
     * 重写这个方法
     */
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 获取焦点改变背景颜色.
        int height = getHeight();// 获取对应高度
        int width = getWidth(); // 获取对应宽度

        float singleHeight = (height * 1.0f) / b.length;// 获取每一个字母的高度
        singleHeight = (height * 1.0f - singleHeight / 2) / b.length;
        int textSize = (int) ((width > singleHeight ? singleHeight : width) * (3.0f / 4));
        //半径
        int circleRadius = (int) (singleHeight / 2);
        for (int i = 0; i < b.length; i++) {
            paint.setColor(Color.parseColor("#ffffff"));
            paint.setTypeface(Typeface.DEFAULT);
            paint.setAntiAlias(true);//抗锯齿
            paint.setTextSize(textSize);
            // x坐标等于中间-字符串宽度的一半.
            float yPos = singleHeight * i + singleHeight;
            float xPos = ((float) width - paint.measureText(b[i])) / 2.0f;
            //选中时显示背景
            if (i == choose) {
                Rect rect = new Rect();
                paint.setColor(Color.parseColor("#0068b7"));
                paint.getTextBounds(b[i], 0, 1, rect);
                canvas.drawCircle(getWidth() / 2, yPos - (circleRadius - rect.height() / 2), circleRadius, paint);
                paint.setFakeBoldText(true);
            }
            paint.setColor(Color.rgb(102, 102, 102));
            if (i == choose) {
                paint.setColor(Color.WHITE);
            }
//            // x坐标等于中间-字符串宽度的一半.
            canvas.drawText(b[i], xPos, yPos, paint);
            paint.reset();// 重置画笔
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        final float y = event.getY();// 点击y坐标
        final int oldChoose = choose;
        final OnTouchingLetterChangedListener listener = onTouchingLetterChangedListener;
        final int c = (int) (y / getHeight() * b.length);// 点击y坐标所占总高度的比例*b数组的长度就等于点击b中的个数.

        switch (action) {
            case MotionEvent.ACTION_UP:
                setBackgroundDrawable(new ColorDrawable(0x00000000));
                choose = -1;//
                invalidate();
                if (mTextDialog != null) {
                    mTextDialog.setVisibility(View.INVISIBLE);
                }
                break;

            default:
                setBackgroundDrawable(new ColorDrawable(0x00000000));
                if (oldChoose != c) {
                    if (c >= 0 && c < b.length) {
                        if (listener != null) {
                            listener.onTouchingLetterChanged(b[c]);
                        }
                        if (mTextDialog != null) {
                            mTextDialog.setText(b[c]);
                            mTextDialog.setVisibility(View.VISIBLE);
                        }
                        choose = c;
                        invalidate();
                    }
                }

                break;
        }
        return true;
    }

    /**
     * 向外公开的方法
     *
     * @param onTouchingLetterChangedListener
     */
    public void setOnTouchingLetterChangedListener(OnTouchingLetterChangedListener
                                                           onTouchingLetterChangedListener) {
        this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;
    }

    /**
     * 接口
     *
     * @author coder
     */
    public interface OnTouchingLetterChangedListener {
        void onTouchingLetterChanged(String s);
    }

}
