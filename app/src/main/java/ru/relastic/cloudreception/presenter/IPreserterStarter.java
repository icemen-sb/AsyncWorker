package ru.relastic.cloudreception.presenter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ru.relastic.cloudreception.repository.IDataTransactCallback;
import ru.relastic.cloudreception.repository.TransactData.ResponseData.IncomingCall;

public interface IPreserterStarter {
    String INTENT_BUNDLE_KEY            = "intent_bundle_key";
    int INTENT_RESULT_COMMIT            = 200;
    int INTENT_RESULT_CANCEL            = 100;
    int WHAT_SELECT_PERSON_ALL          = 10;
    int WHAT_SELECT_SCHEDULEITEM_ALL    = 11;
    int WHAT_SELECT_INCOMING_ALL        = 12;
    int WHAT_SELECT_PERSON_NOTIFIED     = 13;
    int WHAT_UPDATE                     = 20;
    int WHAT_UPDATE_ALL_NOTIFIED        = 21;
    int WHAT_INSERT                     = 30;
    int WHAT_DELETE                     = 40;
    int WHAT_DELETE_ALL_INCOMING_CALL   = 41;
    int EVENT_WHAT_UICALLBACK           = 501;
    int EVENT_WHAT_VIEW_REDIRECT        = 502;
    int EVENT_WHAT_OTHER                = 503;

    void startUI(IncomingCall incomingCall);
    void populateData (@NonNull ZAdapter zAdapter, int what);
    void selectData (int what, @NonNull IDataTransactCallback callback, @Nullable Object arg);
    void updateDataItem (@NonNull Object item);
    void updateData (int what, @Nullable Object arg);
    void insertDataItem (@NonNull Object item);
    void deleteDataItem (@NonNull Object item);
    void deleteData (int what, @Nullable Object arg);
    void addListener (IPresenterUICallback iPresenterUICallback);
    void removeListener (IPresenterUICallback iPresenterUICallback);
}
