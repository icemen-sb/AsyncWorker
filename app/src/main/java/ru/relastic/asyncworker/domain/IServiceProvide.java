package ru.relastic.asyncworker.domain;

import android.support.annotation.NonNull;

import ru.relastic.asyncworker.repository.AuthData;
import ru.relastic.asyncworker.repository.IDataTransactCallback;
import ru.relastic.asyncworker.repository.IPreferencesTransactCallback;
import ru.relastic.asyncworker.repository.TransactData.ResponseData.IncomingCall;

public interface IServiceProvide {
    //<...> ANY CONSTANTS

    public void requestData(@NonNull AuthData data,
                            @NonNull IDataTransactCallback callback,
                            boolean toLocal);
    public void requestPropertiesGet(@NonNull IPreferencesTransactCallback callback,
                                     String namePreference);
    public void requestPropertiesSet(IPreferencesTransactCallback callback,
                                     @NonNull String namePreference,
                                     Object value);
    public AuthData getAuthData();

    public void syncData();

    public void showUI(IncomingCall incomingCall);

    public void addListener(@NonNull IServiceConnectCallback callback);

    public void removeListener(@NonNull IServiceConnectCallback callback);
}
