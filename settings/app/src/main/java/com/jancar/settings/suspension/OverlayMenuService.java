package com.jancar.settings.suspension;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.jancar.JancarServer;
import com.jancar.key.KeyDef;
import com.jancar.prompt.PromptController;
import com.jancar.settings.R;
import com.jancar.settings.suspension.entry.OpenedEntry;
import com.jancar.settings.suspension.entry.UpdateEntry;
import com.jancar.settings.suspension.utils.Contacts;
import com.jancar.settings.suspension.widget.ChildButton;
import com.jancar.settings.suspension.widget.FloatingButton;
import com.jancar.settings.suspension.widget.MagneticMenu;
import com.jancar.state.JacState;
import com.orhanobut.hawk.Hawk;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static com.jancar.settings.suspension.widget.MagneticMenu.OnDragListener.FLOAT_LEFT;
import static com.jancar.settings.suspension.widget.MagneticMenu.OnDragListener.FLOAT_RIGHT;


public class OverlayMenuService extends Service implements View.OnClickListener {
    public static final int MENU_CLOSE = 1;

    private final IBinder mBinder = new LocalBinder();

    private FloatingButton mainCenterButton;

    private MagneticMenu topCenterMenu;
    JancarServer jancarManager = null;

    ImageView tcIcon1;
    ImageView tcIcon2;
    ImageView tcIcon3;
    ImageView tcIcon4;
    ImageView tcIcon5;
    private String tcTitle1;
    private String tcTitle2;
    private String tcTitle3;
    private String tcTitle4;
    private String tcTitle5;
    private int Flag = 0;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MENU_CLOSE:
                    closeMenu();
                    break;
            }
        }
    };

    public void resetTime() {
        handler.removeMessages(MENU_CLOSE);
        Message msg = handler.obtainMessage(MENU_CLOSE);
        handler.sendMessageDelayed(msg, 3000);
    }

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
        EventBus.getDefault().register(this);
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

        initImgRes();


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
                .attachTo(mainCenterButton)
                .build();

        onMenuFloat(FLOAT_LEFT);

        tcIcon1.setOnClickListener(this);
        tcIcon2.setOnClickListener(this);
        tcIcon3.setOnClickListener(this);
        tcIcon4.setOnClickListener(this);
        tcIcon5.setOnClickListener(this);

        topCenterMenu.setDragListener(new MagneticMenu.OnDragListener() {
            @Override
            public void onDragStart() {
                topCenterMenu.setMainActionViewImageResource(R.drawable.iv_sus_centre);
            }

            @Override
            public void onDragEnd(int flag) {
                onMenuFloat(flag);
            }
        });

        JacState jacState = new JacState() {
            @Override
            public void OnPhone(boolean bState) {
                super.OnPhone(bState);
                Log.e("OverlayMenuService", "OnPhone===");
                if (bState) {
                    mainCenterButton.setVisibility(View.GONE);
                } else {
                    mainCenterButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void OnBackCar(boolean bState) {
                super.OnBackCar(bState);
                Log.e("OverlayMenuService", "OnBackCar===");
                if (bState) {
                    mainCenterButton.setVisibility(View.GONE);
                } else {
                    mainCenterButton.setVisibility(View.VISIBLE);
                }
            }
        };
        jancarManager.registerJacStateListener(jacState.asBinder());

    }

    private void initImgRes() {
        tcTitle1 = Hawk.get(Contacts.ICON_POS_0, getResources().getString(R.string.tv_power));
        tcTitle2 = Hawk.get(Contacts.ICON_POS_1, getResources().getString(R.string.tv_home));
        tcTitle3 = Hawk.get(Contacts.ICON_POS_2, getResources().getString(R.string.tv_vioce_add));
        tcTitle4 = Hawk.get(Contacts.ICON_POS_3, getResources().getString(R.string.tv_vioce_dec));
        tcTitle5 = Hawk.get(Contacts.ICON_POS_4, getResources().getString(R.string.tv_back));
        setImgResource(tcTitle1, tcIcon1);
        setImgResource(tcTitle2, tcIcon2);
        setImgResource(tcTitle3, tcIcon3);
        setImgResource(tcTitle4, tcIcon4);
        setImgResource(tcTitle5, tcIcon5);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    private void setImgResource(String title, ImageView view) {
        if (title.equals(getResources().getString(R.string.tv_power))) {
            view.setImageResource(R.drawable.sus_power_selector);
        }
        if (title.equals(getResources().getString(R.string.tv_home))) {
            view.setImageResource(R.drawable.sus_home_selector);
        }
        if (title.equals(getResources().getString(R.string.tv_vioce_add))) {
            if (Flag == 0) {
                view.setImageResource(R.drawable.sus_voice_add_selector_left);
            } else if (Flag == 1) {
                view.setImageResource(R.drawable.sus_voice_add_selector_right);
            }
        }
        if (title.equals(getResources().getString(R.string.tv_vioce_dec))) {
            if (Flag == 0) {
                view.setImageResource(R.drawable.sus_voice_red_selector_left);
            } else if (Flag == 1) {
                view.setImageResource(R.drawable.sus_voice_red_selector_right);
            }
        }
        if (title.equals(getResources().getString(R.string.tv_back))) {
            if (Flag == 0) {
                view.setImageResource(R.drawable.sus_back_selector_left);
            } else if (Flag == 1) {
                view.setImageResource(R.drawable.sus_back_selector_right);
            }
        }
    }

    @Override
    public void onClick(View v) {
        int pos = -1;
        if (v == tcIcon1) {
            pos = 0;
        } else if (v == tcIcon2) {
            pos = 1;
        } else if (v == tcIcon3) {
            pos = 2;
        } else if (v == tcIcon4) {
            pos = 3;
        } else if (v == tcIcon5) {
            pos = 4;
        }
        OnSubBtnClick(pos);
    }

    public void OnSubBtnClick(int pos) {
        switch (pos) {
            case 0:
                onPosClick(tcTitle1);
                break;
            case 1:
                onPosClick(tcTitle2);
                break;
            case 2:
                onPosClick(tcTitle3);
                break;
            case 3:
                onPosClick(tcTitle4);
                break;
            case 4:
                onPosClick(tcTitle5);
                break;
        }
    }

    private void onPosClick(String title) {
        resetTime();
        if (title.equals(getResources().getString(R.string.tv_power))) {
            Log.e("OverlayMenuService", "Pos0===");
            jancarManager.requestDisplay(false);
            closeMenu();
        }
        if (title.equals(getResources().getString(R.string.tv_home))) {
            Log.e("OverlayMenuService", "Pos1===");
            jancarManager.simulateKey(KeyDef.KeyType.KEY_HOME.nativeInt);
        }
        if (title.equals(getResources().getString(R.string.tv_vioce_add))) {
            Log.e("OverlayMenuService", "Pos2===");
            jancarManager.simulateKey(KeyDef.KeyType.KEY_VOL_INC.nativeInt);
        }
        if (title.equals(getResources().getString(R.string.tv_vioce_dec))) {
            Log.e("OverlayMenuService", "Pos3===");
            jancarManager.simulateKey(KeyDef.KeyType.KEY_VOL_DEC.nativeInt);
        }
        if (title.equals(getResources().getString(R.string.tv_back))) {
            Log.e("OverlayMenuService", "Pos4===");
            jancarManager.simulateKey(KeyDef.KeyType.KEY_BACK.nativeInt);
        }
    }

    private void onMenuFloat(int flag) {
        switch (flag) {
            case FLOAT_LEFT:
                Flag = 0;
                initImgRes();
                topCenterMenu.setBackgroundResource(R.drawable.iv_sus_bg_left);
                topCenterMenu.setMainActionViewImageResource(R.drawable.iv_sus_left_btn);
                topCenterMenu.setAngle(280, 443);
                break;
            case FLOAT_RIGHT:
                Flag = 1;
                initImgRes();
                topCenterMenu.setBackgroundResource(R.drawable.iv_sus_bg_right);
                topCenterMenu.setMainActionViewImageResource(R.drawable.iv_sus_right_btn);
                topCenterMenu.setAngle(265, 95);
                break;
        }
    }

    @Override
    public void onDestroy() {
        closeMenu();
        if (mainCenterButton != null) mainCenterButton.detach();
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void closeMenu() {
        if (topCenterMenu != null && topCenterMenu.isOpen()) {
            topCenterMenu.close(false);
        }
    }

    /**
     * 事件响应方法
     * 接收消息
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(UpdateEntry event) {
        if (event.isUpdate()) {
            initImgRes();
        }
    }

    /**
     * 事件响应方法
     * 接收消息
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOpenEvent(OpenedEntry event) {
        if (event.isOpen()) {
            handler.sendEmptyMessageDelayed(MENU_CLOSE, 3000);
        } else {
            handler.removeMessages(MENU_CLOSE);
        }
    }
}
