package com.jancar.radio.listener.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;

public class ParseEncoding extends Encode
{
    public ParseEncoding() {
        this.GB2312format = (int[][])Array.newInstance(Integer.TYPE, 94, 94);
        this.GBKformat = (int[][])Array.newInstance(Integer.TYPE, 126, 191);
        this.Big5format = (int[][])Array.newInstance(Integer.TYPE, 94, 158);
        this.EUC_KRformat = (int[][])Array.newInstance(Integer.TYPE, 94, 94);
        this.JPformat = (int[][])Array.newInstance(Integer.TYPE, 94, 94);
        this.init();
    }
    
    private int asciiprobability(final byte[] array) {
        int n = 75;
        for (int length = array.length, i = 0; i < length; ++i) {
            if (array[i] < 0) {
                n -= 5;
            }
            else if (array[i] == 27) {
                n -= 5;
            }
            if (n <= 0) {
                return 0;
            }
        }
        return n;
    }
    
    private int big5probability(final byte[] array) {
        int n = 1;
        int n2 = 1;
        long n3 = 0L;
        long n4 = 1L;
        for (int length = array.length, i = 0; i < length - 1; ++i) {
            if (array[i] < 0) {
                ++n;
                if (-95 <= array[i] && array[i] <= -7 && ((64 <= array[i + 1] && array[i + 1] <= 126) || (-95 <= array[i + 1] && array[i + 1] <= -2))) {
                    ++n2;
                    n4 += 500L;
                    final int n5 = -161 + (256 + array[i]);
                    int n6;
                    if (64 <= array[i + 1] && array[i + 1] <= 126) {
                        n6 = -64 + array[i + 1];
                    }
                    else {
                        n6 = -97 + (256 + array[i + 1]);
                    }
                    if (this.Big5format[n5][n6] != 0) {
                        n3 += this.Big5format[n5][n6];
                    }
                    else if (3 <= n5 && n5 <= 37) {
                        n3 += 200L;
                    }
                }
                ++i;
            }
        }
        return (int)(50.0f * (n2 / n) + 50.0f * (n3 / n4));
    }
    
    private String check(final int n) {
        if (n == -1) {
            return ParseEncoding.nicename[ParseEncoding.UNKNOWN];
        }
        return ParseEncoding.nicename[n];
    }
    
    private int euc_jpprobability(final byte[] array) {
        int n = 1;
        int n2 = 1;
        long n3 = 0L;
        long n4 = 1L;
        for (int length = array.length, i = 0; i < length - 1; ++i) {
            if (array[i] < 0) {
                ++n;
                if (-95 <= array[i] && array[i] <= -2 && -95 <= array[i + 1] && array[i + 1] <= -2) {
                    ++n2;
                    n4 += 500L;
                    final int n5 = -161 + (256 + array[i]);
                    final int n6 = -161 + (256 + array[i + 1]);
                    if (this.JPformat[n5][n6] != 0) {
                        n3 += this.JPformat[n5][n6];
                    }
                    else if (15 <= n5 && n5 < 55) {
                        n3 += 0L;
                    }
                }
                ++i;
            }
        }
        return (int)(50.0f * (n2 / n) + 50.0f * (n3 / n4));
    }
    
    private int euc_krprobability(final byte[] array) {
        int n = 1;
        int n2 = 1;
        long n3 = 0L;
        long n4 = 1L;
        for (int length = array.length, i = 0; i < length - 1; ++i) {
            if (array[i] < 0) {
                ++n;
                if (-95 <= array[i] && array[i] <= -2 && -95 <= array[i + 1] && array[i + 1] <= -2) {
                    ++n2;
                    n4 += 500L;
                    final int n5 = -161 + (256 + array[i]);
                    final int n6 = -161 + (256 + array[i + 1]);
                    if (this.EUC_KRformat[n5][n6] != 0) {
                        n3 += this.EUC_KRformat[n5][n6];
                    }
                    else if (15 <= n5 && n5 < 55) {
                        n3 += 0L;
                    }
                }
                ++i;
            }
        }
        return (int)(50.0f * (n2 / n) + 50.0f * (n3 / n4));
    }
    
