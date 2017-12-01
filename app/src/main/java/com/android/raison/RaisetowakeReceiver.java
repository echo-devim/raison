package com.android.raison;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class RaisetowakeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent rintent = new Intent(context, RaisetowakeService.class);
        context.startService(rintent);
    }
}
