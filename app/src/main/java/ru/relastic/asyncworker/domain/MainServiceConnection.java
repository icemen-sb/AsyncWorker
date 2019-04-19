package ru.relastic.asyncworker.domain;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Messenger;

public class MainServiceConnection implements ServiceConnection{
    private Messenger messenger;
    private IServiceConnectCallback callback;

    public MainServiceConnection(IServiceConnectCallback callback) {
        this.callback = callback;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        System.out.println("****** mBinder == null: "+ (service == null));
        //messenger = new Messenger(service);
        callback.onStateChange(true, ((MainService.LocalBinder) service).getService(), this);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        callback.onStateChange(false, null, null);
        messenger = null;
        callback = null;
    }

    public Messenger getMessenger () {
        return messenger;
    }

    public IBinder getBinder () {
        return messenger.getBinder();
    }
}