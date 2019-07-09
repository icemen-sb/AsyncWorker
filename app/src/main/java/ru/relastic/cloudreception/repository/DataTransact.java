package ru.relastic.cloudreception.repository;

import android.support.annotation.Nullable;

public interface DataTransact {
    int DATA_TRANSACT_RESULT_OK    = 0;
    int DATA_TRANSACT_RESULT_ERROR = 1;
    void requestData(AuthData data_request, @Nullable IDataTransactCallback callback);

}
