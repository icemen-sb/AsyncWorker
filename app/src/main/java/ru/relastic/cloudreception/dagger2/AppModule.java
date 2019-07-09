package ru.relastic.cloudreception.dagger2;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.relastic.cloudreception.repository.AuthData;
import ru.relastic.cloudreception.repository.DataTransact;
import ru.relastic.cloudreception.repository.IPreferencesTransactCallback;
import ru.relastic.cloudreception.repository.PreferencesTransact;
import ru.relastic.cloudreception.repository.RetrofitDataTransact;
import ru.relastic.cloudreception.repository.RoomDataTransact;
import ru.relastic.cloudreception.repository.SharedPreferencesTransact;

@Module
public class AppModule {
    private Context mContext;
    private PreferencesTransact mSharedPreferences;
    private AuthData mAuthData = new AuthData();
    private DataTransact mDataTransactLocal;
    private DataTransact mDataTransactRemote;

    public AppModule(@NonNull Context context) {
        mContext = context;
        mSharedPreferences = new SharedPreferencesTransact(context);
        IPreferencesTransactCallback callback = new IPreferencesTransactCallback() {
            @Override
            public void responseData(Object value) {
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
        return mContext;
    }

    @Provides
    @Singleton
    public PreferencesTransact providePreferenciesTransact() {
        return mSharedPreferences;
    }

    @Provides
    @Singleton
    public AuthData provideAuthData() {
        return mAuthData;
    }


    @Named("local")
    @Provides
    @Singleton
    public DataTransact provideDataTransactLocal() {
        if (mDataTransactLocal == null) {mDataTransactLocal = new RoomDataTransact(mContext);}
        return mDataTransactLocal;
    }

    @Named("remote")
    @Provides
    @Singleton
    public DataTransact provideDataTransactRemote() {
        if (mDataTransactRemote == null) {mDataTransactRemote = new RetrofitDataTransact();}
        return mDataTransactRemote;
    }
}
