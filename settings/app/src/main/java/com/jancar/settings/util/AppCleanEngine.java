package com.jancar.settings.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.RemoteException;
import android.os.StatFs;
import android.util.Log;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class AppCleanEngine {

	private PackageManager packageManager;

	private Context mContext;

	public AppCleanEngine(Context context) {
		this.mContext = context;
	}

	public ArrayList<String> scanAppList() {
		ArrayList<String> results = new ArrayList<String>();
		if (packageManager == null) {
			packageManager = mContext.getPackageManager();
		}
		@SuppressLint("WrongConstant") List<ApplicationInfo> appinfos = packageManager
				.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES
						| PackageManager.GET_ACTIVITIES);
		for (ApplicationInfo info : appinfos) {
			results.add(info.packageName);
		}
		return results;
	}

	public void cleanAppCacheByPackage(String packageName) {
		try {
			Method deleteApplicationCacheFiles = packageManager.getClass()
					.getDeclaredMethod("deleteApplicationCacheFiles",
							String.class, IPackageDataObserver.class);
			deleteApplicationCacheFiles.invoke(packageManager, packageName,
					mDataObserver);
		} catch (NoSuchMethodException e) {
			Log.e("ygl","NoSuchMethodException:"+e);
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			Log.e("ygl","InvocationTargetException:"+e);
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			Log.e("ygl","IllegalAccessException:"+e);
			e.printStackTrace();
		}
	}

	public void cleanAppCache(List<String> list) {
		for (String packageName : list) {
			cleanAppCacheByPackage(packageName);
		}
	}

	private IPackageDataObserver.Stub mDataObserver = new IPackageDataObserver.Stub() {
		@Override
		public void onRemoveCompleted(String packageName, boolean succeeded)
				throws RemoteException {
		}
	};

	public void setDataObserver(IPackageDataObserver.Stub dataObserver) {
		this.mDataObserver = dataObserver;
	}

	public void cleanAllCache() {
		try {
			if (packageManager == null) {
				packageManager = mContext.getPackageManager();
			}
			Method localMethod = packageManager.getClass().getMethod(
					"freeStorageAndNotify", Long.TYPE,
					IPackageDataObserver.class);
			Long localLong = getEnvironmentSize() - 1L;
			Object[] arrayOfObject = new Object[2];
			arrayOfObject[0] = localLong;
			localMethod.invoke(packageManager, localLong, mDataObserver);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private long getEnvironmentSize() {
		File localFile = Environment.getDataDirectory();
		long l1;
		if (localFile == null) {
			return 0L;
		}
		while (true) {
			String str = localFile.getPath();
			StatFs localStatFs = new StatFs(str);
			long l2 = localStatFs.getBlockSize();
			l1 = localStatFs.getBlockCount() * l2;
			return l1;
		}
	}

}