package com.jancar.bluetooth.phone.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.List;

/**
 * @anthor Tzq
 * @time 2018/11/27 14:38
 * @describe 判断应用是否存在
 */
public class ApplicationUtil {
    public static boolean hasApplication(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);
        for (int i = 0; i < installedPackages.size(); i++) {
            if (installedPackages.get(i).packageName.equalsIgnoreCase(packageName)) {
                return true;
            }
        }
        return false;
    }
}
