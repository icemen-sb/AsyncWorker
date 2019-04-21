package ru.relastic.asyncworker.domain;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import javax.inject.Inject;

import ru.relastic.asyncworker.R;
import ru.relastic.asyncworker.dagger2.App;
import ru.relastic.asyncworker.repository.AuthData;
import ru.relastic.asyncworker.repository.IDataTransactCallback;
import ru.relastic.asyncworker.repository.TransactData;
import ru.relastic.asyncworker.repository.TransactData.*;
import ru.relastic.asyncworker.repository.TransactData.ResponseData.*;
public class MyHandleReceiver implements Runnable {

    private int mWhat;
    private Bundle mIntendData;

    private static WindowManager windowManager;
    private static ViewGroup windowLayout;

    @Inject
    public Context mContext;
    @Inject
    public Intent mainServiceIntent;

    public MyHandleReceiver() {}
    private MyHandleReceiver(int what, Bundle intentData) {
        App.getComponent().inject(this);
        mWhat = what;
        mIntendData = intentData;
        Thread thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        switch (mWhat) {
            case MyReceiver.INTENT_WHAT_INCOMING_CALL :
                handleCall(mIntendData);
                break;
            case MyReceiver.INTENT_WHAT_RETURN_PANDING:
                handleIntent(mIntendData);
                break;
        }
    }
    public void enqueu(int what, Bundle intentData) {
        MyHandleReceiver myHandleReceiver= new MyHandleReceiver(what, intentData);
    }
    private void handleCall(Bundle intentData) {
        //TelephonyManager.EXTRA_STATE_RINGING
        //TelephonyManager.EXTRA_STATE_OFFHOOK
        //TelephonyManager.EXTRA_STATE_IDLE

        String phoneState = intentData.getString(TelephonyManager.EXTRA_STATE);
        if (phoneState.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
            final String phoneNumber = MyHandleReceiver.parseDigit(
                    intentData.getString(TelephonyManager.EXTRA_INCOMING_NUMBER));
            if (phoneNumber.length()>=10) {
                final MainServiceConnection connection =
                        new MainServiceConnection(new IServiceConnectCallback() {
                            @Override
                            public void onStateChange(boolean isConnected,
                                                      final IServiceProvide service,
                                                      final MainServiceConnection connection) {
                                if (isConnected) {
                                    final AuthData auth = service.getAuthData()
                                            .next()
                                            .setType(AuthData.AUTH_TYPE_GET_BY_PHONE)
                                            .setExtras(phoneNumber);
                                    service.requestData(auth, new IDataTransactCallback() {
                                                @Override
                                                public void onResponseData(TransactData response) {
                                                    TransactData.ResponseData.IncomingCall incomingCall;// = null;// = new TransactData.ResponseData.IncomingCall();
                                                    int type;
                                                    if((response.getResponse_msg().getCode()==0) && (response.getResponse_data().getClients().size()>0)) {
                                                        Client client = response.getResponse_data().getClients().get(0);
                                                        System.out.println("********* Клиент НАЙДЕН: "+client.getLastname());
                                                        incomingCall = new IncomingCall(client);
                                                        //отображаем уведомление и окно
                                                        type = AuthData.AUTH_TYPE_TR_NOTIFY;
                                                        showNotify(incomingCall);
                                                        showWindow(incomingCall);
                                                    } else {
                                                        type = AuthData.AUTH_TYPE_TR_UNKNOWN;
                                                        System.out.println("********* Клинт не найден: "+phoneNumber);
                                                        incomingCall = new IncomingCall(phoneNumber);
                                                    }
                                                    //добавляем в локальную базу
                                                    service.requestData(auth.next().setType(type).setBody(
                                                            new ResponseData(incomingCall)),
                                                            new IDataTransactCallback() {
                                                                @Override
                                                                public void onResponseData(TransactData response) {
                                                                    //--- ОК?
                                                                    mContext.unbindService(connection);
                                                                }
                                                            },
                                                            true);
                                                }

                                            },
                                            true);
                                }
                            }
                            @Override
                            public void onChangeTask(int What, Object value) {}
                        });
                mContext.bindService(mainServiceIntent,connection, Context.BIND_AUTO_CREATE);
            }
        } else {
            closeWindow();
        }
    }
    private void handleIntent(final Bundle intentData) {
        final IncomingCall incomingCall = new IncomingCall(intentData.getBundle(MyReceiver.INTENT_CLIENT_BUNDLE));
        mContext.bindService(mainServiceIntent,
                new MainServiceConnection(new IServiceConnectCallback() {
                    @Override
                    public void onStateChange(boolean isConnected,
                                              IServiceProvide service,
                                              MainServiceConnection connection) {
                        if (isConnected) {
                            service.showUI(incomingCall);
                        }
                        mContext.unbindService(connection);
                    }
                    @Override
                    public void onChangeTask(int What, Object value) { }
                }),
                Context.BIND_AUTO_CREATE);
    }