    private int gb2312probability(final byte[] array) {
        int n = 1;
        int n2 = 1;
        long n3 = 0L;
        long n4 = 1L;
        for (int length = array.length, i = 0; i < length - 1; ++i) {
            if (array[i] < 0) {
                ++n;
                if (-95 <= array[i] && array[i] <= -9 && -95 <= array[i + 1] && array[i + 1] <= -2) {
                    ++n2;
                    n4 += 500L;
                    final int n5 = -161 + (256 + array[i]);
                    final int n6 = -161 + (256 + array[i + 1]);
                    if (this.GB2312format[n5][n6] != 0) {
                        n3 += this.GB2312format[n5][n6];
                    }
                    else if (15 <= n5 && n5 < 55) {
                        n3 += 200L;
                    }
                }
                ++i;
            }
        }
        return (int)(50.0f * (n2 / n) + 50.0f * (n3 / n4));
    }
    
    private int gbkprobability(final byte[] array) {
        int n = 1;
        int n2 = 1;
        long n3 = 0L;
        long n4 = 1L;
        for (int length = array.length, i = 0; i < length - 1; ++i) {
            if (array[i] < 0) {
                ++n;
                if (-95 <= array[i] && array[i] <= -9 && -95 <= array[i + 1] && array[i + 1] <= -2) {
                    ++n2;
                    n4 += 500L;
                    final int n5 = -161 + (256 + array[i]);
                    final int n6 = -161 + (256 + array[i + 1]);
                    if (this.GB2312format[n5][n6] != 0) {
                        n3 += this.GB2312format[n5][n6];
                    }
                    else if (15 <= n5 && n5 < 55) {
                        n3 += 200L;
                    }
                }
                else if (-127 <= array[i] && array[i] <= -2 && ((-128 <= array[i + 1] && array[i + 1] <= -2) || (64 <= array[i + 1] && array[i + 1] <= 126))) {
                    ++n2;
                    n4 += 500L;
                    final int n7 = -129 + (256 + array[i]);
                    int n8;
                    if (64 <= array[i + 1] && array[i + 1] <= 126) {
                        n8 = -64 + array[i + 1];
                    }
                    else {
                        n8 = -64 + (256 + array[i + 1]);
                    }
                    if (this.GBKformat[n7][n8] != 0) {
                        n3 += this.GBKformat[n7][n8];
                    }
                }
                ++i;
            }
        }
        return -1 + (int)(50.0f * (n2 / n) + 50.0f * (n3 / n4));
    }
    
    private int getEncodeValue(final String s) {
        final int unknown = ParseEncoding.UNKNOWN;
        if (s.startsWith("http://")) {
            try {
                return this.getEncodeValue(new URL(s));
            }
            catch (MalformedURLException ex) {
                return -1;
            }
        }
        return this.getEncodeValue(new File(s));
    }
    
    private final byte[] read(final InputStream inputStream) {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final byte[] array = new byte[8192];
        try {
            final byte[] array2 = new byte[inputStream.available()];
            while (true) {
                final int read = inputStream.read(array2);
                if (read < 0) {
                    break;
                }
                byteArrayOutputStream.write(array2, 0, read);
            }
            return byteArrayOutputStream.toByteArray();
        }
        catch (IOException ex) {
            return null;
        }
    }
    
    private int sjisprobability(final byte[] array) {
        int n = 1;
        int n2 = 1;
        long n3 = 0L;
        long n4 = 1L;
        for (int length = array.length, i = 0; i < length - 1; ++i) {
            if (array[i] < 0) {
                ++n;
                if (i + 1 < array.length && ((-127 <= array[i] && array[i] <= -97) || (-32 <= array[i] && array[i] <= -17)) && ((64 <= array[i + 1] && array[i + 1] <= 126) || (-128 <= array[i + 1] && array[i + 1] <= -4))) {
                    ++n2;
                    n4 += 500L;
                    final int n5 = 256 + array[i];
                    final int n6 = 256 + array[i + 1];
                    int n7;
                    if (n6 < 159) {
                        n7 = 1;
                        if (n6 > 127) {}
                    }
                    else {
                        n7 = 0;
                    }
                    int n8;
                    if (n5 < 160) {
                        n8 = (n5 - 112 << 1) - n7;
                    }
                    else {
                        n8 = (n5 - 176 << 1) - n7;
                    }
                    final int n9 = n8 - 32;
                    if (n9 < this.JPformat.length && 32 < this.JPformat[n9].length && this.JPformat[n9][32] != 0) {
                        n3 += this.JPformat[n9][32];
                    }
                    ++i;
                }
                else if (-95 <= array[i] && array[i] <= -33) {}
            }
        }
        return -1 + (int)(50.0f * (n2 / n) + 50.0f * (n3 / n4));
    }
    
