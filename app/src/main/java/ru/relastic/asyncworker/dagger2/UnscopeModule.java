package ru.relastic.asyncworker.dagger2;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import dagger.Module;
import dagger.Provides;
import ru.relastic.asyncworker.domain.MainService;
import ru.relastic.asyncworker.domain.MyHandleReceiver;

@Module
public class UnscopeModule {

    @Provides
    public Intent provideMainServiceIntent(@NonNull Context context) {
        System.out.println("-------------- ЗАПРОС ЭКЗЕМПЛЯРА MainServiceIntent");
        return new Intent(context, MainService.class);
    }

    @Provides
    public MyHandleReceiver provideMyHandleReceiver() {
        System.out.println("-------------- ЗАПРОС ЭКЗЕМПЛЯРА MyHandleReceiver");
        return new MyHandleReceiver();
    }
}
