package ru.relastic.asyncworker.presenter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import ru.relastic.asyncworker.R;

public class Activity_AddByPerson extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__add_by_person);
    }


    public static Intent getIntent(Context context) {
        return new Intent(context, Activity_AddByPerson.class);
    }
}
