package ru.relastic.asyncworker.domain;

import android.app.Service;
import android.content.ServiceConnection;

public interface IServiceConnectCallback {
    public void onStateChange(boolean isConnected, IServiceProvide service, MainServiceConnection connection);
    public void onChangeTask(int What, Object value);
}
