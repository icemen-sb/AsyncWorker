package ru.relastic.asyncworker.domain;

import android.app.Service;
import android.content.ServiceConnection;

public interface IServiceConnectCallback {
    public static final int WHAT_SERVICE_EVENT_UDDATED_DATA = 1;
    public void onStateChange(boolean isConnected, IServiceProvide service, MainServiceConnection connection);
    public void onChangeTask(int what, Object value);
}
