package com.jancar.radio.contract;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.jancar.radio.R;
import com.jancar.radio.RadioWrapper;
import com.jancar.radio.listener.utils.FreqRange;


public class RadioCacheUtil {
    private static final String AUTO_RENAME = "autoRename";
    private static final String FIRST_RUN = "firstRun";
    private static final String LAST_AM_FREQ = "lastAm";
    private static final String LAST_AM_RDS_NAME = "lastAMRdsName";
    private static final String LAST_FAVORITES_BAND = "lastFavBand";
    private static final String LAST_FAVORITES_FREQ = "lastFavFreq";
    private static final String LAST_FM_FREQ = "lastfm";
    private static final String LAST_FM_RDS_NAME = "lastFMRdsName";
    private static final String LAST_PAGE = "page";
    private static final String PREFS_NAME = "mRadioPrefs";
    private static final String REGION = "region";
    private static RadioCacheUtil pThis;
    public static String[] ptyArr;
    public static String[] regionArr;
    private int mAF;
    private int mAudoRe;
    private Context mContext;
    private FreqRange mFreqRange;
    private int mLocation;
    private int mPty;
    private SharedPreferences mSharedPreferences;
    private int mTA;
    
    static {
        RadioCacheUtil.ptyArr = null;
        RadioCacheUtil.regionArr = null;
        RadioCacheUtil.pThis = null;
    }
    
    private RadioCacheUtil(@NonNull final Context mContext) {
        this.mContext = null;
        this.mContext = mContext;
        this.initLocation();
    }
    
    public static RadioCacheUtil getInstance(Context mContext) {
        if (RadioCacheUtil.pThis == null) {
            RadioCacheUtil.pThis = new RadioCacheUtil(mContext);
        }
        return RadioCacheUtil.pThis;
    }
    
    public void clearLastFavorites() {
        if (this.mSharedPreferences == null) {
            return;
        }
        this.mSharedPreferences.edit().remove(LAST_FAVORITES_BAND).apply();
        this.mSharedPreferences.edit().remove(LAST_FAVORITES_FREQ).apply();
    }
    
    public int getAF() {
        if (this.mSharedPreferences == null) {
            return 0;
        }
        return this.mSharedPreferences.getInt("af", 0);
    }
    
    public int getAutoRename() {
        if (this.mSharedPreferences == null) {
            return 0;
        }
        return this.mSharedPreferences.getInt(AUTO_RENAME, 0);
    }
    
    public int getBand() {
        if (this.mSharedPreferences == null) {
            return 0;
        }
        return this.mSharedPreferences.getInt("band", 1);
    }
    
    public boolean getFirstRun() {
        return this.mSharedPreferences != null && this.mSharedPreferences.getBoolean(FIRST_RUN, true);
    }
    
    public FreqRange getFreqRange() {
        return this.mFreqRange;
    }
    
    public int getLastAMFreq() {
        if (this.mSharedPreferences == null) {
            return 0;
        }
        return this.mSharedPreferences.getInt(LAST_AM_FREQ, this.mFreqRange.getAmStart());
    }
    
    public String getLastAMRdsName() {
        if (this.mSharedPreferences == null) {
            return "";
        }
        return this.mSharedPreferences.getString(LAST_AM_RDS_NAME, "");
    }
    
    public int getLastFMFreq() {
        if (this.mSharedPreferences == null) {
            return 0;
        }
        return this.mSharedPreferences.getInt(LAST_FM_FREQ, this.mFreqRange.getFmStart());
    }
    
    public String getLastFMRdsName() {
        if (this.mSharedPreferences == null) {
            return "";
        }
        return this.mSharedPreferences.getString(LAST_FM_RDS_NAME, "");
    }
    
    public int getLastFavoritesBand() {
        if (this.mSharedPreferences == null) {
            return 0;
        }
        return this.mSharedPreferences.getInt(LAST_FAVORITES_BAND, 1);
    }
    
    public int getLastFavoritesFreq() {
        if (this.mSharedPreferences == null) {
            return 0;
        }
        return this.mSharedPreferences.getInt(LAST_FAVORITES_FREQ, this.mFreqRange.getFmStart());
    }
    
    public int getLastFreq(final int n) {
        if (n == 1) {
            return this.getLastFMFreq();
        }
        return this.getLastAMFreq();
    }
    
    public int getLastPage(final int n) {
        if (this.mSharedPreferences == null) {
            return 0;
        }
        return this.mSharedPreferences.getInt(LAST_PAGE, n);
    }
    
    public String getLastRdsName(final int n) {
        if (n == 1) {
            return this.getLastFMRdsName();
        }
        return this.getLastAMRdsName();
    }
    
    public int getLocation() {
        if (this.mSharedPreferences == null) {
            return 0;
        }
        return this.mSharedPreferences.getInt("location", /*IVIConfig.getRadioDefaultLocation()*/0);
    }
    
    public int getPty() {
        if (this.mSharedPreferences == null) {
            return 0;
        }
        return this.mSharedPreferences.getInt("pty", 0);
    }
    
    public String getRadioPrestore(final int n) {
        if (n == 1) {
            return this.getRadioPrestoreFM();
        }
        return this.getRadioPrestoreAM();
    }
    
    public String getRadioPrestoreAM() {
        return null;
    }
    
