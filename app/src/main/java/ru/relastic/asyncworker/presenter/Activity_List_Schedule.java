package ru.relastic.asyncworker.presenter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import ru.relastic.asyncworker.R;
import android.os.Bundle;

public class Activity_List_Schedule extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_schedule);
    }


    public static Intent getIntent(Context context) {
        return new Intent(context, Activity_List_Schedule.class);
    }
}
