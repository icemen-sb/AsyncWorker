package ru.relastic.cloudreception.domain;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.drawable.shapes.RoundRectShape;
import android.graphics.drawable.shapes.Shape;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.service.notification.StatusBarNotification;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import javax.inject.Inject;

import ru.relastic.cloudreception.R;
import ru.relastic.cloudreception.SplashActivity;
import ru.relastic.cloudreception.dagger2.App;
import ru.relastic.cloudreception.repository.AuthData;
import ru.relastic.cloudreception.repository.IDataTransactCallback;
import ru.relastic.cloudreception.repository.TransactData;
import ru.relastic.cloudreception.repository.TransactData.*;
import ru.relastic.cloudreception.repository.TransactData.ResponseData.*;

public class MyHandleReceiver {
    private static final Object MONITOR = new Object();
    private static final int DEFAULT_GROUP_ID = -100;
    private static final String DEFAULT_GROUP_KEY = "notifies";
    private static final int COMMAND_SHOW_WINDOW = 1001;
    private static final int COMMAND_HIDE_WINDOW = 1002;

    private static WindowManager windowManager = null;
    private static GradientFrameLayout mContainer = null;
    private static Handler mHandler = null;


    @Inject
    public Context mContext;
    @Inject
    public Intent mainServiceIntent;

    public MyHandleReceiver() {
        App.getComponent().inject(this);
    }

