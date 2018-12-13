package com.jancar.settings.util;

import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;

public class nvRa {
    private nvRa (){}
    public void nvRa(){
        IBinder binder=ServiceManager.getService("NvRAMAgent");
        NvRAMAgent agent=NvRAMAgent.Stub.asInterface(binder);
        try {
            byte[] buff=agent.readFile(45);
            int flag=agent.writeFile(45,buff);
            for (byte aBuff : buff) {
                Log.w("NavigationSoftware", aBuff + " ");
            }
            Log.w("NavigationSoftware", buff.toString() + " ");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
