package ru.relastic.cloudreception.presenter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import ru.relastic.cloudreception.R;

public class Activity_Item_Schedule extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_schrdule);
    }


    public static Intent getIntent(Context context) {
        return new Intent(context, Activity_Item_Schedule.class);
    }
}
