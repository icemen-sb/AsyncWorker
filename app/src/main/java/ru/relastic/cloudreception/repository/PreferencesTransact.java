package ru.relastic.cloudreception.repository;

public interface PreferencesTransact {
    String PREFERENCES_KEY = "preferencies_key";
    String PREF_NAME_SECRET_KEY = "secret_key";
    String PREF_NAME_LOGIN = "login";
    String PREF_NAME_DESCTIPTION = "description";
    String PREF_NAME_LAST_SYNC = "last_sync";
    //<...>
    int PREF_REQUEST_GET = 0;
    int PREF_REQUEST_GETAUTH = 1;
    int PREF_REQUEST_SET = 10;
    int PREF_REQUEST_SETAUTHKEY = 11;

    void getPref(String name, IPreferencesTransactCallback callback);
    void setPref(String name, Object value, IPreferencesTransactCallback callback);
    void getAuth(IPreferencesTransactCallback callback);
    void setAuthKey(String value, IPreferencesTransactCallback callback);
    void addListeners(IPreferencesTransactCallback callback);
    void removeListeners(IPreferencesTransactCallback callback);
}
