// SetDateTime.aidl
package com.jancar.settings;

// Declare any non-default types here with import statements

interface SetDateTime {
     int setTimeToSystem(long MilliSec);
    void setDateTime(int year, int month, int day, int hour, int minute) ;
}
