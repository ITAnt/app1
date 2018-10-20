package com.ancar.suspension.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;


import com.ancar.suspension.R;
import com.ancar.suspension.animation.FadeAnimation;
import com.ancar.suspension.animation.MenuAnimation;

import java.util.ArrayList;
import java.util.List;
/**
 * A FloatingActionMenuBuilder for {@link FloatingMenu} in conventional Java FloatingActionMenuBuilder format
 */
public abstract class MenuBuilder<T> {
    
    int startAngle;
    int endAngle;
    int radius;
    View actionView;
    List<FloatingMenu.Item> subActionItems;
    MenuAnimation animationHandler;
    boolean animated;
    FloatingMenu.MenuStateChangeListener stateChangeListener;
    boolean systemOverlay;
    
    public MenuBuilder(Context context, boolean systemOverlay) {
        subActionItems = new ArrayList<>();
        // Default settings
        radius = context.getResources().getDimensionPixelSize(R.dimen.action_menu_radius);
        startAngle = 180;
        endAngle = 270;
        animationHandler = new FadeAnimation();
        animated = true;
        this.systemOverlay = systemOverlay;
    }
    
    public MenuBuilder(Context context) {
        this(context, false);
    }
    
    public MenuBuilder setStartAngle(int startAngle) {
        this.startAngle = startAngle;
        return this;
    }
    
    public MenuBuilder setEndAngle(int endAngle) {
        this.endAngle = endAngle;
        return this;
    }
    
    public MenuBuilder setRadius(int radius) {
        this.radius = radius;
        return this;
    }
    
    public MenuBuilder addSubActionView(View subActionView, int width, int height) {
        subActionItems.add(new FloatingMenu.Item(subActionView, width, height));
        return this;
    }
    
    /**
     * Adds a sub action view that is already alive, but not added to a parent View.
     * @param subActionView a view for the menu
     * @return the FloatingActionMenuBuilder object itself
     */
    public MenuBuilder addSubActionView(View subActionView) {
        if(systemOverlay) {
            throw new RuntimeException("Sub action views cannot be added without " +
                "definite width and height. Please use " +
                "other methods named addSubActionView");
        }
        return this.addSubActionView(subActionView, 0, 0);
    }
    
    /**
     * Inflates a new view from the specified resource id and adds it as a sub action view.
     * @param resId the resource id reference for the view
     * @param context a valid context
     * @return the FloatingActionMenuBuilder object itself
     */
    public MenuBuilder addSubActionView(int resId, Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(resId, null, false);
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        return this.addSubActionView(view, view.getMeasuredWidth(), view.getMeasuredHeight());
    }
    
    /**
     * Sets the current animation handler to the specified MenuAnimationHandler child
     * @param animationHandler a MenuAnimationHandler child
     * @return the FloatingActionMenuBuilder object itself
     */
    public MenuBuilder setAnimationHandler(MenuAnimation animationHandler) {
        this.animationHandler = animationHandler;
        return this;
    }
    
    public MenuBuilder enableAnimations() {
        animated = true;
        return this;
    }
    
    public MenuBuilder disableAnimations() {
        animated = false;
        return this;
    }
    
    public MenuBuilder setStateChangeListener(FloatingMenu.MenuStateChangeListener listener) {
        stateChangeListener = listener;
        return this;
    }
    
    public MenuBuilder setSystemOverlay(boolean systemOverlay) {
        this.systemOverlay = systemOverlay;
        return this;
    }
    
    /**
     * Attaches the whole menu around a main action view, usually a button.
     * All the calculations are made according to this action view.
     * @param actionView
     * @return the FloatingActionMenuBuilder object itself
     */
    public MenuBuilder attachTo(View actionView) {
        this.actionView = actionView;
        return this;
    }
    
    public abstract T build();
}