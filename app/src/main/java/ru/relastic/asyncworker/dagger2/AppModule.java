package ru.relastic.asyncworker.dagger2;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.relastic.asyncworker.repository.AuthData;
import ru.relastic.asyncworker.repository.DataTransact;
import ru.relastic.asyncworker.repository.IPreferencesTransactCallback;
import ru.relastic.asyncworker.repository.PreferencesTransact;
import ru.relastic.asyncworker.repository.RetrofitDataTransact;
import ru.relastic.asyncworker.repository.RoomDataTransact;
import ru.relastic.asyncworker.repository.SharedPreferencesTransact;

@Module
public class AppModule {
    private Context mContext;
    private PreferencesTransact mSharedPreferences;
    private AuthData mAuthData = new AuthData();
    private DataTransact mDataTransactLocal;
    private DataTransact mDataTransactRemote;

    public AppModule(@NonNull Context context) {
        System.out.println("-------------- СОЗДАНИЕ ЭКЗЕМПЛЯРА Context");
        mContext = context;
        System.out.println("-------------- СОЗДАНИЕ ЭКЗЕМПЛЯРА PreferencesTransact (SharedPreferencesTransact)");
        mSharedPreferences = new SharedPreferencesTransact(context);
        IPreferencesTransactCallback callback = new IPreferencesTransactCallback() {
            @Override
            public void responseData(Object value) {
                System.out.println("-------------- СОЗДАНИЕ ЭКЗЕМПЛЯРА AuthData");
                AppModule.this.mAuthData.updateREG((Bundle) value);
            }

            @Override
            public void onChange(int What, Object value) {
                if (What == PreferencesTransact.PREF_REQUEST_SETAUTHKEY) {
                    mAuthData.updateREG(mAuthData.getLogin(), (String)value);
                }
            }
        };
        mSharedPreferences.getAuth(callback);
        mSharedPreferences.addListeners(callback);
    }

    @Provides
    @Singleton
    public Context provideContext(){
        System.out.println("--------------   ЗАПРОС ЭКЗЕМПЛЯРА Context");
        return mContext;
    }

    @Provides
    @Singleton
    public PreferencesTransact providePreferenciesTransact() {
        System.out.println("-------------- ЗАПРОС ЭКЗЕМПЛЯРА PreferencesTransact");
        return mSharedPreferences;
    }

    @Provides
    @Singleton
    public AuthData provideAuthData() {
        System.out.println("-------------- ЗАПРОС ЭКЗЕМПЛЯРА AuthData. isReady(): " + mAuthData.isReady());
        return mAuthData;
    }


    @Named("local")
    @Provides
    @Singleton
    public DataTransact provideDataTransactLocal() {
        System.out.println("-------------- СОЗДАНИЕ ЭКЗЕМПЛЯРА DataTransact (RoomDataTransact)");
        if (mDataTransactLocal == null) {mDataTransactLocal = new RoomDataTransact(mContext);}
        return mDataTransactLocal;
    }

    @Named("remote")
    @Provides
    @Singleton
    public DataTransact provideDataTransactRemote() {
        System.out.println("-------------- СОЗДАНИЕ ЭКЗЕМПЛЯРА DataTransact (RetrofitDataTransact)");
        if (mDataTransactRemote == null) {mDataTransactRemote = new RetrofitDataTransact();}
        return mDataTransactRemote;
    }
}
