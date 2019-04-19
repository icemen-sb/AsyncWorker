package ru.relastic.asyncworker.repository;

public interface PreferencesTransact {
    public static final String PREFERENCES_KEY = "preferencies_key";
    public static final String PREF_NAME_SECRET_KEY = "secret_key";
    public static final String PREF_NAME_LOGIN = "login";
    public static final String PREF_NAME_DESCTIPTION = "description";
    public static final String PREF_NAME_LAST_SYNC = "last_sync";
    //<...>
    public static final int PREF_REQUEST_GET = 0;
    public static final int PREF_REQUEST_GETAUTH = 1;
    public static final int PREF_REQUEST_SET = 10;
    public static final int PREF_REQUEST_SETAUTHKEY = 11;

    public void getPref(String name, IPreferencesTransactCallback callback);
    public void setPref(String name, Object value, IPreferencesTransactCallback callback);
    public void getAuth(IPreferencesTransactCallback callback);
    public void setAuthKey(String value, IPreferencesTransactCallback callback);
    public void addListeners(IPreferencesTransactCallback callback);
    public void removeListeners(IPreferencesTransactCallback callback);
}
