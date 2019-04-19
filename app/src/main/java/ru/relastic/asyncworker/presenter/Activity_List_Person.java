package ru.relastic.asyncworker.presenter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import javax.inject.Inject;
import javax.inject.Named;

import ru.relastic.asyncworker.R;
import ru.relastic.asyncworker.dagger2.App;
import ru.relastic.asyncworker.repository.TransactData.ResponseData.*;


public class Activity_List_Person extends AppCompatActivity {
    public static final int INTENT_REQUEST_ADD_PERSON       = 0;
    public static final int INTENT_REQUEST_EDIT_PERSON      = 1;
    public static final int INTENT_REQUEST_PREFERENCES      = 2;

    private static final int TAB_1 = 1;
    private static final int TAB_2 = 2;
    private static final int TAB_3 = 3;



    private Button mButtonPreferences;
    private Button mButtonAddPerson;
    private Button mButtonGoToSchedule;
    private Button mButtonListPerson;
    private Button mButtonListNotified;
    private Button mButtonListNews;

    @Inject
    public IPreserterStarter myPresenter;

    @Named("clients")
    @Inject
    public ZAdapter zAdapter;

    @Named("incoming_calls")
    @Inject
    public ZAdapter zAdapter2;

    private RecyclerView mRecyclerView;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if ((requestCode==INTENT_REQUEST_ADD_PERSON) && (resultCode==IPreserterStarter.INTENT_RESULT_COMMIT)) {
            Bundle bundle = data.getBundleExtra(IPreserterStarter.INTENT_BUNDLE_KEY);
            if (bundle != null){
                Client client = Client.fromBundle(bundle);
                myPresenter.insertDataItem(zAdapter,IPreserterStarter.WHAT_INSERT_CLIENT,client);
            }
        } else if((requestCode==INTENT_REQUEST_EDIT_PERSON) && (resultCode==IPreserterStarter.INTENT_RESULT_COMMIT)) {
            Bundle bundle = data.getBundleExtra(IPreserterStarter.INTENT_BUNDLE_KEY);
            if (bundle != null) {
                Client client = Client.fromBundle(bundle);
                myPresenter.updateDataItem(zAdapter, IPreserterStarter.WHAT_UPDATE_CLIENT, client);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_person);
        App.getUIComponent().inject(this);
        initViews();
        initListeners();
        init();
    }

    private void initViews() {
        mButtonPreferences = (Button)findViewById(R.id.person_button_top1);
        mButtonAddPerson = (Button)findViewById(R.id.person_button_top2);
        mButtonGoToSchedule = (Button)findViewById(R.id.person_button_top3);
        mButtonListPerson = (Button)findViewById(R.id.person_button_bottom1);
        mButtonListNotified = (Button)findViewById(R.id.person_button_bottom2);
        mButtonListNews = (Button)findViewById(R.id.person_button_bottom3);
        mRecyclerView = new RecyclerView(this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(zAdapter);
        ((ViewGroup)findViewById(R.id.person_container1)).addView(mRecyclerView);
    }

    private void initListeners() {
        final Context context = this;
        final IPreserterUICallback callback = new IPreserterUICallback() {
            @Override
            public void onOccurredEvent(int what, Object value) {
                if (what==IPreserterStarter.EVENT_WHAT_UICALLBACK) {
                    Intent personWindow = Activity_Item_Person
                            .getIntent(context)
                            .putExtra(IPreserterStarter.INTENT_BUNDLE_KEY,Client.toBundle((Client)value));

                    startActivityForResult(personWindow,INTENT_REQUEST_EDIT_PERSON);
                }else if(what==IPreserterStarter.EVENT_WHAT_VIEW_REDIRECT) {
                    switch (((View)value).getTag().toString()) {
                        case "top1":
                            //<...> Настройки
                            break;
                        case "top2":
                            //<...> Новый клиент
                            Intent personWindow = Activity_Item_Person.getIntent(context);
                            startActivityForResult(personWindow,INTENT_REQUEST_ADD_PERSON);
                            break;
                        case "top3":
                            //<...> в расписание
                            break;
                        case "bottom1":
                            mRecyclerView.setAdapter(zAdapter);
                            myPresenter.populateData(zAdapter2, IPreserterStarter.WHAT_POUPULATE_NOTIFIES, null);
                            break;
                        case "bottom2":
                            mRecyclerView.setAdapter(zAdapter);
                            myPresenter.populateData(zAdapter, IPreserterStarter.WHAT_POUPULATE_NOTIFIES, null);
                            break;
                        case "bottom3":
                            mRecyclerView.setAdapter(zAdapter2);
                            myPresenter.populateData(zAdapter2, IPreserterStarter.WHAT_POUPULATE_NEW, null);
                            break;
                    }
                }
            }
        };
        zAdapter.setListener(callback);
        View.OnClickListener callbackOther = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onOccurredEvent(IPreserterStarter.EVENT_WHAT_VIEW_REDIRECT,v);
            }
        };
        mButtonPreferences.setOnClickListener(callbackOther);
        mButtonAddPerson.setOnClickListener(callbackOther);
        mButtonGoToSchedule.setOnClickListener(callbackOther);
        mButtonListPerson.setOnClickListener(callbackOther);
        mButtonListNotified.setOnClickListener(callbackOther);
        mButtonListNews.setOnClickListener(callbackOther);
    }

    private void init( ) {
        myPresenter.populateData(zAdapter, IPreserterStarter.WHAT_POUPULATE_PERSONE, null);
    }

    private void changeTab() {

    }

    @Override
    protected void onResume() {
        super.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
    }

    public static Intent getIntent(Context context) {
        return new Intent(context, Activity_List_Person.class);
    }
}