    public void enqueue(int what, Bundle intentData) {
        if (windowManager == null) {
            windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        }
        if (mHandler == null) {
            mHandler = new Handler(mContext.getMainLooper(), new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    return changeStateWindow(mContext, msg.what, msg.getData());
                }
            });
        }
        switch (what) {
            case MyReceiver.INTENT_WHAT_INCOMING_CALL :
                //входящий звонок
                handleCall(intentData);
                break;
            case MyReceiver.INTENT_WHAT_RETURN_PENDING:
                //интент с нотификации
                handleIntent(intentData);
                break;
            case MyReceiver.INTENT_WHAT_HIDE_NOTIFY:
                //интент запросом отмены нотификации
                handleHideNotify(intentData);
                break;
        }
    }

    private void handleCall(Bundle intentData) {
        //TelephonyManager.EXTRA_STATE_RINGING
        //TelephonyManager.EXTRA_STATE_OFFHOOK
        //TelephonyManager.EXTRA_STATE_IDLE

        String phoneState = intentData.getString(TelephonyManager.EXTRA_STATE,null);

        if (phoneState != null && phoneState.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
            String phoneExtra = intentData.getString(TelephonyManager.EXTRA_INCOMING_NUMBER, null);
                        Log.v("LOG: ",
                    "handled incoming state: " + phoneState + ", contain extra: " + phoneExtra);
            final String phoneNumber = parseDigit(phoneExtra);
            if (phoneNumber.length()>=10) {
                final MainServiceConnection serviceConnection =
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
                                                    IncomingCall incomingCall;
                                                    int type;
                                                    if((response.getResponse_msg().getCode()==0)
                                                            && (response.getResponse_data().getClients().size()>0)) {

                                                        Client client = response.getResponse_data().getClients().get(0);
                                                        Log.v("LOG: ","Client exist: " +
                                                                client.getFullName() + ", code: " +
                                                                client.getCode());
                                                        incomingCall = new IncomingCall(client);
                                                        //show alert and notification
                                                        type = AuthData.AUTH_TYPE_TR_NOTIFY;
                                                        showNotify(incomingCall);
                                                        showWindow(incomingCall);
                                                    } else {
                                                        type = AuthData.AUTH_TYPE_TR_UNKNOWN;
                                                        Log.v("LOG: ","Client NOT exist: " +
                                                                phoneNumber);
                                                        incomingCall = new IncomingCall(phoneNumber);
                                                    }
                                                    //change local DB
                                                    service.requestData(auth.next().setType(type).setBody(
                                                            new ResponseData(incomingCall)),
                                                            new IDataTransactCallback() {
                                                                @Override
                                                                public void onResponseData(TransactData response) {
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
                mContext.bindService(mainServiceIntent,serviceConnection, Context.BIND_AUTO_CREATE);
            }
        } else {
            Log.v("LOG: ","handled incoming state: " + phoneState + ", contain extra: " +
                    intentData.getString(TelephonyManager.EXTRA_INCOMING_NUMBER) );
            closeWindow();
        }
    }
    private void handleIntent(final Bundle intentData) {
        //<...> Возможно следует исп. интент приложения с Extra данными, включающими intentData
        final IncomingCall incomingCall = new IncomingCall(intentData.getBundle(MyReceiver.INTENT_INCOMING_CALL_BUNDLE));
        mContext.bindService(mainServiceIntent,
                new MainServiceConnection(new IServiceConnectCallback() {
                    @Override
                    public void onStateChange(boolean isConnected,
                                              IServiceProvide service,
                                              MainServiceConnection connection) {
                        if (isConnected) {
                            Log.v("LOG: ", "return notification pending by client code: " +
                                    incomingCall.getClient().getCode());
                            service.showUI(incomingCall);
                        }
                        mContext.unbindService(connection);
                    }
                    @Override
                    public void onChangeTask(int What, Object value) { }
                }),
                Context.BIND_AUTO_CREATE);
    }
    private void handleHideNotify(Bundle intentData) {
        if (intentData != null) {
            final IncomingCall incomingCall = new IncomingCall(
                    intentData.getBundle(MyReceiver.INTENT_INCOMING_CALL_BUNDLE));
            if (incomingCall.getClient() != null) {
                NotificationManager notificationManager =
                        (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                Log.v("LOG: ", "cancel notification by client code: " +
                        incomingCall.getClient().getCode());

                boolean toCancelGroup = false;
                if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
                    //Группа сама не закрывается
                    if (notificationManager.getActiveNotifications().length <= 2) {
                        for (StatusBarNotification notification : notificationManager.getActiveNotifications()) {
                            if (notification.getId()==incomingCall.getClient().getCode()) {
                                toCancelGroup = true;
                                break;
                            }
                        }
                    }
                }

                if (toCancelGroup) {
                    notificationManager.cancel(DEFAULT_GROUP_ID);
                } else {
                    notificationManager.cancel(incomingCall.getClient().getCode());
                }

            }
        }
    }

    private void showNotify(IncomingCall incomingCall) {
        if (incomingCall.getCode_client() != 0) {
            NotificationManager notificationManager =
                    (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            Log.v("LOG: ", "create notification by client code: " +
                    incomingCall.getClient().getCode());
            notificationManager.notify(incomingCall.getClient().getCode(), createNotification(mContext, incomingCall));
        }
    }
    private void showWindow(IncomingCall incomingCall) {
        Message message = new Message();
        message.what = COMMAND_SHOW_WINDOW;
        message.setData(incomingCall.getBundle());
        message.setTarget(mHandler);
        message.sendToTarget();
    }
    private void closeWindow() {
        Message message = new Message();
        message.what = COMMAND_HIDE_WINDOW;
        message.setTarget(mHandler);
        message.sendToTarget();
    }



    private static String parseDigit(@Nullable String pattern) {
        StringBuilder retVal= new StringBuilder();
        if (pattern!=null) {
            for (char c : pattern.toCharArray()) {
                if (Character.isDigit(c)) {
                    retVal.append(c);
                }
            }
        }

        return retVal.toString();
    }


    private static Notification createNotification(Context context, IncomingCall incomingCall) {
        Intent intent = MyReceiver.getIntent(context);
        intent.putExtra(MyReceiver.INTENT_WHAT, MyReceiver.INTENT_WHAT_VALUE);
        intent.putExtra(MyReceiver.INTENT_INCOMING_CALL_BUNDLE,incomingCall.getBundle());

        PendingIntent pendingIntentGroup = PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                incomingCall.getClient().getCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder group;
        NotificationCompat.Builder builder;
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        int notificationImportance;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channel_id = context.getString(R.string.notifications_channel_id);
            String channel_name = context.getString(R.string.notifications_channel_name);
            String channel_description = context.getString(R.string.notifications_channel_description);
            if (notificationManager.getNotificationChannel(channel_id)==null) {
                NotificationChannel channel = new NotificationChannel(channel_id,
                        channel_name, NotificationManager.IMPORTANCE_DEFAULT);
                channel.setDescription(channel_description);
                notificationManager.createNotificationChannel(channel);
            }
            group = new NotificationCompat.Builder(context, channel_id);
            builder = new NotificationCompat.Builder(context, channel_id);
            notificationImportance = NotificationManager.IMPORTANCE_DEFAULT;
        } else {
            group = new NotificationCompat.Builder(context);
            builder = new NotificationCompat.Builder(context);
            notificationImportance = NotificationManagerCompat.IMPORTANCE_DEFAULT;
            // Альтернативный вариант
            //NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);
        }

        //Bitmap bitmap = BitmapFactory.decodeResource(Resources.getSystem().getDrawable(R.drawable.ic_launcher_background))
        //Bitmap bitmap = Bitmap.createBitmap(512,512,null);
        //Drawable drawable = context.getResources().getDrawable(R.drawable.ic_launcher_background);
        //drawable.draw(new Canvas(bitmap));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            group.setSmallIcon(R.drawable.ic_launcher)
                            .setContentInfo("notify clients")
                            .setGroup(DEFAULT_GROUP_KEY)
                            .setContentIntent(pendingIntentGroup)
                            .setAutoCancel(true)
                            .setGroupSummary(true);
            notificationManager.notify(DEFAULT_GROUP_ID, group.build());
        }

        //builder.setSmallIcon(R.mipmap.ic_launcher)
        builder.setSmallIcon(R.drawable.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.drawable.ic_launcher))
                .setContentTitle("Звонок клиента")
                .setShowWhen(true)
                .setContentText(incomingCall.getClient().getFullName())
                //.setTicker("Последнее китайское предупреждение!")
                .setPriority(notificationImportance)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setGroup(DEFAULT_GROUP_KEY);
        return builder.build();
    }
    private static boolean changeStateWindow(Context context, int command, Bundle incomingCall) {
        boolean retVal = false;
        synchronized (MONITOR) {
            if (SplashActivity.requiresSystemPermission(context,false)) {
                Log.v("LOG: ", "required SYSTEM_ALERT_WINDOW permission for view alert");
            } else {
                try {
                    switch (command) {
                        case COMMAND_HIDE_WINDOW:
                            if (mContainer != null) {
                                windowManager.removeView(mContainer);
                                mContainer = null;
                            }
                            break;
                        case COMMAND_SHOW_WINDOW:
                            if (incomingCall != null) {
                                IncomingCall newIncomingCall = new IncomingCall(incomingCall);
                                if (mContainer != null) {
                                    if (mContainer.getIncomingCall().getClient().getCode() !=
                                            newIncomingCall.getClient().getCode()) {
                                        mContainer.setContent(context, newIncomingCall);
                                    }
                                }else {
                                    mContainer = new GradientFrameLayout(context, new IncomingCall(incomingCall));
                                    int windowsType;
                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                        windowsType = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
                                    }else {
                                        windowsType = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
                                    }
                                    WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                                            WindowManager.LayoutParams.MATCH_PARENT,
                                            WindowManager.LayoutParams.WRAP_CONTENT,
                                            windowsType,
                                            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                                            PixelFormat.TRANSLUCENT);
                                    params.gravity = Gravity.LEFT;
                                    //params.alpha = 0.5f;
                                    //params.horizontalMargin = 0.5f;
                                    windowManager.addView(mContainer,params);
                                }
                            }
                            break;
                    }
                    retVal = true;
                } catch (IllegalStateException | IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }

        }
        return retVal;
    }

    private static class GradientFrameLayout extends FrameLayout {
        private IncomingCall mIncomingCall;
        private int mHeight = 0;
        private Shader mShader = null;

        public GradientFrameLayout(@NonNull Context context,
                                   final @NonNull IncomingCall incomingCall) {
            super(context);
            setContent(context, incomingCall);
        }

        @Override
        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            final int height = getMeasuredHeight();
            if (height!=mHeight || mShader == null) {
                mShader = createLinearGradientVShader(getContext().getResources().getColor(R.color.primary_light),
                        getContext().getResources().getColor(R.color.background),
                        height);
                mHeight=height;
            }
            final Drawable background = getBackground();
            if (background!=null &&  background.getClass().equals(ShapeDrawable.class)) {
                ((ShapeDrawable)background).getPaint().setShader(mShader);
                setBackground(background);
            }else {
                setBackground(createShapeDrawable(mShader));
            }
            super.onLayout(changed, left, top, right, bottom);
        }

        public IncomingCall getIncomingCall() {
            return mIncomingCall;
        }

        public void setContent(@NonNull Context context, IncomingCall incomingCall) {
            removeAllViews();
            createContent(context,incomingCall,this);
            mIncomingCall = incomingCall;
        }

        private static void createContent(@NonNull Context context,
                                               final @NonNull IncomingCall incomingCall,
                                               @NonNull ViewGroup container){
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            ViewGroup windowLayout = (ViewGroup) layoutInflater.inflate(R.layout.info, container);

            ImageView imageView = windowLayout.findViewById(R.id.imageView);
            imageView.setImageBitmap(BitmapFactory
                    .decodeResource(context.getResources(),R.drawable.ic_phone_forwarded_black_48dp));
            TextView textView;

            textView = windowLayout.findViewById(R.id.info_textview_title);
            textView.setTextColor(context.getResources().getColor(R.color.primary_text));
            textView.setFontFeatureSettings("bold");

            textView = windowLayout.findViewById(R.id.info_textview_incoming);
            textView.setText(incomingCall.getClient().getFullName());
            textView.setTextColor(context.getResources().getColor(R.color.primary_text));
            textView.setFontFeatureSettings("italic");
            textView.setVisibility((textView.getText().toString().trim().length()!=0) ? VISIBLE : GONE);

            TextView textView2 = windowLayout.findViewById(R.id.info_textview_phone);
            textView2.setText(incomingCall.getClient().getPhone());
            textView2.setTextColor(context.getResources().getColor(R.color.primary_text));
            textView.setFontFeatureSettings("italic");
            textView2.setVisibility((textView2.getText().toString().trim().length()!=0) ? VISIBLE : GONE);

            TextView textView3 = windowLayout.findViewById(R.id.info_textview_note);
            textView3.setText(incomingCall.getClient().getDescription());
            textView3.setTextColor(context.getResources().getColor(R.color.primary_text));
            textView.setFontFeatureSettings("bold_italic");
            textView3.setVisibility((textView3.getText().toString().trim().length()!=0) ? VISIBLE : GONE);

            Button buttonClose= windowLayout.findViewById(R.id.info_button_cancel);
            buttonClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Message message = new Message();
                    message.what = COMMAND_HIDE_WINDOW;
                    message.setTarget(mHandler);
                    message.sendToTarget();
                }
            });
        }


        private static LinearGradient createLinearGradientVShader(@ColorInt int borderColor,
                                                                  @ColorInt int centerColor,
                                                                  int height) {
            return new LinearGradient(0, 0, 0, (((float)height)/2),
                    borderColor, centerColor, Shader.TileMode.MIRROR);
        }
        private static ShapeDrawable createShapeDrawable(Shader shader) {
            int rad = 32;
            float [] outR = new float [] {rad, rad, rad, rad, rad, rad, rad, rad};
            ShapeDrawable shapeDrawable = new ShapeDrawable(new RoundRectShape(outR, null, null));
            shapeDrawable.getPaint().setShader(shader);
            return  shapeDrawable;
        }
        private static GradientDrawable createGradientDrawable (@ColorInt int[] colors) {
            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setColors(colors);
            return gradientDrawable;
        }
        private static ShapeDrawable createShapeDrawable(@Nullable Shape shape, @Nullable Shader shader) {
            if (shape == null) { shape = new RectShape(); }
            ShapeDrawable shapeDrawable = new ShapeDrawable(shape);
            if (shader != null) {
                shapeDrawable.getPaint().setShader(shader);
            } else {
                shapeDrawable.getPaint().setColor(Color.WHITE);
            }
            return  shapeDrawable;
        }
    }
}
