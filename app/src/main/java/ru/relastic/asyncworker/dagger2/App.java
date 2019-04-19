package ru.relastic.asyncworker.dagger2;

import android.app.Application;

public class App extends Application {
    private static AppComponent component;
    private static UIComponent uiComponent;
    //private static UnscopeComponent unscopeComponent;
    public static AppComponent getComponent() {
        return component;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        component = buildComponent();
    }

    protected AppComponent buildComponent() {
        return DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }

    public static UIComponent getUIComponent() {
        if (uiComponent==null) {
            uiComponent = component.uiComponent(new UIModule());
        }
        return uiComponent;
    }
    public static void destroyUIComponent() {
        uiComponent = null;
    }


}