    private void showNotify(IncomingCall incomingCall) {
        if (incomingCall.getCode_client() != 0) {
            Intent intent = MyReceiver.getIntent(mContext);
            intent.putExtra(MyReceiver.INTENT_WHAT,MyReceiver.INTENT_WHAT_VALIE);
            intent.putExtra(MyReceiver.INTENT_CLIENT_BUNDLE,incomingCall.getBundle());
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    mContext,
                    0,
                    intent,
                    PendingIntent.FLAG_CANCEL_CURRENT);

            //Bitmap bitmap;
            //Bitmap tempBitmap = Bitmap.createBitmap(300, 300, Bitmap.Config.ARGB_8888);
            //BitmapFactory.Options options = new BitmapFactory.Options();
            //options.inBitmap = tempBitmap;
            // bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_school_cyan_24dp);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext)
                    .setSmallIcon(R.drawable.ic_school_black_48dp)
                    .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(),R.drawable.logo_small))
                    .setContentTitle("Звонок клиента")
                    .setContentText(incomingCall.getClient().getLastname())
                    //.setTicker("Последнее китайское предупреждение!")
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .addAction(new NotificationCompat.Action(R.drawable.ic_school_black_48dp,
                            "Открыть приложение",
                            pendingIntent))
                    .setAutoCancel(true);


            NotificationManager notificationManager =
                    (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            // Альтернативный вариант
            // NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(MyReceiver.NOTIFY_ID, builder.build());
        }
    }
    private void showWindow(IncomingCall incomingCall) {
        try {
        windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.CENTER;

/*
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        layout.setPadding(48,0,48,0);
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.HORIZONTAL);
        container.setPadding(0,0,0,384);
        ImageView imageView = new ImageView(this);
        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_launcher_background);
        imageView.setImageBitmap(bitmap);
        container.addView(imageView);
        layout.addView(container);
  */



            windowLayout = (ViewGroup) layoutInflater.inflate(R.layout.info, null);
            ImageView imageView = (ImageView) windowLayout.findViewById(R.id.imageView);
            imageView.setImageBitmap(BitmapFactory
                    .decodeResource(mContext.getResources(),R.drawable.ic_person_black_48dp));
            TextView textView = (TextView)  windowLayout.findViewById(R.id.info_textview_incoming);
            textView.setTextColor(mContext.getResources().getColor(R.color.primary_text));
            textView.setFontFeatureSettings("bold");
            textView.setText(incomingCall.getClient().getLastname());
            TextView textView2 = (TextView)  windowLayout.findViewById(R.id.info_textview_phone);
            textView2.setTextColor(mContext.getResources().getColor(R.color.primary_text));
            textView2.setFontFeatureSettings("italic");
            textView2.setText(incomingCall.getClient().getPhone());
            TextView textView3 = (TextView)  windowLayout.findViewById(R.id.info_textview_note);
            textView3.setTextColor(mContext.getResources().getColor(R.color.primary_text));
            textView3.setFontFeatureSettings("bold_italic");
            textView3.setText(incomingCall.getClient().getDescription());
            Button buttonClose=(Button) windowLayout.findViewById(R.id.info_button_cancel);

            buttonClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    closeWindow();
                }
            });



            windowManager.addView(windowLayout, params);
        }catch (WindowManager.BadTokenException e) {
            windowLayout = null;
        }

    }
    private void closeWindow() {
        if (windowLayout !=null){
            windowManager.removeView(windowLayout);
            windowLayout =null;
        }
    }
    public static final String parseDigit(String pattern) {
        String retval="";
        for (char c : pattern.toCharArray()) {
            if (Character.isDigit(c)) {
                retval+=c;
            }
        }
        return retval;
    }
}
