package ru.relastic.cloudreception.domain;

import android.support.annotation.NonNull;

import ru.relastic.cloudreception.repository.AuthData;
import ru.relastic.cloudreception.repository.IDataTransactCallback;
import ru.relastic.cloudreception.repository.IPreferencesTransactCallback;
import ru.relastic.cloudreception.repository.TransactData.ResponseData.IncomingCall;

public interface IServiceProvide {
    void requestData(@NonNull AuthData data,
                            IDataTransactCallback callback,
                            boolean toLocal);
    void requestPropertiesGet(@NonNull IPreferencesTransactCallback callback,
                                     String namePreference);
    void requestPropertiesSet(IPreferencesTransactCallback callback,
                                     @NonNull String namePreference,
                                     Object value);
    AuthData getAuthData();

    void syncData();

    void showUI(IncomingCall incomingCall);

    void addListener(@NonNull IServiceConnectCallback callback);

    void removeListener(@NonNull IServiceConnectCallback callback);
}
