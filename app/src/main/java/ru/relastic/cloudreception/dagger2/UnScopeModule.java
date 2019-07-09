package ru.relastic.cloudreception.dagger2;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import dagger.Module;
import dagger.Provides;
import ru.relastic.cloudreception.domain.MainService;
import ru.relastic.cloudreception.domain.MyHandleReceiver;

@Module
public class UnScopeModule {

    @Provides
    public Intent provideMainServiceIntent(@NonNull Context context) {
        return new Intent(context, MainService.class);
    }

    @Provides
    public MyHandleReceiver provideMyHandleReceiver() {
        return new MyHandleReceiver();
    }
}
