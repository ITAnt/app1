package com.jancar.radio.listener.utils;

public class Encoding
{
    public static int ASCII = 0;
    public static int BIG5 = 0;
    public static int EUC_JP = 0;
    public static int EUC_KR = 0;
    public static int GB2312 = 0;
    public static int GBK = 0;
    public static final int SIMP = 0;
    public static int SJIS = 0;
    public static int TOTALT = 0;
    public static final int TRAD = 1;
    public static int UNICODE;
    public static int UNKNOWN;
    public static int UTF8;
    public static String[] htmlname;
    public static String[] javaname;
    public static String[] nicename;
    
    static {
        Encoding.GB2312 = 0;
        Encoding.GBK = 1;
        Encoding.BIG5 = 2;
        Encoding.UTF8 = 3;
        Encoding.UNICODE = 4;
        Encoding.EUC_KR = 5;
        Encoding.SJIS = 6;
        Encoding.EUC_JP = 7;
        Encoding.ASCII = 8;
        Encoding.UNKNOWN = 9;
        Encoding.TOTALT = 10;
    }
    
    public Encoding() {
        Encoding.javaname = new String[Encoding.TOTALT];
        Encoding.nicename = new String[Encoding.TOTALT];
        Encoding.htmlname = new String[Encoding.TOTALT];
        Encoding.javaname[Encoding.GB2312] = "GB2312";
        Encoding.javaname[Encoding.GBK] = "GBK";
        Encoding.javaname[Encoding.BIG5] = "BIG5";
        Encoding.javaname[Encoding.UTF8] = "UTF8";
        Encoding.javaname[Encoding.UNICODE] = "Unicode";
        Encoding.javaname[Encoding.EUC_KR] = "EUC_KR";
        Encoding.javaname[Encoding.SJIS] = "SJIS";
        Encoding.javaname[Encoding.EUC_JP] = "EUC_JP";
        Encoding.javaname[Encoding.ASCII] = "ASCII";
        Encoding.javaname[Encoding.UNKNOWN] = "ISO8859_1";
        Encoding.htmlname[Encoding.GB2312] = "GB2312";
        Encoding.htmlname[Encoding.GBK] = "GBK";
        Encoding.htmlname[Encoding.BIG5] = "BIG5";
        Encoding.htmlname[Encoding.UTF8] = "UTF-8";
        Encoding.htmlname[Encoding.UNICODE] = "UTF-16";
        Encoding.htmlname[Encoding.EUC_KR] = "EUC-KR";
        Encoding.htmlname[Encoding.SJIS] = "Shift_JIS";
        Encoding.htmlname[Encoding.EUC_JP] = "EUC-JP";
        Encoding.htmlname[Encoding.ASCII] = "ASCII";
        Encoding.htmlname[Encoding.UNKNOWN] = "ISO8859-1";
        Encoding.nicename[Encoding.GB2312] = "GB-2312";
        Encoding.nicename[Encoding.GBK] = "GBK";
        Encoding.nicename[Encoding.BIG5] = "Big5";
        Encoding.nicename[Encoding.UTF8] = "UTF-8";
        Encoding.nicename[Encoding.UNICODE] = "Unicode";
        Encoding.nicename[Encoding.EUC_KR] = "EUC-KR";
        Encoding.nicename[Encoding.SJIS] = "Shift-JIS";
        Encoding.nicename[Encoding.EUC_JP] = "EUC-JP";
        Encoding.nicename[Encoding.ASCII] = "ASCII";
        Encoding.nicename[Encoding.UNKNOWN] = "UNKNOWN";
    }
    
    public String toEncoding(final int n) {
        return (Encoding.javaname[n] + "," + Encoding.nicename[n] + "," + Encoding.htmlname[n]).intern();
    }
}
