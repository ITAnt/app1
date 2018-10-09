package com.jancar.bluetooth.phone;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.jancar.bluetooth.Listener.BTConnectStatusListener;
import com.jancar.bluetooth.lib.BluetoothManager;
import com.jancar.bluetooth.phone.view.fragment.ContactFragment;
import com.jancar.bluetooth.phone.view.fragment.DialFragment;
import com.jancar.bluetooth.phone.view.fragment.EquipmentFragment;
import com.jancar.bluetooth.phone.view.fragment.RecordsFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class MainActivity extends AppCompatActivity implements BTConnectStatusListener {
    private static final String TAG = "MainActivity";
    public static final int TAB_DIAL_MANAGER = 1;
    public static final int TAB_CONTACT_MANAGER = 2;
    public static final int TAB_RECORD_MANAGER = 3;
    public static final int TAB_EQUIPMENT_MANAGER = 4;
    public static int indexTab = TAB_DIAL_MANAGER;
    private DialFragment dialFragment;             //拨号
    private ContactFragment contactFragment;       //联系人
    private RecordsFragment recordsFragment;       //通话记录
    private EquipmentFragment equipmentFragment;   //设备管理
    private FragmentManager fragmentManager;
    private Unbinder unbinder;
    @BindView(R.id.tab_dial_manager)
    RelativeLayout dialRelayout;
    @BindView(R.id.tab_contact_manager)
    RelativeLayout contactRelayout;
    @BindView(R.id.tab_records_manager)
    RelativeLayout recordRelayout;
    @BindView(R.id.tab_statics_manager)
    RelativeLayout equipmentRelaout;
    private boolean isConnect;
    BluetoothManager bluetoothManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bluetoothManager = BluetoothManager.getBluetoothManagerInstance(this);
        isConnect = bluetoothManager.isConnect();
        if (isConnect) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
            setContentView(R.layout.activity_main);
            unbinder = ButterKnife.bind(this);
            initComponent();
        } else {
            setTheme(R.style.AlertDialogCustom);
            setContentView(R.layout.dialog_connect);
            findViewById(R.id.tv_connect_dialog_yes).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setClassName("com.jancar.settingss", "com.jancar.settings.view.activity.MainActivity");
                    intent.putExtra("position", 1);
                    startActivity(intent);
                    finish();
                }
            });
            findViewById(R.id.tv_connect_dialog_no).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        bluetoothManager.setBTConnectStatusListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isConnect) {
            unbinder.unbind();
        }
    }

    private void initComponent() {
        fragmentManager = getSupportFragmentManager();
        go2Fragment(indexTab);
    }

    private void go2Fragment(int indexTab) {
        changeTab(indexTab);
        changeTabBg(indexTab);
    }


    public Fragment getFrag(final int tabIndex) {
        if (tabIndex == TAB_DIAL_MANAGER && dialFragment != null) {
            return dialFragment;
        }
        if (tabIndex == TAB_CONTACT_MANAGER && contactFragment != null) {
            return contactFragment;
        }
        if (tabIndex == TAB_RECORD_MANAGER && recordsFragment != null) {
            return recordsFragment;
        }
        if (tabIndex == TAB_EQUIPMENT_MANAGER && equipmentFragment != null) {
            return equipmentFragment;
        }
        return null;
    }


    private void changeTab(int tabIndex) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (dialFragment != null) {
            fragmentTransaction.hide(dialFragment);
        }
        if (contactFragment != null) {
            fragmentTransaction.hide(contactFragment);
        }
        if (recordsFragment != null) {
            fragmentTransaction.hide(recordsFragment);
        }
        if (equipmentFragment != null) {
            fragmentTransaction.hide(equipmentFragment);
        }
        switch (tabIndex) {
            case TAB_DIAL_MANAGER:
                if (dialFragment == null) {
                    dialFragment = new DialFragment();
                    fragmentTransaction.add(R.id.frame_main, dialFragment);
                }
                fragmentTransaction.show(dialFragment);
                indexTab = TAB_DIAL_MANAGER;
                break;
            case TAB_CONTACT_MANAGER:
                if (contactFragment == null) {
                    contactFragment = new ContactFragment();
                    fragmentTransaction.add(R.id.frame_main, contactFragment);
                }
                fragmentTransaction.show(contactFragment);
                indexTab = TAB_CONTACT_MANAGER;
                break;
            case TAB_RECORD_MANAGER:
                if (recordsFragment == null) {
                    recordsFragment = new RecordsFragment();
                    fragmentTransaction.add(R.id.frame_main, recordsFragment);
                }
                fragmentTransaction.show(recordsFragment);
                indexTab = TAB_RECORD_MANAGER;
                break;
            case TAB_EQUIPMENT_MANAGER:
                if (equipmentFragment == null) {
                    equipmentFragment = new EquipmentFragment();
                    fragmentTransaction.add(R.id.frame_main, equipmentFragment);
                }
                fragmentTransaction.show(equipmentFragment);
                indexTab = TAB_EQUIPMENT_MANAGER;
                break;
        }
        fragmentTransaction.commitAllowingStateLoss();
    }

    private void changeTabBg(int indexTab) {
        dialRelayout.setSelected(false);
        contactRelayout.setSelected(false);
        recordRelayout.setSelected(false);
        equipmentRelaout.setSelected(false);
        switch (indexTab) {
            case TAB_DIAL_MANAGER:
                dialRelayout.setSelected(true);
                break;
            case TAB_CONTACT_MANAGER:
                contactRelayout.setSelected(true);
                break;
            case TAB_RECORD_MANAGER:
                recordRelayout.setSelected(true);
                break;
            case TAB_EQUIPMENT_MANAGER:
                equipmentRelaout.setSelected(true);
                break;

        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
    }


    @OnClick({R.id.tab_dial_manager, R.id.tab_contact_manager, R.id.tab_records_manager, R.id.tab_statics_manager})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.tab_dial_manager:
                if (indexTab != TAB_DIAL_MANAGER) {
                    changeTab(TAB_DIAL_MANAGER);
                    changeTabBg(TAB_DIAL_MANAGER);
                }
                break;
            case R.id.tab_contact_manager:
                if (indexTab != TAB_CONTACT_MANAGER) {
                    changeTab(TAB_CONTACT_MANAGER);
                    changeTabBg(TAB_CONTACT_MANAGER);
                }
                break;
            case R.id.tab_records_manager:
                if (indexTab != TAB_RECORD_MANAGER) {
                    changeTab(TAB_RECORD_MANAGER);
                    changeTabBg(TAB_RECORD_MANAGER);
                }
                break;
            case R.id.tab_statics_manager:
                if (indexTab != TAB_EQUIPMENT_MANAGER) {
                    changeTab(TAB_EQUIPMENT_MANAGER);
                    changeTabBg(TAB_EQUIPMENT_MANAGER);
                }
                break;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN &&
                getCurrentFocus() != null &&
                getCurrentFocus().getWindowToken() != null) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {
                hideSoftInput(v.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);

    }

    private boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击EditText的事件，忽略它。
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
        return false;
    }

    /**
     * 获取InputMethodManager，隐藏软键盘
     */
    private void hideSoftInput(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token,
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void onNotifyBTConnectStateChange(byte state) {
        if (state == BluetoothManager.BT_CONNECT_IS_CONNECTED) {
            Message msg = handler.obtainMessage();
            msg.what = state;
            handler.sendMessage(msg);
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == BluetoothManager.BT_CONNECT_IS_CONNECTED) {
                finish();
            }
        }
    };
}
