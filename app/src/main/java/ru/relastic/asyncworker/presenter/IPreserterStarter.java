package ru.relastic.asyncworker.presenter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ru.relastic.asyncworker.repository.TransactData.ResponseData.IncomingCall;

public interface IPreserterStarter {
    public static final String INTENT_BUNDLE_KEY            = "intent_bundle_key";
    public static final int INTENT_RESULT_COMMIT            = 200;
    public static final int INTENT_RESULT_CANCEL            = 100;
    public static final int WHAT_POUPULATE_PERSONE          = 1;
    public static final int WHAT_POUPULATE_NOTIFIES         = 2;
    public static final int WHAT_POUPULATE_NEW              = 3;
    public static final int WHAT_UPDATE_CLIENT              = 11;
    public static final int WHAT_UPDATE_SCHEDULE_ITEM       = 12;
    public static final int WHAT_UPDATE_NEWCALLS            = 13;
    public static final int WHAT_INSERT_CLIENT              = 21;
    public static final int WHAT_INSERT_SCHEDULE_ITEM       = 22;
    public static final int WHAT_INSERT_NEWCALLS_ITEM       = 23;
    public static final int EVENT_WHAT_UICALLBACK           = 30;
    public static final int EVENT_WHAT_VIEW_REDIRECT        = 31;

    public void startUI(IncomingCall incomingCall);
    public void populateData (@NonNull ZAdapter zAdapter, int what, Object arg);
    public void updateDataItem (@Nullable ZAdapter zAdapter, int what, Object arg);
    public void insertDataItem (@Nullable ZAdapter zAdapter, int what, Object arg);
}
