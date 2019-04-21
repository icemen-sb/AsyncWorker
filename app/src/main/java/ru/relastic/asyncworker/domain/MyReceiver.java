package ru.relastic.asyncworker.domain;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import javax.inject.Inject;

import ru.relastic.asyncworker.dagger2.App;


public class MyReceiver extends BroadcastReceiver {
    public static final int NOTIFY_ID = 101;
    public static final String INTENT_WHAT = "ASYNKWORKER";
    public static final String INTENT_WHAT_VALIE = "asyncworker_lounch";
    public static final String INTENT_CLIENT_BUNDLE = "ASYNKWORKER_CLIENT_BUNDLE";
    public static final int INTENT_WHAT_INCOMING_CALL = 1;
    public static final int INTENT_WHAT_RETURN_PANDING = 2;


    @Inject
    public MyHandleReceiver myHandleReceiver;

    @Override
    public void onReceive(final Context context, final Intent intent) {
        App.getComponent().inject(this);
        if ((intent.getAction()!=null) && intent.getAction().equals("android.intent.action.PHONE_STATE")) {
            myHandleReceiver.enqueu(INTENT_WHAT_INCOMING_CALL, intent.getExtras());
        } else if (intent.hasExtra(INTENT_WHAT) && intent.getStringExtra(INTENT_WHAT).equals(INTENT_WHAT_VALIE)) {
            //запускаем UI
            myHandleReceiver.enqueu(INTENT_WHAT_RETURN_PANDING, intent.getExtras());
        } else {

        }
    }
    public static Intent getIntent(Context context) {
        return new Intent(context, MyReceiver.class);
    }
}