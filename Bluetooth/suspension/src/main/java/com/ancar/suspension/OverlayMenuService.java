package com.ancar.suspension;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
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


public class OverlayMenuService extends Service {

    private final IBinder mBinder = new LocalBinder();

    private FloatingButton topCenterButton;

    private MagneticMenu topCenterMenu;
    JancarServer jancarManager = null;

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

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate() {
        super.onCreate();
        jancarManager = (JancarServer) getSystemService("jancar_manager");
        int redActionButtonSize = getResources().getDimensionPixelSize(R.dimen.red_action_button_size);
        int redActionButtonMargin = getResources().getDimensionPixelOffset(R.dimen.action_button_margin);
        int redActionButtonContentSize = getResources().getDimensionPixelSize(R.dimen.red_action_button_content_size);
        int redActionButtonContentMargin = getResources().getDimensionPixelSize(R.dimen.red_action_button_content_margin);
        int redActionMenuRadius = getResources().getDimensionPixelSize(R.dimen.red_action_menu_radius);
        int blueSubActionButtonSize = getResources().getDimensionPixelSize(R.dimen.blue_sub_action_button_size);
        int blueSubActionButtonContentMargin = getResources().getDimensionPixelSize(R.dimen.blue_sub_action_button_content_margin);

        ImageView fabIconStar = new ImageView(this);
        fabIconStar.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_important));

        FloatingButton.LayoutParams fabIconStarParams = new FloatingButton.LayoutParams(redActionButtonContentSize, redActionButtonContentSize);
        fabIconStarParams.setMargins(redActionButtonContentMargin,
                redActionButtonContentMargin,
                redActionButtonContentMargin,
                redActionButtonContentMargin);

        WindowManager.LayoutParams mainParam = FloatingButton.Builder.getDefaultSystemWindowParams(this);
        mainParam.width = redActionButtonSize;
        mainParam.height = redActionButtonSize;

        topCenterButton = new FloatingButton.Builder(this)
                .setSystemOverlay(true)
                .setContentView(fabIconStar, fabIconStarParams)
                .setBackgroundDrawable(R.drawable.iv_sus_left_btn)
                .setPosition(FloatingButton.POSITION_LEFT_CENTER)
                .setLayoutParams(mainParam)
                .build();
        // Set up customized SubActionButtons for the right center menu
        ChildButton.Builder tCSubBuilder = new ChildButton.Builder(this);
        tCSubBuilder.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_action_blue_selector));

        ChildButton.Builder tCRedBuilder = new ChildButton.Builder(this);
        tCRedBuilder.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_action_red_selector));

        FrameLayout.LayoutParams childBtnParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        childBtnParams.setMargins(blueSubActionButtonContentMargin,
                blueSubActionButtonContentMargin,
                blueSubActionButtonContentMargin,
                blueSubActionButtonContentMargin);

        // Set custom layout params
        FrameLayout.LayoutParams blueParams = new FrameLayout.LayoutParams(blueSubActionButtonSize, blueSubActionButtonSize);
        tCSubBuilder.setLayoutParams(blueParams);
        tCRedBuilder.setLayoutParams(blueParams);

        ImageView tcIcon1 = new ImageView(this);
        ImageView tcIcon2 = new ImageView(this);
        final ImageView tcIcon3 = new ImageView(this);
        final ImageView tcIcon4 = new ImageView(this);
        final ImageView tcIcon5 = new ImageView(this);
//        ImageView tcIcon6 = new ImageView(this);

        tcIcon1.setImageDrawable(getResources().getDrawable(R.drawable.sus_power_selector));
        tcIcon2.setImageDrawable(getResources().getDrawable(R.drawable.sus_home_selector));
        tcIcon3.setImageDrawable(getResources().getDrawable(R.drawable.sus_voice_add_selector_left));
        tcIcon4.setImageDrawable(getResources().getDrawable(R.drawable.sus_voice_red_selector_left));
        tcIcon5.setImageDrawable(getResources().getDrawable(R.drawable.sus_back_selector_left));
