package com.jancar.bluetooth.phone.util;

import android.app.VoiceInteractor;
import android.content.Context;

import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Options;

import java.util.List;

/**
 * @anthor Tzq
 * @time 2018/11/15 11:16
 * @describe 权限申请工具类
 */
public class PermissionRequestUtil {
    //手机、联系人CODE
    public static final int PHONE_CONTACTS_CODE = 2000;
    private Context mContext;

    private Options options;

    public PermissionRequestUtil(Context context) {
        this.mContext = context;
         options = AndPermission.with(mContext);
    }

    }



