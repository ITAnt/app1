package com.jancar.radio.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.jancar.radio.R;

public class cropView extends ImageView {
    Paint mPaint=new Paint();
    private String number;
    private boolean Reflection;

    public void setNumber(String number) {
        this.number = number;
        invalidate();
    }

    public String getNumber() {
        return number;
    }

    public cropView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAttrs(context,attrs);
    }


    public cropView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
           super(context, attrs, defStyleAttr);
           initAttrs(context,attrs);
       }

     public cropView(Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
         initAttrs(context,attrs);
        }

        public cropView(Context context) {
            super(context);

        }
    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.Screenshot);
        number = ta.getString(R.styleable.Screenshot_digital);
        Reflection = ta.getBoolean(R.styleable.Screenshot_isReflection,false);
        ta.recycle();

    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Bitmap zero = null;
        Bitmap one ;
         Bitmap two ;
         Bitmap three;
        Bitmap four;
        Bitmap five;
        Bitmap six ;
        Bitmap seven;
        Bitmap eight;
        Bitmap nine;
        Bitmap point;
        Bitmap points;

        Bitmap   resource = BitmapFactory.decodeResource(this.getResources(), R.drawable.num);

        Matrix m = new Matrix();
        m.setScale(1, -1);//垂直翻转


        if (Reflection){

            zero = Bitmap.createBitmap(resource, 504, 0, 56, 88, m, true);
            one = Bitmap.createBitmap(resource, 0, 0, 56, 88, m, true);
            two = Bitmap.createBitmap(resource, 56, 0, 56, 88 , m, true);
            three = Bitmap.createBitmap(resource, 112, 0, 56, 88, m, true);
            four = Bitmap.createBitmap(resource, 168, 0, 56, 88, m, true);
            five = Bitmap.createBitmap(resource, 224, 0, 56, 88, m, true);
            six = Bitmap.createBitmap(resource, 280, 0, 56, 88, m, true);
            seven = Bitmap.createBitmap(resource, 336, 0, 56, 88, m, true);
            eight = Bitmap.createBitmap(resource, 392, 0, 56, 88, m, true);
            nine = Bitmap.createBitmap(resource, 448, 0, 56, 88, m, true);
            point = Bitmap.createBitmap(resource, 560, 0, 56, 88, m, true);

           // canvas.drawBitmap(points, 0, 0, mPaint);
        }else {
            zero = Bitmap.createBitmap(resource, 504, 0, 56, 88);
            one = Bitmap.createBitmap(resource, 0, 0, 56, 88);
            two = Bitmap.createBitmap(resource, 56, 0, 56, 88);
            three = Bitmap.createBitmap(resource, 112, 0, 56, 88);
            four = Bitmap.createBitmap(resource, 168, 0, 56, 88);
            five = Bitmap.createBitmap(resource, 224, 0, 56, 88);
            six = Bitmap.createBitmap(resource, 280, 0, 56, 88);
            seven = Bitmap.createBitmap(resource, 336, 0, 56, 88);
            eight = Bitmap.createBitmap(resource, 392, 0, 56, 88);
            nine = Bitmap.createBitmap(resource, 448, 0, 56, 88);
            point = Bitmap.createBitmap(resource, 560, 0, 56, 88);
        }

        if (number==null){
            number="88.07";
        }
        if (Reflection){
           // mPaint .setAlpha( 75 );   //
        }
        char nums[] = number.toCharArray();
        for (int i = 0; i < nums.length; i++) {

            if (nums[i] == '0') {
                canvas.drawBitmap(zero, i * 56+ (this.getWidth()-number.length()*56)/2, 0, mPaint);
            } else if (nums[i] == '1') {
                canvas.drawBitmap(one, i * 56+ (this.getWidth()-number.length()*56)/2, 0, mPaint);
            } else if (nums[i] == '2') {
                canvas.drawBitmap(two, i * 56+ (this.getWidth()-number.length()*56)/2, 0, mPaint);
            } else if (nums[i] == '3') {
                canvas.drawBitmap(three, i * 56+ (this.getWidth()-number.length()*56)/2, 0, mPaint);
            } else if (nums[i] == '4') {
                canvas.drawBitmap(four, i * 56+ (this.getWidth()-number.length()*56)/2, 0, mPaint);
            } else if (nums[i] == '5') {
                canvas.drawBitmap(five, i * 56+ (this.getWidth()-number.length()*56)/2, 0, mPaint);
            } else if (nums[i] == '6') {
                canvas.drawBitmap(six, i * 56+ (this.getWidth()-number.length()*56)/2, 0, mPaint);
            } else if (nums[i] == '7') {
                canvas.drawBitmap(seven, i * 56+ (this.getWidth()-number.length()*56)/2, 0, mPaint);
            } else if (nums[i] == '8') {
                canvas.drawBitmap(eight, i * 56+ (this.getWidth()-number.length()*56)/2, 0, mPaint);
            } else if(nums[i] == '9') {
                canvas.drawBitmap(nine, i * 56+ (this.getWidth()-number.length()*56)/2, 0, mPaint);
            }else if(nums[i] == '.') {
                canvas.drawBitmap(point, i * 56+ (this.getWidth()-number.length()*56)/2, 0, mPaint);
            }
        }

    }


 public Bitmap addShadow(Bitmap bm) {
        int[] mBackShadowColors = new int[] { Color.parseColor("#00000000") , Color.parseColor("#FF000000")};
        GradientDrawable    mBackShadowDrawableLR = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM, mBackShadowColors);
        mBackShadowDrawableLR.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        mBackShadowDrawableLR.setBounds(0, 0, bm.getWidth() , bm.getHeight());
        Canvas canvas = new Canvas(bm);
        mBackShadowDrawableLR.draw(canvas);
        return bm;
    }
}
