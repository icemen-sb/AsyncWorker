package ru.relastic.asyncworker.dagger2;

import android.content.Context;
import android.view.View;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import ru.relastic.asyncworker.presenter.IPreserterStarter;
import ru.relastic.asyncworker.presenter.MyPresenter;
import ru.relastic.asyncworker.presenter.ZAdapter;
import ru.relastic.asyncworker.presenter.ZViewHolderClients;
import ru.relastic.asyncworker.presenter.ZViewHolderIncomingCalls;
import ru.relastic.asyncworker.presenter.ZViewHolderSchedule;

@Module
public class UIModule {
    private IPreserterStarter iPreserterStarter = null;

    public UIModule() {

    }
    @UIScope
    @Provides
    public IPreserterStarter providePresenterStarter () {
        if (iPreserterStarter == null) {
            iPreserterStarter = new MyPresenter();
        }
        return iPreserterStarter;
    }

    @UIScope
    @Provides
    public ZViewHolderClients provideZViewHolderClients (Context context) {
        return new ZViewHolderClients(new View(context));
    }
    @UIScope
    @Provides
    public ZViewHolderSchedule provideZViewHolderSchedule (Context context) {
        return new ZViewHolderSchedule(new View(context));
    }

    @UIScope
    @Provides
    public ZViewHolderIncomingCalls provideZViewHolderIncomingCalls (Context context) {
        return new ZViewHolderIncomingCalls(new View(context));
    }


    @UIScope
    @Named("clients")
    @Provides
    public ZAdapter provideZAdapterClients (ZViewHolderClients zViewHolderClients) {
        return new ZAdapter(zViewHolderClients);
    }
    @UIScope
    @Named("schedule")
    @Provides
    public ZAdapter provideZAdapterSchedule(ZViewHolderSchedule zViewHolderSchedule)  {
        return new ZAdapter(zViewHolderSchedule);
    }
    @UIScope
    @Named("incoming_calls")
    @Provides
    public ZAdapter provideZAdapterIncomingCalls(ZViewHolderIncomingCalls zViewHolderIncomingCalls)  {
        return new ZAdapter(zViewHolderIncomingCalls);
    }

}
