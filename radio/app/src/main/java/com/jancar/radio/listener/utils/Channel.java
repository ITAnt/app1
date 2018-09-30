package com.jancar.radio.listener.utils;

import android.content.Context;
import android.text.TextUtils;

import com.jancar.utils.Logcat;

import java.lang.reflect.Field;

public enum Channel
{
    BlueGray, 
    black, 
    blue, 
    chr, 
    gray, 
    metal, 
    none, 
    vertical;
    
    public static Channel getChannel(final Context context) {
        if (context == null) {
            Logcat.w("context is null");
            return Channel.gray;
        }
        final String lowerCase = getChannelEx(context).toLowerCase();
        Logcat.d(lowerCase);
        if ("chr".equals(lowerCase)) {
            return Channel.chr;
        }
        if ("blue".equals(lowerCase)) {
            return Channel.blue;
        }
        if ("black".equals(lowerCase)) {
            return Channel.black;
        }
        if ("gray".equals(lowerCase)) {
            return Channel.gray;
        }
        if ("metal".equals(lowerCase)) {
            return Channel.metal;
        }
        if ("vertical".equals(lowerCase)) {
            return Channel.vertical;
        }
        if ("bluegray".equals(lowerCase)) {
            return Channel.BlueGray;
        }
        return Channel.none;
    }

    public static String getChannelEx(final Context context) {
        String string = "";
        if (context == null) {
            Logcat.w("context is null");
            return string;
        }
        try {
            final String packageName = context.getPackageName();
            if (!TextUtils.isEmpty((CharSequence)packageName)) {
                final Class<?> forName = Class.forName(packageName + ".BuildConfig");
                if (forName != null) {
                    final Field field = forName.getField("FLAVOR");
                    if (field != null) {
                        final Object value = field.get(forName);
                        if (value != null) {
                            string = value.toString();
                        }
                    }
                }
            }
            return string;
        }
        catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            return string;
        }
        catch (NoSuchFieldException ex2) {
            ex2.printStackTrace();
            return string;
        }
        catch (IllegalAccessException ex3) {
            ex3.printStackTrace();
            return string;
        }
        catch (Exception ex4) {
            ex4.printStackTrace();
            return string;
        }
    }
    public static boolean isChannelBlack(final Context context) {
        return getChannel(context).equals(Channel.black);
    }
    
    public static boolean isChannelBlue(final Context context) {
        return getChannel(context).equals(Channel.blue);
    }
    
    public static boolean isChannelBlueGray(final Context context) {
        return getChannel(context).equals(Channel.BlueGray);
    }
    
    public static boolean isChannelCHR(final Context context) {
        return getChannel(context).equals(Channel.chr);
    }
    
    public static boolean isChannelGray(final Context context) {
        return getChannel(context).equals(Channel.gray);
    }
    
    public static boolean isChannelMetal(final Context context) {
        return getChannel(context).equals(Channel.metal);
    }
    
    public static boolean isChannelVertical(final Context context) {
        return getChannel(context).equals(Channel.vertical);
    }
}
