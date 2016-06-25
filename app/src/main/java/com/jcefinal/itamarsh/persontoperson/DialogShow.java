package com.jcefinal.itamarsh.persontoperson;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * handles the dialogs for Wifi, Gps, and Bluetooth
 */
public class DialogShow extends BroadcastReceiver {
    private MainScreenActivity main = null;
    void setMainActivityHandler(MainScreenActivity main){
        this.main=main;
    }
    public DialogShow() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int type = intent.getIntExtra("type", -1);
        if (type > -1) {
            this.main.buildAlertMessageNoGps(type);
        }
    }
}
