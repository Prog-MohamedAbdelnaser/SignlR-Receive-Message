package com.tabeesgl.background.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.tabeesgl.EmployeeLocationEntity;
import com.tabeesgl.utl.Events;

import java.util.concurrent.ExecutionException;

import microsoft.aspnet.signalr.client.MessageReceivedHandler;
import microsoft.aspnet.signalr.client.Platform;
import microsoft.aspnet.signalr.client.SignalRFuture;
import microsoft.aspnet.signalr.client.http.android.AndroidPlatformComponent;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler1;
import microsoft.aspnet.signalr.client.transport.ClientTransport;
import microsoft.aspnet.signalr.client.transport.ServerSentEventsTransport;

import static com.tabeesgl.utl.Events.Event_NewMessage;
import static com.tabeesgl.utl.Events.serverUrl;


public class SignalRService extends Service {
    private HubConnection mHubConnection;
    private HubProxy mHubProxy;
    private Handler mHandler; // to display Toast message
    private final IBinder mBinder = new LocalBinder(); // Binder given to clients
    private Context context ;
    public SignalRService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context=this;
        mHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        context=this;
        int result = super.onStartCommand(intent, flags, startId);
        System.out.println("start command ... ");
        startSignalR();
        return START_STICKY_COMPATIBILITY;
    }

    @Override
    public void onDestroy() {
        System.out.println("stop connection signl ");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        System.out.println("signal bind");
        startSignalR();
        return mBinder;
    }

    public class LocalBinder extends Binder {
        public SignalRService getService() {
            return SignalRService.this;
        }
    }

    public void updateLocation(double lat, double lng) {
        EmployeeLocationEntity employeeLocationEntity =new EmployeeLocationEntity("02-01-2019 00:00:00",
               lat,lng,"12");

        System.out.println("test signal send ..."+mHubConnection.getConnectionToken());

        mHubProxy.invoke(Events.Event_UpdateLocation, employeeLocationEntity).onError(throwable -> {
            throwable.printStackTrace();
            System.out.println("signal error send ");
        });



    }

    private void startSignalR() {
        Platform.loadPlatformComponent(new AndroidPlatformComponent());

        mHubConnection = new HubConnection(serverUrl);
        mHubProxy = mHubConnection.createHubProxy(Events.Hub_Location);

        ClientTransport clientTransport = new ServerSentEventsTransport(mHubConnection.getLogger());

        SignalRFuture<Void> signalRFuture = mHubConnection.start(clientTransport);

        mHubConnection.connected(() -> System.out.println("signal 1 is connected "+mHubConnection.getConnectionToken()));
        try {
            System.out.println("signal git ....");
            signalRFuture.get();
            signalRFuture.onCancelled(() -> System.out.println("signal cancled"));
            signalRFuture.onError(throwable -> {
                throwable.printStackTrace();
                System.out.println("singal error");
            });
        } catch (InterruptedException | ExecutionException e) {
            Log.e("SimpleSignalR", e.toString());
            e.printStackTrace();
            return;
        }




        updateLocation(30.0,29.0);


        mHubConnection.received(new MessageReceivedHandler() {
            @Override
            public void onMessageReceived(JsonElement jsonElement) {
                //todo bind message to ui
                try{
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {

                        Toast.makeText(context,jsonElement.toString(),Toast.LENGTH_LONG).show();
                    }
                });}catch (Exception ex){
                    ex.printStackTrace();
                }
                System.out.println("test we have new sms received : "+jsonElement.toString());
            }
        });

        starHandlersConnection();
       /* mHubProxy.on(Event_NewMessage,
                msg -> {
                    System.out.println("test we have sms "+msg);
                    mHandler.post(() -> {
                        new Notification().setNotification(msg);
                    });
                }
                ,String.class);*/

    }

    private SubscriptionHandler1 handlerCon;

    private void starHandlersConnection(){
        handlerCon = (SubscriptionHandler1<String>) p1 -> {
            System.out.println("test we have new sms received on sub "+p1);
            //Here is where we get back the response from the server. Do stuffs
        };

        mHubProxy.on(Event_NewMessage,handlerCon,String.class);
    }



}