//        tcIcon6.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_cancel));

        ChildButton tcSub1 = tCSubBuilder.setContentView(tcIcon1, childBtnParams).build();
        ChildButton tcSub2 = tCSubBuilder.setContentView(tcIcon2, childBtnParams).build();
        ChildButton tcSub3 = tCSubBuilder.setContentView(tcIcon3, childBtnParams).build();
        ChildButton tcSub4 = tCSubBuilder.setContentView(tcIcon4, childBtnParams).build();
        ChildButton tcSub5 = tCSubBuilder.setContentView(tcIcon5, childBtnParams).build();
//        ChildButton tcSub6 = tCRedBuilder.setContentView(tcIcon6, childBtnParams).build();

        int sRange = 270;
        // Build another menu with custom options
        topCenterMenu = (MagneticMenu) new MagneticMenu.Builder(this, true)
                .addSubActionView(tcSub1, tcSub1.getLayoutParams().width, tcSub1.getLayoutParams().height)
                .addSubActionView(tcSub2, tcSub2.getLayoutParams().width, tcSub2.getLayoutParams().height)
                .addSubActionView(tcSub3, tcSub3.getLayoutParams().width, tcSub3.getLayoutParams().height)
                .addSubActionView(tcSub4, tcSub4.getLayoutParams().width, tcSub4.getLayoutParams().height)
                .addSubActionView(tcSub5, tcSub5.getLayoutParams().width, tcSub5.getLayoutParams().height)
//                .addSubActionView(tcSub6, tcSub6.getLayoutParams().width, tcSub6.getLayoutParams().height)
                .setRadius(redActionMenuRadius)
                .setStartAngle(sRange)
                .setEndAngle(sRange + 180)
                .attachTo(topCenterButton)
                .build();

        tcIcon1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jancarManager.requestDisplay(false);
                topCenterMenu.close(false);

            }
        });
        tcIcon2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jancarManager.simulateKey(KeyDef.KeyType.KEY_HOME.nativeInt);
                topCenterMenu.close(false);
            }
        });
        tcIcon3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jancarManager.simulateKey(KeyDef.KeyType.KEY_VOL_INC.nativeInt);
                topCenterMenu.close(false);
            }
        });
        tcIcon4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jancarManager.simulateKey(KeyDef.KeyType.KEY_VOL_DEC.nativeInt);
                topCenterMenu.close(false);

            }
        });
        tcSub5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jancarManager.simulateKey(KeyDef.KeyType.KEY_BACK.nativeInt);
                topCenterMenu.close(false);
            }
        });


        topCenterMenu.setDragListener(new MagneticMenu.OnDragListener() {
            @Override
            public void onDragStart() {
                topCenterMenu.getMainActionView().setBackgroundResource(R.drawable.iv_sus_centre);
            }

            @Override
            public void onDragEnd(int flag) {
                switch (flag) {
                    case FLOAT_LEFT:
                        tcIcon3.setImageDrawable(getResources().getDrawable(R.drawable.sus_voice_add_selector_left));
                        tcIcon4.setImageDrawable(getResources().getDrawable(R.drawable.sus_voice_red_selector_left));
                        tcIcon5.setImageDrawable(getResources().getDrawable(R.drawable.sus_back_selector_left));
//                        topCenterMenu.getOverlayContainer().setBackgroundColor(Color.GRAY);
                        topCenterMenu.getOverlayContainer().setBackgroundResource(R.drawable.iv_sus_bg_left);
                        topCenterMenu.getMainActionView().setBackgroundResource(R.drawable.iv_sus_left_btn);
                        break;
                    case FLOAT_RIGHT:
                        tcIcon3.setImageDrawable(getResources().getDrawable(R.drawable.sus_voice_add_selector_right));
                        tcIcon4.setImageDrawable(getResources().getDrawable(R.drawable.sus_voice_red_selector_right));
                        tcIcon5.setImageDrawable(getResources().getDrawable(R.drawable.sus_back_selector_right));
//                        topCenterMenu.getOverlayContainer().setBackgroundColor(Color.YELLOW);
                        topCenterMenu.getOverlayContainer().setBackgroundResource(R.drawable.iv_sus_bg_right);
                        topCenterMenu.getMainActionView().setBackgroundResource(R.drawable.iv_sus_right_btn);
                        break;
                }
            }
        });


    }

    @Override
    public void onDestroy() {
        if (topCenterMenu != null && topCenterMenu.isOpen()) topCenterMenu.close(false);
        if (topCenterButton != null) topCenterButton.detach();
        super.onDestroy();
    }
}
