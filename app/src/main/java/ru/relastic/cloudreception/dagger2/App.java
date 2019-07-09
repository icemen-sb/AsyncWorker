package ru.relastic.cloudreception.dagger2;

import android.app.Application;

public class App extends Application {
    public static final String KEY_LAUNCH_DETAILS = "key_launch_details";
    public static final int LAUNCH_DETAILS_NOTIFY_CLIENT = 1;
    private static AppComponent component;
    private static UIComponent uiComponent;
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