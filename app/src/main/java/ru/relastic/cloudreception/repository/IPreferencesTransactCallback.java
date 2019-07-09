package ru.relastic.cloudreception.repository;

public interface IPreferencesTransactCallback {
    //public static final int PREFERENCES_WHAT_AUTH_CHANGED = 1;

    void responseData(Object value);
    void onChange(int What, Object value);
}
