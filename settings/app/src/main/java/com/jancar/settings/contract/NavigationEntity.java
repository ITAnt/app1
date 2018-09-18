package com.jancar.settings.contract;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ouyan on 2018/9/10.
 */

public class NavigationEntity implements Parcelable {
    private String name;
    private int  img;
    public NavigationEntity(){}

    protected NavigationEntity(Parcel in) {
        name = in.readString();
        img = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(img);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<NavigationEntity> CREATOR = new Creator<NavigationEntity>() {
        @Override
        public NavigationEntity createFromParcel(Parcel in) {
            return new NavigationEntity(in);
        }

        @Override
        public NavigationEntity[] newArray(int size) {
            return new NavigationEntity[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }
}
