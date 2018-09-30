package com.jancar.radio.listener.utils;

import java.io.Serializable;

public class FreqRange implements Serializable
{
    private static final long serialVersionUID = 1L;
    private int amEnd;
    private int amStart;
    private int amStep;
    private int fmEnd;
    private int fmStart;
    private int fmStep;
    private int mEnd;
    private int mStart;
    private int mType;
    
    public int getAmEnd() {
        return this.amEnd;
    }
    
    public int getAmStart() {
        return this.amStart;
    }
    
    public int getAmStep() {
        return this.amStep;
    }
    
    public int getFmEnd() {
        return this.fmEnd;
    }
    
    public int getFmStart() {
        return this.fmStart;
    }
    
    public int getFmStep() {
        return this.fmStep;
    }
    
    public int getmEnd() {
        if (this.mType == 0) {
            this.mEnd = this.getAmEnd();
        }
        else {
            this.mEnd = this.getFmEnd();
        }
        return this.mEnd;
    }
    
    public int getmStart() {
        if (this.mType == 0) {
            this.mStart = this.getAmStart();
        }
        else {
            this.mStart = this.getFmStart();
        }
        return this.mStart;
    }
    
    public void setAmEnd(final int amEnd) {
        this.amEnd = amEnd;
    }
    
    public void setAmStart(final int amStart) {
        this.amStart = amStart;
    }
    
    public void setAmStep(final int amStep) {
        this.amStep = amStep;
    }
    
    public void setFmEnd(final int fmEnd) {
        this.fmEnd = fmEnd;
    }
    
    public void setFmStart(final int fmStart) {
        this.fmStart = fmStart;
    }
    
    public void setFmStep(final int fmStep) {
        this.fmStep = fmStep;
    }
    
    public void setmEnd(final int mEnd) {
        this.mEnd = mEnd;
    }
    
    public void setmStart(final int mStart) {
        this.mStart = mStart;
    }
}
