package ru.relastic.cloudreception.domain;

public interface IServiceConnectCallback {
    int WHAT_SERVICE_EVENT_UPDATED_DATA = 1;
    void onStateChange(boolean isConnected, IServiceProvide service, MainServiceConnection connection);
    void onChangeTask(int what, Object value);
}
