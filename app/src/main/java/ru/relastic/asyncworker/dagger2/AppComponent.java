package ru.relastic.asyncworker.dagger2;

import javax.inject.Singleton;
import dagger.Component;
import ru.relastic.asyncworker.SplashActivity;
import ru.relastic.asyncworker.domain.MyHandleReceiver;
import ru.relastic.asyncworker.domain.MyReceiver;
import ru.relastic.asyncworker.presenter.Activity_List_Person;
import ru.relastic.asyncworker.domain.MainService.MyServiceProvider;
import ru.relastic.asyncworker.presenter.MyPresenter;

@Component (modules = {AppModule.class, UnscopeModule.class})
@Singleton
public interface AppComponent {
    UIComponent uiComponent(UIModule uiModule);

    void inject(MyReceiver myReceiver);
    void inject(SplashActivity splashActivity);
    void inject(MyHandleReceiver myHandleReceiver);
    void inject(MyPresenter myPresenter);
}
