package ru.relastic.cloudreception.presenter;

import ru.relastic.cloudreception.R;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ActivityPreferences extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferencies);
    }


    public static Intent getIntent(Context context) {
        return new Intent(context, Activity_List_Person.class);
    }
}
