package com.jancar.radio.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
@Entity
public class Collection {
    @Id(autoincrement = true)
    private Long _id;
    private String desp;
    private int id;
    private int isLove;
    private int kind;
    private String isAFfM;
    private int mBand;
    private int mFreq;
    private String name;
    private int position;
    private String rdsname;
    private boolean select;
    private int  frequency;
    private int Location;
    @Generated(hash = 941600138)
    public Collection(Long _id, String desp, int id, int isLove, int kind,
            String isAFfM, int mBand, int mFreq, String name, int position,
            String rdsname, boolean select, int frequency, int Location) {
        this._id = _id;
        this.desp = desp;
        this.id = id;
        this.isLove = isLove;
        this.kind = kind;
        this.isAFfM = isAFfM;
        this.mBand = mBand;
        this.mFreq = mFreq;
        this.name = name;
        this.position = position;
        this.rdsname = rdsname;
        this.select = select;
        this.frequency = frequency;
        this.Location = Location;
    }
    @Generated(hash = 1149123052)
    public Collection() {
    }
    public Long get_id() {
        return this._id;
    }
    public void set_id(Long _id) {
        this._id = _id;
    }
    public String getDesp() {
        return this.desp;
    }
    public void setDesp(String desp) {
        this.desp = desp;
    }
    public int getId() {
        return this.id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getIsLove() {
        return this.isLove;
    }
    public void setIsLove(int isLove) {
        this.isLove = isLove;
    }
    public int getKind() {
        return this.kind;
    }
    public void setKind(int kind) {
        this.kind = kind;
    }
    public String getIsAFfM() {
        return this.isAFfM;
    }
    public void setIsAFfM(String isAFfM) {
        this.isAFfM = isAFfM;
    }
    public int getMBand() {
        return this.mBand;
    }
    public void setMBand(int mBand) {
        this.mBand = mBand;
    }
    public int getMFreq() {
        return this.mFreq;
    }
    public void setMFreq(int mFreq) {
        this.mFreq = mFreq;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getPosition() {
        return this.position;
    }
    public void setPosition(int position) {
        this.position = position;
    }
    public String getRdsname() {
        return this.rdsname;
    }
    public void setRdsname(String rdsname) {
        this.rdsname = rdsname;
    }
    public boolean getSelect() {
        return this.select;
    }
    public void setSelect(boolean select) {
        this.select = select;
    }
    public int getFrequency() {
        return this.frequency;
    }
    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }
    public int getLocation() {
        return this.Location;
    }
    public void setLocation(int Location) {
        this.Location = Location;
    }
}
