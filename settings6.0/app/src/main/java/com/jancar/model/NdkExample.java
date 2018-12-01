package com.jancar.model;

/**
 * Created by ouyan on 2018/10/23.
 */

public class NdkExample {

    public native String nativeGetNdkString();

    public native void nativeGreetingNdk(String str, int i);
}
