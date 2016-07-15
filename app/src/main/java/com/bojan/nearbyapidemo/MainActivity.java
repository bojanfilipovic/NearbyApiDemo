package com.bojan.nearbyapidemo;

import android.app.PendingIntent;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.android.gms.nearby.messages.Strategy;
import com.google.android.gms.nearby.messages.SubscribeOptions;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient googleApiClient;
    private MessageListener messageListener;
    private Message activeMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //setting up the Nearby API
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Nearby.MESSAGES_API)
                .addConnectionCallbacks(this)
                .enableAutoManage(this, this)
                .build();

        messageListener = new MessageListener(){
            @Override
            public void onFound(Message message) {
                String messageAsString = new String(message.getContent());
                Log.i("***", "Found message: " + messageAsString);
            }

            @Override
            public void onLost(Message message) {
                String messageAsString = new String(message.getContent());
                Log.i("***", "Lost sight of message: " + messageAsString);
            }
        };

        Button sub = (Button) findViewById(R.id.subscribe);
        Button unsub = (Button) findViewById(R.id.unsubscribe);

        sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("***", "calling subscribe method...");
                //subscribe();          //if you use foreground subscribe, don't forget to uncomment
                                        //foreground unsubscribe in unsubscribe()
                backgroundSubscribe();

            }
        });

        unsub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("***", "calling unsubscribe method...");
                unsubscribe();
            }
        });

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i("***", "Google client connected!");
        publish("Hello World! I am a Nearby Message!");
    }

    @Override
    protected void onStop() {
        unpublish();
        unsubscribe();

        super.onStop();
    }

    private void subscribe(){
        Log.i("***", "Subscribing...");
        SubscribeOptions options = new SubscribeOptions.Builder()
                .setStrategy(Strategy.BLE_ONLY)                                     //use only BLE to find devices
                .build();
        Nearby.Messages.subscribe(googleApiClient, messageListener, options);       //subscribing in the Foreground
    }

    private void unsubscribe(){
        Log.i("***", "Unsubscribing...");

        //foreground unsubscribe
        //Nearby.Messages.unsubscribe(googleApiClient, messageListener);

        //background unsubscribe
        Nearby.Messages.unsubscribe(googleApiClient, getPendingIntent());
    }

    private void publish(String message){
        Log.i("***", "Publishing message: " + message);
        activeMessage = new Message(message.getBytes());
        Nearby.Messages.publish(googleApiClient, activeMessage);
    }

    private void unpublish(){
        Log.i("***", "Unpublishing...");
        if (activeMessage != null){
            Nearby.Messages.unpublish(googleApiClient, activeMessage);
            activeMessage = null;
        }
    }

    //subscribing in the background
    private void backgroundSubscribe(){
        Log.i("***", "Background subscribe");
        SubscribeOptions options = new SubscribeOptions.Builder()
                .setStrategy(Strategy.BLE_ONLY)
                .build();
        Nearby.Messages.subscribe(googleApiClient, getPendingIntent(), options);
    }

    private PendingIntent getPendingIntent(){
        return PendingIntent.getBroadcast(this, 0, new Intent(this, BeaconMessageReceiver.class),
                PendingIntent.FLAG_UPDATE_CURRENT);
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }


}
