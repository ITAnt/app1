package com.jancar.settings.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SendResetBroadcast extends BroadcastReceiver {
        private boolean mEraseSdCard;
        @Override
        public void onReceive(Context context, Intent intent) {
                // TODO Auto-generated method stub
                String action = intent.getAction();
                if(action.equals("com.qucii.sendreset")){
                intent = new Intent("android.intent.action.MASTER_CLEAR");
                intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
                intent.putExtra("android.intent.extra.REASON", "MasterClearConfirm");
                intent.putExtra("android.intent.extra.WIPE_EXTERNAL_STORAGE", mEraseSdCard);
                context.sendBroadcast(intent);
                }
        }

}