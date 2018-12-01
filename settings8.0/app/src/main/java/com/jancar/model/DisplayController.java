package com.jancar.model;

import android.util.Log;

public class DisplayController {
    static {
        System.loadLibrary("JanCar");
    }
    private final static String TAG = "DisplayController";

    public void getPkName(){
        Log.w(TAG, "getPkName: " + getClass().getPackage() );
    };

    public native int nativeGetContrastAdjRange();


    public native int nativeGetContrastAdjIndex();

    /**
     * set the contrast adj index
     * @param index
     * @return
     */
    public native boolean nativeSetContrastAdjIndex(int index);

    /**
     * get the max of brightness
     * @return
     */
    public native int nativeGetBrightnessAdjRange();

    /**
     * get current brightness
     * @return
     */
    public native int nativeGetBrightnessAdjIndex();

    /**
     * set the index value of brightness
     * @param index
     * @return
     */
    public native boolean nativeSetBrightnessAdjIndex(int index);

    public native int nativeGetXAxisRange();

    public native int nativeGetXAxisIndex();

    public native boolean nativeSetXAxisIndex(int index);

    public native int nativeGetYAxisRange();

    public native int nativeGetYAxisIndex();

    public native boolean nativeSetYAxisIndex(int index);

    public native int nativeGetGrassToneHRange();

    public native int nativeGetGrassToneHIndex();

    public native boolean nativeSetGrassToneHIndex(int index);

    public native int nativeGetGrassToneSRange();

    public native int nativeGetGrassToneSIndex();

    public native boolean nativeSetGrassToneSIndex(int index);

    public native int nativeGetHueAdjRange();

    public native int nativeGetHueAdjIndex();

    public native boolean nativeSetHueAdjIndex(int index);

    /**
     * Get the max index of global saturation
     * @return
     */
    public native int nativeGetSatAdjRange();

    /**
     * Get the current index of global saturation
     * @return
     */
    public native int nativeGetSatAdjIndex();

    /**
     * set the index value of global saturation
     * @param index
     * @return
     */
    public native boolean nativeSetSatAdjIndex(int index);

    public native int nativeGetSharpAdjRange();

    public native int nativeGetSharpAdjIndex();

    public native boolean nativeSetSharpAdjIndex(int index);

    /**
     * get the max index of skin tone hue
     * @return
     */
    public native int nativeGetSkinToneHRange();

    /**
     * get the current index of skin tone hue
     * @return
     */
    public native int nativeGetSkinToneHIndex();

    /**
     * set the index of value of skin tone hue
     * @param index
     * @return
     */
    public native boolean nativeSetSkinToneHIndex(int index);

    /**
     * get the max index of skin tone saturation
     * @return
     */
    public native int nativeGetSkinToneSRange();

    /**
     * get the current skin tone saturation
     * @return
     */
    public native int nativeGetSkinToneSIndex();

    /**
     * set the index value of skin tone saturation
     * @param index
     * @return
     */
    public native boolean nativeSetSkinToneSIndex(int index);

    /**
     * get the max index if sky tone hue
     * @return
     */
    public native int nativeGetSkyToneHRange();

    /**
     * get current index of sky tone hue
     * @return
     */
    public native int nativeGetSkyToneHIndex();

    /**
     * set the index value of sky tone hue
     * @param index
     * @return
     */
    public native boolean nativeSetSkyToneHIndex(int index);

    /**
     * get the max index of sky tone saturation
     * @return
     */
    public native int nativeGetSkyToneSRange();

    /**
     * get the current index of sky tone saturation
     * @return
     */
    public native int nativeGetSkyToneSIndex();

    /**
     * set the index value of sky tone saturation
     * @param index
     * @return
     */
    public native boolean nativeSetSkyToneSIndex(int index);

}
