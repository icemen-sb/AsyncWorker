package ru.relastic.cloudreception;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.HashSet;

import javax.inject.Inject;

import ru.relastic.cloudreception.dagger2.App;
import ru.relastic.cloudreception.domain.IServiceConnectCallback;
import ru.relastic.cloudreception.domain.IServiceProvide;
import ru.relastic.cloudreception.domain.MainServiceConnection;

public class SplashActivity extends AppCompatActivity {
    private static final String KEY_IGNORE_SYSTEM_PERMISSION = "ignore_system_permission";
    private static final String KEY_IGNORE_MEDIUM_PERMISSION = "ignore_medium_permission";
    private static final int REQUEST_CODE_SYSTEM_PERMISSION = 1001;
    private static final int REQUEST_CODE_MEDIUM_PERMISSION = 1002;
    private volatile boolean ignoreSystemPermission = false;
    private volatile boolean ignoreMediumPermission = false;
    private Button mButtonRequestPermissions;
    private Button mButtonIgnorePermissions;

    private final int mDelay = 2000;
    private volatile boolean uiHasStarted = false;

    private final LogoViewer mLogoViewer = new LogoViewer(mDelay);

    @Inject
    public Intent mainServiceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(KEY_IGNORE_SYSTEM_PERMISSION)) {
                ignoreSystemPermission = savedInstanceState.getBoolean(KEY_IGNORE_SYSTEM_PERMISSION);
            }
            if (savedInstanceState.containsKey(KEY_IGNORE_MEDIUM_PERMISSION)) {
                ignoreMediumPermission = savedInstanceState.getBoolean(KEY_IGNORE_MEDIUM_PERMISSION);
            }
        }
        initViews();
        initListeners();
        init();
    }
    @Override
    protected void onResume() {
        super.onResume();
        initServices();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_MEDIUM_PERMISSION) {
            ignoreMediumPermission = true;
            initServices();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE_SYSTEM_PERMISSION) {
            if (!requiresSystemPermission(this,ignoreSystemPermission)) { mLogoViewer.skip(); }
            initServices();
        }
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(KEY_IGNORE_SYSTEM_PERMISSION,ignoreSystemPermission);
        outState.putBoolean(KEY_IGNORE_MEDIUM_PERMISSION,ignoreMediumPermission);
        super.onSaveInstanceState(outState);
    }


    private void initViews(){
        ViewGroup mPermissionContainer = findViewById(R.id.splash_permission);
        mButtonIgnorePermissions = findViewById(R.id.splash_ignore_permissions);
        mButtonRequestPermissions = findViewById(R.id.splash_request_permissions);

        if (requiresSystemPermission(this, ignoreSystemPermission)) {
            mPermissionContainer.setVisibility(View.VISIBLE);
        } else {
            mPermissionContainer.setVisibility(View.INVISIBLE);
        }
    }
    private void initListeners(){
        mButtonIgnorePermissions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ignoreSystemPermission = true;
                ignoreMediumPermission = true;
                mLogoViewer.skip();
                initServices();
            }
        });
        mButtonRequestPermissions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, REQUEST_CODE_SYSTEM_PERMISSION);
                }
            }
        });
    }
    private void init(){ }
    private void initServices(){
        if (!requiresMediumPermission(this, ignoreMediumPermission)
                && !requiresSystemPermission(this,ignoreSystemPermission)
                && !uiHasStarted) {
            uiHasStarted = true;
            //<...> Показать прогресс бар ...
            App.getComponent().inject(this);
            bindService(mainServiceIntent,
                    new MainServiceConnection(new IServiceConnectCallback() {
                        @Override
                        public void onStateChange(boolean isConnected,
                                                  final IServiceProvide service,
                                                  final MainServiceConnection connection) {
                            if (isConnected) {
                                mLogoViewer.enqueueTask(new LogoViewer.OnDelayedCallback() {
                                    @Override
                                    public void execute() {
                                        //<...> Скрыть прогресс бар ...
                                        service.showUI(null);
                                        unbindService(connection);
                                        closeSplash();
                                    }
                                });
                            }
                        }
                        @Override
                        public void onChangeTask(int What, Object value) { }
                    }),
                    Context.BIND_AUTO_CREATE);
        }
    }
    private void closeSplash(){
        this.finish();
    }

    public static boolean requiresSystemPermission(Context context, boolean ignore) {
        return !ignore &&
               Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
               !Settings.canDrawOverlays(context);
    }
    public static boolean requiresMediumPermission(AppCompatActivity activity, boolean ignore) {
        boolean retVal = false;
        if (!ignore) {
            ArrayList<String> required = new ArrayList<>();
            required.add(Manifest.permission.INTERNET);
            required.add(Manifest.permission.READ_PHONE_STATE);
            //required.add(Manifest.permission.READ_CALL_LOG);
            required.add(Manifest.permission.CALL_PHONE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                required.add(Manifest.permission.ACCESS_NOTIFICATION_POLICY);

            }

            ArrayList<String> denied = new ArrayList<>();
            for (String p_name : required) {
                if (ContextCompat.checkSelfPermission(activity, p_name) == PackageManager.PERMISSION_DENIED) {
                    denied.add(p_name);
                }
            }
            if (denied.size()>0) {
                retVal = true;
                String[] list = {};
                ActivityCompat.requestPermissions(activity, denied.toArray(list), REQUEST_CODE_MEDIUM_PERMISSION);
            }
        }
        return retVal;
    }

    private static class LogoViewer extends AsyncTask<Void,Integer,Void> {

        private final int mDelay;
        private final HashSet<OnDelayedCallback> mCallbacksEnqueue = new HashSet<>();
        private final HashSet<OnProgressedCallback> mCallbacksProgress = new HashSet<>();
        private volatile boolean isSkipped = false;
        private volatile boolean isInterrupted = false;

        LogoViewer(int delay) {
            mDelay = delay;
            execute();
        }
        LogoViewer(int delay, OnDelayedCallback callbackTask) {
            mDelay = delay;
            mCallbacksEnqueue.add(callbackTask);
            execute();
        }
        LogoViewer(int delay, OnDelayedCallback callbackTask, OnProgressedCallback callbackProgress) {
            mDelay = delay;
            mCallbacksEnqueue.add(callbackTask);
            mCallbacksProgress.add(callbackProgress);
            execute();
        }
        LogoViewer(int delay, OnProgressedCallback callbackProgress) {
            mDelay = delay;
            mCallbacksProgress.add(callbackProgress);
            execute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            final int timeRange = mDelay/100;
            int lastValue = 0;
            try {
                for (int percentValue = 0; percentValue<=100; percentValue++) {
                    Integer[] progressValue = {percentValue};
                    if (!isInterrupted && !isSkipped) {
                        publishProgress(progressValue);
                        Thread.sleep(timeRange);
                    }else {
                        break;
                    }
                    lastValue = percentValue;
                }
            } catch (InterruptedException e) {
                isInterrupted = true;
                isSkipped = false;
                e.printStackTrace();
            } finally {
                if (lastValue < 100) {
                    Integer[] progressValueInterrupted = {100};
                    publishProgress(progressValueInterrupted);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (!isInterrupted) {
                synchronized (mCallbacksEnqueue) {
                    for (OnDelayedCallback callback : mCallbacksEnqueue) {
                        callback.execute();
                    }
                    mCallbacksEnqueue.clear();
                }
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            for (OnProgressedCallback callback : mCallbacksProgress) {
                callback.onProgress(values[0]);
            }
        }

        public void enqueueTask(OnDelayedCallback callback) {
            if (!isInterrupted) {
                synchronized (mCallbacksEnqueue) {
                    if (this.getStatus().equals(Status.FINISHED) || isInterrupted) {
                        callback.execute();
                    } else {
                        mCallbacksEnqueue.add(callback);
                    }
                }
            }
        }

        public void addProgressCallback(OnProgressedCallback onProgressedCallback) {
            mCallbacksProgress.add(onProgressedCallback);
        }

        private void skip(){
            isSkipped = !isInterrupted;
        }
        private void interrupt(){
            isInterrupted = true;
            isSkipped = false;
        }

        public  interface OnDelayedCallback {
            void execute();
        }
        public interface OnProgressedCallback {
            void onProgress(int percent);
        }
    }
}