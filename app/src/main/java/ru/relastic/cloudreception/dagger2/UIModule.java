package ru.relastic.cloudreception.dagger2;

import android.content.Context;
import android.view.View;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import ru.relastic.cloudreception.presenter.IPreserterStarter;
import ru.relastic.cloudreception.presenter.MyPresenter;
import ru.relastic.cloudreception.presenter.ZAdapter;
import ru.relastic.cloudreception.presenter.ZViewHolderClients;
import ru.relastic.cloudreception.presenter.ZViewHolderIncomingCalls;
import ru.relastic.cloudreception.presenter.ZViewHolderSchedule;

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
