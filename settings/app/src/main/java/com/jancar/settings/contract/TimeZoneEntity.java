package com.jancar.settings.contract;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ouyan on 2018/9/5.
 */

public class TimeZoneEntity  implements Parcelable {
    private String id;
    private String name;
    private String time;
    public  TimeZoneEntity(){};
    protected TimeZoneEntity(Parcel in) {
        id = in.readString();
        name = in.readString();
        time = in.readString();
    }

    public static final Creator<TimeZoneEntity> CREATOR = new Creator<TimeZoneEntity>() {
        @Override
        public TimeZoneEntity createFromParcel(Parcel in) {
            return new TimeZoneEntity(in);
        }

        @Override
        public TimeZoneEntity[] newArray(int size) {
            return new TimeZoneEntity[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(time);
    }
}