    public String getRadioPrestoreFM() {
        return null;
    }
    
    public int getRegion() {
        if (this.mSharedPreferences == null) {
            return 0;
        }
        return this.mSharedPreferences.getInt(REGION, 0);
    }
    
    public int getTA() {
        if (this.mSharedPreferences == null) {
            return 0;
        }
        return this.mSharedPreferences.getInt("ta", 0);
    }
    
    public void initLocation() {
        if (this.mSharedPreferences == null) {
            this.mSharedPreferences = this.mContext.getSharedPreferences(PREFS_NAME, 0);
        }
        this.mAF = this.getAF();
        this.mTA = this.getTA();
        this.mPty = this.getPty();
        this.mLocation = this.getLocation();
        this.mAudoRe = this.getAutoRename();
        if (RadioCacheUtil.ptyArr == null) {
            RadioCacheUtil.ptyArr = this.mContext.getResources().getStringArray(R.array.ptyArray);
        }
        if (RadioCacheUtil.regionArr == null) {
            RadioCacheUtil.regionArr = this.mContext.getResources().getStringArray(R.array.regionArray);
        }
        if (this.mFreqRange == null) {
            this.mFreqRange = new FreqRange();
        }
        this.mFreqRange.setAmEnd(RadioWrapper.getFreqEnd(0, this.mLocation));
        this.mFreqRange.setAmStart(RadioWrapper.getFreqStart(0, this.mLocation));
        this.mFreqRange.setAmStep(RadioWrapper.getFreqStep(0, this.mLocation));
        this.mFreqRange.setFmEnd(RadioWrapper.getFreqEnd(1, this.mLocation));
        this.mFreqRange.setFmStart(RadioWrapper.getFreqStart(1, this.mLocation));
        this.mFreqRange.setFmStep(RadioWrapper.getFreqStep(1, this.mLocation));
    }
    
    public void setAF(final int n) {
        if (this.mSharedPreferences == null) {
            return;
        }
        this.mSharedPreferences.edit().putInt("af", n).apply();
    }
    
    public void setAutoRename(final int n) {
        if (this.mSharedPreferences == null) {
            return;
        }
        this.mSharedPreferences.edit().putInt(AUTO_RENAME, this.mAudoRe).apply();
    }
    
    public void setBand(final int n) {
        if (this.mSharedPreferences == null) {
            return;
        }
        this.mSharedPreferences.edit().putInt("band", n).apply();
    }
    
    public void setFirstRun(final boolean b) {
        if (this.mSharedPreferences == null) {
            return;
        }
        this.mSharedPreferences.edit().putBoolean(FIRST_RUN, b).apply();
    }
    
    public void setLastAMFreq(final int n) {
        if (this.mSharedPreferences == null) {
            return;
        }
        this.mSharedPreferences.edit().putInt(LAST_AM_FREQ, n).apply();
    }
    
    public void setLastAMRdsName(final String s) {
        if (this.mSharedPreferences != null && !TextUtils.isEmpty((CharSequence)s)) {
            this.mSharedPreferences.edit().putString(LAST_AM_RDS_NAME, s).apply();
        }
    }
    
    public void setLastFMRdsName(final String s) {
        if (this.mSharedPreferences != null && !TextUtils.isEmpty((CharSequence)s)) {
            this.mSharedPreferences.edit().putString(LAST_FM_RDS_NAME, s).apply();
        }
    }
    
    public void setLastFavoritesBand(final int n) {
        if (this.mSharedPreferences == null) {
            return;
        }
        this.mSharedPreferences.edit().putInt(LAST_FAVORITES_BAND, n).apply();
    }
    
    public void setLastFavoritesFreq(final int n) {
        if (this.mSharedPreferences == null) {
            return;
        }
        this.mSharedPreferences.edit().putInt(LAST_FAVORITES_FREQ, n).apply();
    }
    
    public void setLastFmFreq(final int n) {
        if (this.mSharedPreferences == null) {
            return;
        }
        this.mSharedPreferences.edit().putInt(LAST_FM_FREQ, n).apply();
    }
    
    public void setLastFreq(final int n, final int n2) {
        if (n == 1) {
            this.setLastFmFreq(n2);
            return;
        }
        this.setLastAMFreq(n2);
    }
    
    public void setLastPage(final int n) {
        if (this.mSharedPreferences == null) {
            return;
        }
        this.mSharedPreferences.edit().putInt("page", n).apply();
    }
    
    public void setLastRdsName(final int n, final String s) {
        if (1 == n) {
            this.setLastFMRdsName(s);
            return;
        }
        this.setLastAMRdsName(s);
    }
    
    public void setLocation(final int n) {
        if (this.mSharedPreferences == null) {
            return;
        }
        this.mSharedPreferences.edit().putInt("location", n).apply();
    }
    
    public void setPty(final int n) {
        if (this.mSharedPreferences == null) {
            return;
        }
        this.mSharedPreferences.edit().putInt("pty", n).apply();
    }
    
    public void setRegion(final int n) {
        if (this.mSharedPreferences == null) {
            return;
        }
        this.mSharedPreferences.edit().putInt(REGION, n).apply();
    }
    
    public void setTA(final int n) {
        if (this.mSharedPreferences == null) {
            return;
        }
        this.mSharedPreferences.edit().putInt("ta", n).apply();
    }
}
