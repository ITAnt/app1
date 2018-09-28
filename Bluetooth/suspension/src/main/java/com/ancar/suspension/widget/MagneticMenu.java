package com.ancar.suspension.widget;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;


import com.ancar.suspension.animation.MenuAnimation;

import java.util.List;

public class MagneticMenu extends FloatingMenu {
    
    /**
     * drag event listener
     */
    public interface OnDragListener {
        int FLOAT_LEFT = 0;
        int FLOAT_RIGHT = 1;
        
        void onDragStart();
        void onDragEnd(int flag);
    }
    
    OnDragListener dragListener;
    
    /**
     * Constructor that takes the parameters collected using {@link Builder}
     * @param mainActionView
     * @param startAngle
     * @param endAngle
     * @param radius
     * @param subActionItems
     * @param animationHandler
     * @param animated
     * @param stateChangeListener
     * @param systemOverlay
     */
    public MagneticMenu(View mainActionView, int startAngle, int endAngle, int radius, List<Item> subActionItems, MenuAnimation animationHandler, boolean animated, MenuStateChangeListener stateChangeListener, boolean systemOverlay) {
        super(mainActionView, startAngle, endAngle, radius, subActionItems, animationHandler, animated, stateChangeListener, systemOverlay);
        mainActionView.setOnTouchListener(onTouchListener);
        super.setStateChangeListener(menuStateChangeListener);
    }
    
    /**
     * move main button to suitable point for sub-buttons when menu opened
     */
    private MenuStateChangeListener menuStateChangeListener = new MenuStateChangeListener() {
        
        WindowManager.LayoutParams lp;
        
        @Override
        public void onMenuOpened(FloatingMenu menu) {
            lp = (WindowManager.LayoutParams) getMainActionView().getLayoutParams();
            int menuHeight = getOverlayContainer().getHeight();
            Rect rect = getMenuDisplayRect();
            if (menu.getActionViewCenter().y < menuHeight / 2) {
                lp.y = -rect.height() / 2 + menuHeight / 2;
            } else if (menu.getActionViewCenter().y > rect.height() - menuHeight / 2) {
                lp.y =  rect.height() / 2 - menuHeight / 2;
            }
//            Log.e("JanCar", "onMenuOpened: " + menu.getActionViewCenter() + " y:" + lp.y);
            updatePosition(lp);
        }
    
        @Override
        public void onMenuClosed(FloatingMenu menu) {
//            updatePosition(lp);
        }
    };
    
    int iCoverSize = 20;
    
    /**
     * set cover size when panel at the edge of the screen
     * @param size
     */
    public void setCoverSize(int size) {
        iCoverSize = size;
    }
    
    /**
     * get sub-button center point around
     * @return
     */
    @Override
    public Point getActionViewCenter() {
        Point point = super.getActionViewCenter();
        Rect rect = getMenuDisplayRect();
        if (point.x < rect.width() / 2) {
            point.x -= iCoverSize;
        } else {
            point.x += iCoverSize;
        }
        return point;
    }
   
    @Override
    protected WindowManager.LayoutParams calculateOverlayContainerParams() {
        WindowManager.LayoutParams params = super.calculateOverlayContainerParams();
        if (params.x < getMenuDisplayRect().width() / 2) {
            params.width += params.x;
            params.x = 0;
        } else {
            params.width += (getMenuDisplayRect().width() - (params.x + params.width));
        }
        return params;
    }
    
    /**
     * set drag listener
     * @param dragListener
     */
    public void setDragListener(OnDragListener dragListener) {
        this.dragListener = dragListener;
    }
    
    /**
     * handle touch event, update display position
     */
    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        int lastX, lastY;
        int currentX, currentY;
        WindowManager.LayoutParams lp;
        boolean bMove = false;
        
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastX = (int) event.getRawX();
                    lastY = (int) event.getRawY();
                    lp = (WindowManager.LayoutParams) v.getLayoutParams();
                    currentX = lp.x;
                    currentY = lp.y;
                    bMove = false;
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (!isOpen()) {
                        int dx = (int) event.getRawX() - lastX;
                        int dy = (int) event.getRawY() - lastY;
                        if (Math.abs(dx) > 5 || Math.abs(dy) > 5) {
                            bMove = true;
                        }
                        lp.x = currentX + dx;
                        lp.y = currentY + dy;
                        
                        if (bMove) {
                            if (dragListener != null) {
                                dragListener.onDragStart();
                            }
//                            getMainActionView().setBackgroundResource(R.drawable.button_action);
                            // 更新悬浮窗位置
                            if (lp.x < getMenuDisplayRect().width() / 2) {
                                setAngle(270, 450);
                            } else {
                                setAngle(270, 90);
                            }
                            updatePosition(lp);
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (!isOpen() && bMove) {
                        int dragFlag = OnDragListener.FLOAT_LEFT;
                        if (lp.x < getMenuDisplayRect().width() / 2) {
                            lp.x = getMenuDisplayRect().left;
                        } else {
                            dragFlag = OnDragListener.FLOAT_RIGHT;
                            lp.x = getMenuDisplayRect().right;
                        }
                        if (dragListener != null) {
                            dragListener.onDragEnd(dragFlag);
                        }
                        updatePosition(lp);
                    }
                    break;
            }
            if (bMove) {
                return false;
            } else {
                return v.onTouchEvent(event);
            }
        }
    };
    
    /**
     * Builder mode
     */
    public static class Builder extends MenuBuilder<MagneticMenu> {
        
        public Builder(Context context) {
            this(context, false);
        }
    
        public Builder(Context context, boolean systemOverlay) {
            super(context, systemOverlay);
        }
        
        @Override
        public MagneticMenu build() {
            return new MagneticMenu(actionView,
                startAngle,
                endAngle,
                radius,
                subActionItems,
                animationHandler,
                animated,
                stateChangeListener,
                systemOverlay);
        }
    }
}
