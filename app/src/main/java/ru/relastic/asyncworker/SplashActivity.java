package ru.relastic.asyncworker;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;

import javax.inject.Inject;

import ru.relastic.asyncworker.dagger2.App;
import ru.relastic.asyncworker.domain.IServiceConnectCallback;
import ru.relastic.asyncworker.domain.IServiceProvide;
import ru.relastic.asyncworker.domain.MainServiceConnection;
import ru.relastic.asyncworker.presenter.MyPresenter;

public class SplashActivity extends AppCompatActivity {


    @Inject
    public Intent mainServiceIntent;
    /*
    @Inject
    public MyPresenter myPresenter;
*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        layout.setPadding(48,0,48,0);
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.HORIZONTAL);
        container.setPadding(0,0,0,384);
        ImageView imageView = new ImageView(this);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.logo_small);
        imageView.setImageBitmap(bitmap);
        container.addView(imageView);
        layout.addView(container);
        setContentView(layout);

        App.getComponent().inject(this);
        //App.getUIComponent().inject(this);

        //myPresenter.startUI();


        bindService(mainServiceIntent,

                new MainServiceConnection(new IServiceConnectCallback() {
                    @Override
                    public void onStateChange(boolean isConnected,
                                              IServiceProvide service,
                                              MainServiceConnection connection) {
                        if (isConnected) {
                            service.showUI(null);
                            //closeSplash();
                        }
                        unbindService(connection);
                    }
                    @Override
                    public void onChangeTask(int What, Object value) { }
                }),
                Context.BIND_AUTO_CREATE);

    }

    private void closeSplash(){
        this.finish();
    }
}
