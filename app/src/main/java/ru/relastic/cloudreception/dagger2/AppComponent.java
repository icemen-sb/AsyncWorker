package ru.relastic.cloudreception.dagger2;

import javax.inject.Singleton;
import dagger.Component;
import ru.relastic.cloudreception.SplashActivity;
import ru.relastic.cloudreception.domain.MyHandleReceiver;
import ru.relastic.cloudreception.domain.MyReceiver;
import ru.relastic.cloudreception.presenter.MyPresenter;

@Component (modules = {AppModule.class, UnScopeModule.class})
@Singleton
public interface AppComponent {
    UIComponent uiComponent(UIModule uiModule);

    void inject(MyReceiver myReceiver);
    void inject(SplashActivity splashActivity);
    void inject(MyHandleReceiver myHandleReceiver);
    void inject(MyPresenter myPresenter);
}
