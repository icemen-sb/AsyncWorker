package ru.relastic.asyncworker.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Set;

public class SharedPreferencesTransact extends AsyncTask<Integer,Integer,Object> implements PreferencesTransact {

    private final SharedPreferences mSharedPreferences;
    private final ArrayList<IPreferencesTransactCallback> mListeners = new ArrayList<>();
    private IPreferencesTransactCallback mCallback;
    private String mPrefName;
    private Object mNewValue;
    private int mWhat = 0;

    public SharedPreferencesTransact(Context context) {
        this.mSharedPreferences = context.getSharedPreferences(PREFERENCES_KEY,
                Context.MODE_PRIVATE);
    }
    private SharedPreferencesTransact(SharedPreferences sharedPreferences,
                                      ArrayList<IPreferencesTransactCallback> mListeners,
                                      IPreferencesTransactCallback callback,
                                      String prefName,
                                      Object newValue,
                                      int type) {
        mListeners.addAll(mListeners);
        this.mSharedPreferences = sharedPreferences;
        this.mCallback = callback;
        this.mPrefName = prefName;
        this.mNewValue = newValue;
        this.execute(type);
    }

    @Override
    public void getPref(String name, IPreferencesTransactCallback callback) {
        this.enqueue(this.mSharedPreferences,this.mListeners,callback,name,null,PREF_REQUEST_GET);
    }

    @Override
    public void setPref(String name, Object value, IPreferencesTransactCallback callback) {
        this.enqueue(this.mSharedPreferences,this.mListeners,callback,name,value,PREF_REQUEST_SET);
    }

    @Override
    public void getAuth(IPreferencesTransactCallback callback) {
        this.enqueue(this.mSharedPreferences,this.mListeners,callback,null,null,PREF_REQUEST_GETAUTH);
    }

    @Override
    public void setAuthKey(String value, IPreferencesTransactCallback callback) {
        this.enqueue(this.mSharedPreferences,this.mListeners,callback,null,value,PREF_REQUEST_SETAUTHKEY);
    }

    @Override
    public void addListeners(IPreferencesTransactCallback callback) {
        mListeners.add(callback);
    }

    @Override
    public void removeListeners(IPreferencesTransactCallback callback) {
        mListeners.remove(callback);
    }

    @Override
    protected Object doInBackground(Integer... integers) {
        SharedPreferences.Editor editor;
        Object retVal = null;
        int type = (int)integers[0];
        switch (type) {
            case PREF_REQUEST_GET:
                retVal = this.mSharedPreferences.getString(this.mPrefName,null);
                break;
            case PREF_REQUEST_SET:
                editor = this.mSharedPreferences.edit();
                editor.putString(this.mPrefName,this.mNewValue.toString());
                editor.commit();
                retVal = this.mSharedPreferences.getString(this.mPrefName,null);
                break;
            case PREF_REQUEST_GETAUTH:

                Set<String> pars = mSharedPreferences.getAll().keySet();
                //for (String sn:pars) {
                //    System.out.println("####################:"+sn);
                //}

                if (this.mSharedPreferences.contains(PREF_NAME_LOGIN) &&
                                    this.mSharedPreferences.contains(PREF_NAME_SECRET_KEY)) {
                    String login = this.mSharedPreferences.getString(PREF_NAME_LOGIN, null);
                    String secret_key = this.mSharedPreferences.getString(PREF_NAME_SECRET_KEY, null);
                    Bundle auth = new Bundle();
                    auth.putString(PREF_NAME_LOGIN, login);
                    auth.putString(PREF_NAME_SECRET_KEY, secret_key);
                    retVal = auth;

                }else {

                }
                break;
            case PREF_REQUEST_SETAUTHKEY:
                if (this.mSharedPreferences.contains(PREF_NAME_SECRET_KEY)) {
                    editor = this.mSharedPreferences.edit();
                    editor.putString(this.mPrefName,this.mNewValue.toString());
                    editor.commit();
                    retVal =this.mNewValue.toString();
                }
                break;
        }
        this.mWhat = type;
        return retVal;
    }

    @Override
    protected void onPostExecute(Object value) {
        for(IPreferencesTransactCallback callback : this.mListeners) {
            callback.onChange(mWhat, value);
        }
        this.mCallback.responseData(value);
    }

    private static void enqueue(SharedPreferences mSharedPreferences,
                                ArrayList<IPreferencesTransactCallback> listeners,
                                IPreferencesTransactCallback callback,
                                String prefName,
                                Object newValue,
                                int type) {

        //выполняем на новом экземпляре AsyncTask
        SharedPreferencesTransact newInstance = new SharedPreferencesTransact(
                                                                mSharedPreferences,
                                                                listeners,
                                                                callback,
                                                                prefName,
                                                                newValue,
                                                                type);
    }
}
