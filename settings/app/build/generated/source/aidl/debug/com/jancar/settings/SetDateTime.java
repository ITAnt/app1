/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: G:\\and\\pro\\AC8227L-AP1\\settings\\app\\src\\main\\aidl\\com\\jancar\\settings\\SetDateTime.aidl
 */
package com.jancar.settings;
// Declare any non-default types here with import statements

public interface SetDateTime extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.jancar.settings.SetDateTime
{
private static final java.lang.String DESCRIPTOR = "com.jancar.settings.SetDateTime";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.jancar.settings.SetDateTime interface,
 * generating a proxy if needed.
 */
public static com.jancar.settings.SetDateTime asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.jancar.settings.SetDateTime))) {
return ((com.jancar.settings.SetDateTime)iin);
}
return new com.jancar.settings.SetDateTime.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_setTimeToSystem:
{
data.enforceInterface(DESCRIPTOR);
long _arg0;
_arg0 = data.readLong();
int _result = this.setTimeToSystem(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_setDateTime:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
int _arg2;
_arg2 = data.readInt();
int _arg3;
_arg3 = data.readInt();
int _arg4;
_arg4 = data.readInt();
this.setDateTime(_arg0, _arg1, _arg2, _arg3, _arg4);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.jancar.settings.SetDateTime
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public int setTimeToSystem(long MilliSec) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeLong(MilliSec);
mRemote.transact(Stub.TRANSACTION_setTimeToSystem, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void setDateTime(int year, int month, int day, int hour, int minute) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(year);
_data.writeInt(month);
_data.writeInt(day);
_data.writeInt(hour);
_data.writeInt(minute);
mRemote.transact(Stub.TRANSACTION_setDateTime, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_setTimeToSystem = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_setDateTime = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
}
public int setTimeToSystem(long MilliSec) throws android.os.RemoteException;
public void setDateTime(int year, int month, int day, int hour, int minute) throws android.os.RemoteException;
}