    private int utf16probability(final byte[] array) {
        if (array.length <= 1 || -2 != array[0] || -1 != array[1]) {
            final byte b = array[0];
            int n = 0;
            if (-1 != b) {
                return n;
            }
            final byte b2 = array[1];
            n = 0;
            if (-2 != b2) {
                return n;
            }
        }
        return 100;
    }
    
    private int utf8probability(final byte[] array) {
        int n = 0;
        int n2 = 0;
        final int length = array.length;
        for (int i = 0; i < length; ++i) {
            if ((0x7F & array[i]) == array[i]) {
                ++n2;
            }
            else if (-64 <= array[i] && array[i] <= -33 && i + 1 < length && -128 <= array[i + 1] && array[i + 1] <= -65) {
                n += 2;
                ++i;
            }
            else if (-32 <= array[i] && array[i] <= -17 && i + 2 < length && -128 <= array[i + 1] && array[i + 1] <= -65 && -128 <= array[i + 2] && array[i + 2] <= -65) {
                n += 3;
                i += 2;
            }
        }
        if (n2 != length) {
            final int n3 = (int)(100.0f * (n / (length - n2)));
            if (n3 > 98) {
                return n3;
            }
            if (n3 > 95 && n > 30) {
                return n3;
            }
        }
        return 0;
    }
    
    public int getEncodeValue(final File file) {
        try {
            final byte[] read = this.read(new FileInputStream(file));
            return this.getEncodeValue(read);
        }
        catch (FileNotFoundException ex) {
            final byte[] read = null;
            return this.getEncodeValue(read);
        }
    }
    
    public int getEncodeValue(final InputStream inputStream) {
        final byte[] array = new byte[8192];
        int n = 0;
        final int unknown = ParseEncoding.UNKNOWN;
        try {
            while (true) {
                final int read = inputStream.read(array, n, array.length - n);
                if (read <= 0) {
                    break;
                }
                n += read;
            }
            inputStream.close();
            return this.getEncodeValue(array);
        }
        catch (Exception ex) {
            return -1;
        }
    }
    
    public int getEncodeValue(final URL url) {
        try {
            final InputStream openStream = url.openStream();
            return this.getEncodeValue(openStream);
        }
        catch (IOException ex) {
            final InputStream openStream = null;
            return this.getEncodeValue(openStream);
        }
    }
    
    public int getEncodeValue(final byte[] array) {
        int unknown;
        if (array == null) {
            unknown = -1;
        }
        else {
            int n = 0;
            unknown = ParseEncoding.UNKNOWN;
            final int[] array2 = new int[ParseEncoding.TOTALT];
            array2[ParseEncoding.GB2312] = this.gb2312probability(array);
            array2[ParseEncoding.GBK] = this.gbkprobability(array);
            array2[ParseEncoding.BIG5] = this.big5probability(array);
            array2[ParseEncoding.UTF8] = this.utf8probability(array);
            array2[ParseEncoding.UNICODE] = this.utf16probability(array);
            array2[ParseEncoding.EUC_KR] = this.euc_krprobability(array);
            array2[ParseEncoding.ASCII] = this.asciiprobability(array);
            array2[ParseEncoding.SJIS] = this.sjisprobability(array);
            array2[ParseEncoding.EUC_JP] = this.euc_jpprobability(array);
            array2[ParseEncoding.UNKNOWN] = 0;
            for (int i = 0; i < ParseEncoding.TOTALT; ++i) {
                if (array2[i] > n) {
                    unknown = i;
                    n = array2[i];
                }
            }
            if (n <= 50) {
                return ParseEncoding.UNKNOWN;
            }
        }
        return unknown;
    }
    
    @Override
    public String getEncoding(final InputStream inputStream) {
        return this.check(this.getEncodeValue(inputStream));
    }
    
    @Override
    public String getEncoding(final String s) {
        return this.check(this.getEncodeValue(s));
    }
    
    @Override
    public String getEncoding(final URL url) {
        return this.check(this.getEncodeValue(url));
    }
    
    @Override
    public String getEncoding(final byte[] array) {
        return this.check(this.getEncodeValue(array));
    }
}
