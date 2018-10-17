package com.jancar.bluetooth.phone.view;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.jancar.bluetooth.Listener.BTConnectStatusListener;
import com.jancar.bluetooth.lib.BluetoothManager;
import com.jancar.bluetooth.phone.R;
import com.jancar.bluetooth.phone.util.Constants;
import com.jancar.bluetooth.phone.view.fragment.ContactFragment;
import com.jancar.bluetooth.phone.view.fragment.DialFragment;
import com.jancar.bluetooth.phone.view.fragment.EquipmentFragment;
import com.jancar.bluetooth.phone.view.fragment.RecordsFragment;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.jancar.bluetooth.phone.util.Constants.BT_CONNECT_IS_NONE;

public class MainActivity extends AppCompatActivity implements BTConnectStatusListener {
    @BindView(R.id.radioGroup)
    RadioGroup radioGroup;

    private DialFragment dialFragment;             //拨号
    private ContactFragment contactFragment;       //联系人
    private RecordsFragment recordsFragment;       //通话记录
    private EquipmentFragment equipmentFragment;   //设备管理
    private Unbinder unbinder;
    BluetoothManager bluetoothManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_main_new);
        unbinder = ButterKnife.bind(this);
        initView();
        initListener();

    }

    private void initListener() {
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_keypad:
                        replaceFragment(DialFragment.class.getName());
                        break;
                    case R.id.rb_contact:
                        replaceFragment(ContactFragment.class.getName());
                        break;
                    case R.id.rb_records:
                        replaceFragment(RecordsFragment.class.getName());
                        break;
                    case R.id.rb_mange:
                        replaceFragment(EquipmentFragment.class.getName());
                        break;
                }
            }
        });
    }

    /**
     * 用tempFragment替代当前Fragment, 并给tempFragment增加一个tag，以便下次调用，不用新建
     *
     * @param tag .class.getName
     */
    protected void replaceFragment(String tag) {
        Fragment tempFragment = getSupportFragmentManager().findFragmentByTag(tag);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (tempFragment == null) {
            try {
                tempFragment = (Fragment) Class.forName(tag).newInstance();
                transaction.add(R.id.fragmentContainer, tempFragment, tag);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null) {
            for (int i = 0; i < fragments.size(); i++) {
                Fragment fragment = fragments.get(i);
                if (fragment.getTag().equals(tag)) {
                    transaction.show(fragment);
                } else
                    transaction.hide(fragment);
            }
        }
        transaction.commitAllowingStateLoss();
        System.gc();
    }

    private void initView() {
        dialFragment = new DialFragment();             //拨号
        contactFragment = new ContactFragment();       //联系人
        recordsFragment = new RecordsFragment();       //通话记录
        equipmentFragment = new EquipmentFragment();   //设备管理

    }

    @Override
    protected void onResume() {
        super.onResume();
        bluetoothManager.setBTConnectStatusListener(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
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
        Message message = new Message();
        message.what = Constants.CONTACT_BT_CONNECT;
        message.obj = state;
        handler.sendMessage(message);

    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constants.CONTACT_BT_CONNECT:
                    //蓝牙状态
                    byte obj = (byte) msg.obj;
                    if (obj == BT_CONNECT_IS_NONE) {

                    } else if (obj == Constants.BT_CONNECT_IS_CONNECTED) {

                    } else if (obj == Constants.BT_CONNECT_IS_CLOSE) {

                    }
                    break;
            }

        }
    };
}
