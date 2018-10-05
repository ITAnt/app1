package com.ancar.suspension;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Binder;
import android.os.IBinder;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.ancar.suspension.widget.ChildButton;
import com.ancar.suspension.widget.FloatingButton;
import com.ancar.suspension.widget.MagneticMenu;
import com.jancar.JancarServer;
import com.jancar.key.KeyDef;

import static com.ancar.suspension.widget.MagneticMenu.OnDragListener.*;


public class OverlayMenuService extends Service {

    private final IBinder mBinder = new LocalBinder();

    private FloatingButton mainCenterButton;

    private MagneticMenu topCenterMenu;
    JancarServer jancarManager = null;
    
    ImageView tcIcon1;
    ImageView tcIcon2;
    ImageView tcIcon3;
    ImageView tcIcon4;
    ImageView tcIcon5;

    public OverlayMenuService() {
    }


    public class LocalBinder extends Binder {
        OverlayMenuService getService() {
            return OverlayMenuService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @SuppressLint({"ClickableViewAccessibility", "WrongConstant"})
    @Override
    public void onCreate() {
        super.onCreate();
        jancarManager = (JancarServer) getSystemService("jancar_manager");
        int mainActionButtonSize = getResources().getDimensionPixelSize(R.dimen.main_action_button_size);
        int mainActionButtonMargin = getResources().getDimensionPixelOffset(R.dimen.main_action_button_margin);
        int mainActionButtonContentSize = getResources().getDimensionPixelSize(R.dimen.main_action_button_content_size);
        int mainActionButtonContentMargin = getResources().getDimensionPixelSize(R.dimen.main_action_button_content_margin);
        int mainActionMenuRadius = getResources().getDimensionPixelSize(R.dimen.main_action_menu_radius);
        int childActionButtonSize = getResources().getDimensionPixelSize(R.dimen.child_action_button_size);
        int childSubActionButtonContentMargin = getResources().getDimensionPixelSize(R.dimen.child_action_button_content_margin);
    
        Drawable translateBackground = new ColorDrawable(Color.TRANSPARENT);
        
        ImageView fabIconStar = new ImageView(this);
        fabIconStar.setImageDrawable(getResources().getDrawable(R.drawable.iv_sus_centre));
    
//        FloatingButton.LayoutParams fabIconStarParams = new FloatingButton.LayoutParams(mainActionButtonContentSize, mainActionButtonContentSize);
        FloatingButton.LayoutParams fabIconStarParams = new FloatingButton.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
//        fabIconStarParams.setMargins(mainActionButtonContentMargin,
//                mainActionButtonContentMargin,
//                mainActionButtonContentMargin,
//                mainActionButtonContentMargin);

        WindowManager.LayoutParams mainParam = FloatingButton.Builder.getDefaultSystemWindowParams(this);
//        mainParam.width = mainActionButtonSize;
//        mainParam.height = mainActionButtonSize;

        mainCenterButton = new FloatingButton.Builder(this)
                .setSystemOverlay(true)
                .setContentView(fabIconStar, fabIconStarParams)
                .setBackgroundDrawable(translateBackground)
                .setPosition(FloatingButton.POSITION_LEFT_CENTER)
                .setLayoutParams(mainParam)
                .build();
        
        // Set up customized SubActionButtons for the right center menu
        ChildButton.Builder childButtonBuilder = new ChildButton.Builder(this);
        childButtonBuilder.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_action_blue_selector));

        FrameLayout.LayoutParams childBtnParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        childBtnParams.setMargins(childSubActionButtonContentMargin,
                childSubActionButtonContentMargin,
                childSubActionButtonContentMargin,
                childSubActionButtonContentMargin);

        // Set custom layout params
//        FrameLayout.LayoutParams blueParams = new FrameLayout.LayoutParams(childActionButtonSize, childActionButtonSize);
//        childButtonBuilder.setLayoutParams(blueParams);

        tcIcon1 = new ImageView(this);
        tcIcon2 = new ImageView(this);
        tcIcon3 = new ImageView(this);
        tcIcon4 = new ImageView(this);
        tcIcon5 = new ImageView(this);
        
        tcIcon1.setImageDrawable(getResources().getDrawable(R.drawable.sus_power_selector));
        tcIcon2.setImageDrawable(getResources().getDrawable(R.drawable.sus_home_selector));
        tcIcon3.setImageDrawable(getResources().getDrawable(R.drawable.sus_voice_add_selector_left));
        tcIcon4.setImageDrawable(getResources().getDrawable(R.drawable.sus_voice_red_selector_left));
        tcIcon5.setImageDrawable(getResources().getDrawable(R.drawable.sus_back_selector_left));
        
        ChildButton tcSub1 = childButtonBuilder
                .setContentView(tcIcon1, childBtnParams)
                .setBackgroundDrawable(translateBackground)
                .build();
        ChildButton tcSub2 = childButtonBuilder
                .setContentView(tcIcon2, childBtnParams)
                .setBackgroundDrawable(translateBackground)
                .build();
        ChildButton tcSub3 = childButtonBuilder
                .setContentView(tcIcon3, childBtnParams)
                .setBackgroundDrawable(translateBackground)
                .build();
        ChildButton tcSub4 = childButtonBuilder
                .setContentView(tcIcon4, childBtnParams)
                .setBackgroundDrawable(translateBackground)
                .build();
        ChildButton tcSub5 = childButtonBuilder
                .setContentView(tcIcon5, childBtnParams)
                .setBackgroundDrawable(translateBackground)
                .build();

