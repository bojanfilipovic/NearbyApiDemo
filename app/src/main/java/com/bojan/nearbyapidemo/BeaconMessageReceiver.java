package com.bojan.nearbyapidemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;

public class BeaconMessageReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        Nearby.Messages.handleIntent(intent, new MessageListener() {
            @Override
            public void onFound(Message message) {

                String receivedMessage = new String(message.getContent());
                Log.i("***", "Found via PendingIntent: "+ message);
                Log.i("***", "Message string: " + receivedMessage);
                Toast.makeText(context, receivedMessage, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLost(Message message) {
                Log.i("***", "Lost via PendingIntent: "+ message);
            }
        });
    }
}
