package ru.relastic.asyncworker.domain;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Named;

import ru.relastic.asyncworker.dagger2.App;
import ru.relastic.asyncworker.presenter.IPreserterStarter;
import ru.relastic.asyncworker.repository.AuthData;
import ru.relastic.asyncworker.repository.DataTransact;
import ru.relastic.asyncworker.repository.IDataTransactCallback;
import ru.relastic.asyncworker.repository.IPreferencesTransactCallback;
import ru.relastic.asyncworker.repository.PreferencesTransact;
import ru.relastic.asyncworker.repository.TransactData.ResponseData.IncomingCall;

public class MainService extends Service {

    private MyServiceProvider myServiceProvider;
    private LocalBinder mBinder;


    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("**** сервис сдох");
    }

    @Override
    public void onCreate() {
        myServiceProvider = new MyServiceProvider();
        mBinder = new LocalBinder();
        System.out.println("**** сервис создан");
    }


    @Override
    public IBinder onBind(Intent intent) {
        System.out.println("**** к сервису подключились");
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        System.out.println("**** от сервиса отключились");
        return super.onUnbind(intent);
    }



    //INTERACTOR:
    public static class MyServiceProvider implements IServiceProvide {
        @Inject
        Context contrxt;

        @Inject
        public AuthData authData;

        @Inject
        public PreferencesTransact preferenciesTransact;

        @Inject
        @Named("local")
        public DataTransact dataTransactLocal;

        @Inject
        @Named("remote")
        public DataTransact dataTransactRemote;

        @Inject
        IPreserterStarter myPresenterStarter;



        public ArrayList<IServiceConnectCallback> mListeners = new ArrayList<>();




        public MyServiceProvider() {
            App.getUIComponent().inject(this);
        }


        @Override
        public void requestData(AuthData data,
                                IDataTransactCallback callback,
                                boolean toLocal) {
            //!!!
            //toLocal = false;
            //!!!
            DataTransact dataTransact = toLocal ? dataTransactLocal : dataTransactRemote;
            dataTransact.requestData(data,callback);
        }

        @Override
        public void requestPropertiesGet(IPreferencesTransactCallback callback,
                                         String namePreference) {
            //<...>
        }

        @Override
        public void requestPropertiesSet(IPreferencesTransactCallback callback,
                                         String namePreference,
                                         Object value) {
            //<...>
        }

        @Override
        public AuthData getAuthData() {
            return authData;
        }

        @Override
        public void syncData() {
            //<...> синхронизируем данные
        }

        @Override
        public void showUI(IncomingCall incomingCall) {
            //App.getUIComponent().inject(this);
            myPresenterStarter.startUI(incomingCall);

        }

        @Override
        public void addListener(IServiceConnectCallback callback) {
            mListeners.add(callback);
        }
        @Override
        public void removeListener(IServiceConnectCallback callback) {
            mListeners.remove(callback);
        }
    }

    public class LocalBinder extends Binder {
        IServiceProvide getService(){
            return myServiceProvider;
        }
    }
}