        int sRange = 280;
        // Build another menu with custom options
        topCenterMenu = (MagneticMenu) new MagneticMenu.Builder(this, true)
//                .addSubActionView(tcSub1, tcSub1.getLayoutParams().width, tcSub1.getLayoutParams().height)
//                .addSubActionView(tcSub2, tcSub2.getLayoutParams().width, tcSub2.getLayoutParams().height)
//                .addSubActionView(tcSub3, tcSub3.getLayoutParams().width, tcSub3.getLayoutParams().height)
//                .addSubActionView(tcSub4, tcSub4.getLayoutParams().width, tcSub4.getLayoutParams().height)
//                .addSubActionView(tcSub5, tcSub5.getLayoutParams().width, tcSub5.getLayoutParams().height)
                .addSubActionView(tcSub1, tcSub1.getLayoutParams().width + childSubActionButtonContentMargin * 2, tcSub1.getLayoutParams().height + childSubActionButtonContentMargin * 2)
                .addSubActionView(tcSub2, tcSub2.getLayoutParams().width + childSubActionButtonContentMargin * 2, tcSub2.getLayoutParams().height + childSubActionButtonContentMargin * 2)
                .addSubActionView(tcSub3, tcSub3.getLayoutParams().width + childSubActionButtonContentMargin * 2, tcSub3.getLayoutParams().height + childSubActionButtonContentMargin * 2)
                .addSubActionView(tcSub4, tcSub4.getLayoutParams().width + childSubActionButtonContentMargin * 2, tcSub4.getLayoutParams().height + childSubActionButtonContentMargin * 2)
                .addSubActionView(tcSub5, tcSub5.getLayoutParams().width + childSubActionButtonContentMargin * 2, tcSub5.getLayoutParams().height + childSubActionButtonContentMargin * 2)
                .setRadius(mainActionMenuRadius)
                .setStartAngle(sRange)
                .setEndAngle(sRange + 160)
                .attachTo(mainCenterButton)
                .build();
    
        onMenuFloat(FLOAT_LEFT);

        tcIcon1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jancarManager.requestDisplay(false);
//                topCenterMenu.close(false);

            }
        });
        tcIcon2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jancarManager.simulateKey(KeyDef.KeyType.KEY_HOME.nativeInt);
//                topCenterMenu.close(false);
            }
        });
        tcIcon3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jancarManager.simulateKey(KeyDef.KeyType.KEY_VOL_INC.nativeInt);
//                topCenterMenu.close(false);
            }
        });
        tcIcon4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jancarManager.simulateKey(KeyDef.KeyType.KEY_VOL_DEC.nativeInt);
//                topCenterMenu.close(false);

            }
        });
        tcSub5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jancarManager.simulateKey(KeyDef.KeyType.KEY_BACK.nativeInt);
//                topCenterMenu.close(false);
            }
        });


        topCenterMenu.setDragListener(new MagneticMenu.OnDragListener() {
            @Override
            public void onDragStart() {
                topCenterMenu.setMainActionViewImageSource(R.drawable.iv_sus_centre);
            }

            @Override
            public void onDragEnd(int flag) {
                onMenuFloat(flag);
            }
        });
    }
    
    private void onMenuFloat(int flag) {
        switch (flag) {
            case FLOAT_LEFT:
                tcIcon3.setImageDrawable(getResources().getDrawable(R.drawable.sus_voice_add_selector_left));
                tcIcon4.setImageDrawable(getResources().getDrawable(R.drawable.sus_voice_red_selector_left));
                tcIcon5.setImageDrawable(getResources().getDrawable(R.drawable.sus_back_selector_left));
                topCenterMenu.setBackgroundResource(R.drawable.iv_sus_bg_left);
                topCenterMenu.setMainActionViewImageSource(R.drawable.iv_sus_left_btn);
                topCenterMenu.setAngle(280, 440);
                break;
            case FLOAT_RIGHT:
                tcIcon3.setImageDrawable(getResources().getDrawable(R.drawable.sus_voice_add_selector_right));
                tcIcon4.setImageDrawable(getResources().getDrawable(R.drawable.sus_voice_red_selector_right));
                tcIcon5.setImageDrawable(getResources().getDrawable(R.drawable.sus_back_selector_right));
                topCenterMenu.setBackgroundResource(R.drawable.iv_sus_bg_right);
                topCenterMenu.setMainActionViewImageSource(R.drawable.iv_sus_right_btn);
                topCenterMenu.setAngle(260, 100);
                break;
        }
    }

    @Override
    public void onDestroy() {
        if (topCenterMenu != null && topCenterMenu.isOpen()) topCenterMenu.close(false);
        if (mainCenterButton != null) mainCenterButton.detach();
        super.onDestroy();
    }
}
