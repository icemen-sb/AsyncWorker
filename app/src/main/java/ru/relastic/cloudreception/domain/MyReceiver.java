package ru.relastic.cloudreception.domain;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import javax.inject.Inject;
import ru.relastic.cloudreception.dagger2.App;


public class MyReceiver extends BroadcastReceiver {
    public static final String INTENT_WHAT = "CLOUD_RECEPTION";
    public static final String INTENT_WHAT_VALUE = "cloud_reception_launch";
    public static final String INTENT_INCOMING_CALL_BUNDLE = "CLOUD_RECEPTION_CLIENT_BUNDLE";
    public static final int INTENT_WHAT_INCOMING_CALL = 1;      //Смена статуса звонка
    public static final int INTENT_WHAT_RETURN_PENDING = 2;     //Реакция с уведомления
    public static final int INTENT_WHAT_HIDE_NOTIFY = 3;        //Интент с IncomingCall прочтенного


    @Inject
    public MyHandleReceiver myHandleReceiver;

    @Override
    public void onReceive(final Context context, final Intent intent) {
        App.getComponent().inject(this);

        final PendingResult result = goAsync();
        Thread thread = new Thread() {
            @Override
            public void run() {
                if ((intent.getAction()!=null) && intent.getAction().equals("android.intent.action.PHONE_STATE")) {
                    //Интент со звонилки. Параметр - стандартный Bundle звонилки
                    myHandleReceiver.enqueue(INTENT_WHAT_INCOMING_CALL, intent.getExtras());
                } else if (intent.hasExtra(INTENT_WHAT) && intent.getStringExtra(INTENT_WHAT).equals(INTENT_WHAT_VALUE)) {
                    //Сработан PendingIntent.  Параметр - объект, содержащий IncomingCall
                    myHandleReceiver.enqueue(INTENT_WHAT_RETURN_PENDING, intent.getExtras());
                } else if (intent.hasExtra(INTENT_INCOMING_CALL_BUNDLE)) {
                    //отсутствует INTENT_WHAT. Признаком вызова из приложения является наличие ключа
                    // INTENT_INCOMING_CALL_BUNDLE.  Параметр - объект, содержащий IncomingCall
                    myHandleReceiver.enqueue(INTENT_WHAT_HIDE_NOTIFY, intent.getExtras());
                }
                //int resultCode = 1001; - только для order Broadcast
                //result.setResultCode(resultCode);
                result.finish();
            }
        };
        thread.run();
    }

    public static Intent getIntent(Context context) {
        return new Intent(context, MyReceiver.class);
    }
}