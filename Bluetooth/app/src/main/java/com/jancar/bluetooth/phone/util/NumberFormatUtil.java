package com.jancar.bluetooth.phone.util;

/**
 * @anthor Tzq
 * @time 2018/8/29 19:59
 * @describe 号码格式化工具类
 */
public class NumberFormatUtil {
    public static String getNumber(String number) {
        if (number == null || number.length() == 0) {
            return null;
        } else {
            if (number.length() == 11 || number.length() < 11) {
                StringBuffer stringBuffer = new StringBuffer();
                for (int i = 0; i < number.length(); i++) {
                    char c = number.charAt(i);
                    stringBuffer.append(c);
                    if (i == 2 || i == 6) {
                        stringBuffer.append(" ");
                    }
                }
                return stringBuffer.toString();
            } else {
                return number;
            }
        }
    }
}
