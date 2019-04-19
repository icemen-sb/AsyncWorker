package ru.relastic.asyncworker.repository;

public interface IPreferencesTransactCallback {
    //public static final int PREFERENCES_WHAT_AUTH_CHANGED = 1;

    public void responseData(Object value);
    public void onChange(int What, Object value);
}
