package ru.relastic.asyncworker.repository;

import android.support.annotation.Nullable;

public interface DataTransact {

    public void requestData(AuthData data_request, @Nullable IDataTransactCallback callback);

}
