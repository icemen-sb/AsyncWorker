package ru.relastic.asyncworker.presenter;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
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
import ru.relastic.asyncworker.repository.TransactData;
import ru.relastic.asyncworker.repository.TransactData.ResponseData;
import ru.relastic.asyncworker.repository.TransactData.ResponseData.*;


public class Activity_List_Person extends AppCompatActivity {
    public static final int INTENT_REQUEST_ADD_PERSON       = 0;
    public static final int INTENT_REQUEST_EDIT_PERSON      = 1;
    public static final int INTENT_REQUEST_PREFERENCES      = 2;
    private static final int TAB_1 = 1;
    private static final int TAB_2 = 2;
    private static final int TAB_3 = 3;
    public static final int INTENT_TAB_PRESET_DEFAULT       = TAB_1;
    public static final String INTENT_TAB_PRESET_KEY        = "tab";


    private Button mButtonPreferences;
    private Button mButtonAddPerson;
    private Button mButtonGoToSchedule;
    private Button mButtonListPerson;
    private Button mButtonListNotified;
    private Button mButtonListNews;
    private Button mCurrentTab;

    private int mTab = INTENT_TAB_PRESET_DEFAULT;

    @Inject
    public IPreserterStarter myPresenter;

    @Named("clients")
    @Inject
    public ZAdapter zAdapter;

    @Named("incoming_calls")
    @Inject
    public ZAdapter zAdapter2;

    private RecyclerView mRecyclerView;

    private IPreserterUICallback mCallback = new IPreserterUICallback() {

        @Override
        public void onOccurredEvent(int what, Object value) {
            if (what == IPreserterStarter.EVENT_WHAT_UICALLBACK) {
                //edit person
                Client client;
                if (value.getClass().equals(IncomingCall.class)) {
                    client = IncomingCall.createClientByPhone(((IncomingCall)value).getPhone());
                }else {
                    client = (Client) value;
                }
                Intent personWindow = Activity_Item_Person
                        .getIntent(Activity_List_Person.this)
                        .putExtra(IPreserterStarter.INTENT_BUNDLE_KEY, Client.toBundle(client));

                startActivityForResult(personWindow, INTENT_REQUEST_EDIT_PERSON);
            } else if (what == IPreserterStarter.EVENT_WHAT_VIEW_REDIRECT) {
                switch (((View) value).getTag().toString()) {
                    case "top1":
                        //<...> Настройки
                        break;
                    case "top2":
                        //<...> Новый клиент
                        Intent personWindow = Activity_Item_Person.getIntent(Activity_List_Person.this);
                        startActivityForResult(personWindow, INTENT_REQUEST_ADD_PERSON);
                        break;
                    case "top3":
                        //<...> в расписание
                        break;
                    case "bottom1":
                        if (getTab()!=TAB_1) {
                            setTab((Button)value);
                            mRecyclerView.setAdapter(zAdapter);
                            myPresenter.populateData(zAdapter, IPreserterStarter.WHAT_POUPULATE_PERSONE, null);
                        }
                        break;
                    case "bottom2":
                        if (getTab()!=TAB_2) {
                            setTab((Button)value);
                            mRecyclerView.setAdapter(zAdapter);
                            myPresenter.populateData(zAdapter, IPreserterStarter.WHAT_POUPULATE_NOTIFIES, null);
                        }
                        break;
                    case "bottom3":
                        if (getTab()!=TAB_3) {
                            setTab((Button) value);
                            zAdapter2.setListener(mCallback);
                            mRecyclerView.setAdapter(zAdapter2);
                            myPresenter.populateData(zAdapter2, IPreserterStarter.WHAT_POUPULATE_NEW, null);
                        }
                        break;
                }
            } else {
                if (what == IPreserterStarter.EVENT_WHAT_OTHER) {
                    //value - измененные данные. попробовать использовать с diffUtils
                    //if type of value Incoming call

                    ResponseData responseData = (ResponseData)value;
                    switch (getTab()) {
                        case TAB_1:
                            if ((responseData.getClients() != null) &&
                                    responseData.getClients().size()>0) {
                                myPresenter.populateData(zAdapter,
                                        IPreserterStarter.WHAT_POUPULATE_PERSONE,
                                        null);
                            }
                            break;
                        case TAB_2:
                            if ((responseData.getClients() != null) &&
                                    responseData.getClients().size()>0) {
                                myPresenter.populateData(zAdapter,
                                        IPreserterStarter.WHAT_POUPULATE_NOTIFIES,
                                        null);
                            }
                            break;
                        case TAB_3:
                            if ((responseData.getCalls() != null) &&
                                    responseData.getCalls().size()>0)  {
                                myPresenter.populateData(zAdapter2,
                                        IPreserterStarter.WHAT_POUPULATE_NEW,
                                        null);
                            }
                            break;
                    }
                }
            }
        }
    };

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
        if (savedInstanceState == null) {
            mTab = getIntent().getIntExtra(INTENT_TAB_PRESET_KEY,INTENT_TAB_PRESET_DEFAULT);
        }else if (savedInstanceState.containsKey(INTENT_TAB_PRESET_KEY)) {
            mTab = savedInstanceState.getInt(INTENT_TAB_PRESET_KEY);
        } else {
            mTab = INTENT_TAB_PRESET_DEFAULT;
        }
        initViews();
        initListeners();
        init();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(INTENT_TAB_PRESET_KEY,mTab);
        super.onSaveInstanceState(outState);
    }

    private void initViews() {
        mButtonPreferences = (Button)findViewById(R.id.person_button_top1);
        mButtonAddPerson = (Button)findViewById(R.id.person_button_top2);
        mButtonGoToSchedule = (Button)findViewById(R.id.person_button_top3);
        mButtonListPerson = (Button)findViewById(R.id.person_button_bottom1);
        mButtonListNotified = (Button)findViewById(R.id.person_button_bottom2);
        mButtonListNews = (Button)findViewById(R.id.person_button_bottom3);
        setTab(mTab);
        zAdapter.setListener(mCallback);
        zAdapter2.setListener(mCallback);
        mRecyclerView = new RecyclerView(this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        //mRecyclerView.setAdapter(zAdapter);
        ((ViewGroup)findViewById(R.id.person_container1)).addView(mRecyclerView);
    }
    private void initListeners() {
        View.OnClickListener callbackOther = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onOccurredEvent(IPreserterStarter.EVENT_WHAT_VIEW_REDIRECT,v);
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
        switch (mTab) {
            case TAB_1:
                mRecyclerView.setAdapter(zAdapter);
                myPresenter.populateData(zAdapter, IPreserterStarter.WHAT_POUPULATE_PERSONE, null);
                break;
            case TAB_2:
                mRecyclerView.setAdapter(zAdapter);
                myPresenter.populateData(zAdapter, IPreserterStarter.WHAT_POUPULATE_NOTIFIES, null);
                break;
            case TAB_3:
                mRecyclerView.setAdapter(zAdapter2);
                myPresenter.populateData(zAdapter2, IPreserterStarter.WHAT_POUPULATE_NEW, null);
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        myPresenter.addListener(mCallback);
    }
    @Override
    protected void onResume() {
        super.onResume();

    }
    @Override
    protected void onPause() {
        super.onPause();
    }
    @Override
    protected void onStop() {
        super.onStop();
        myPresenter.removeListener(mCallback);
    }

    private void setTab(Button nextTab) {
        ColorStateList myColorStateList = new ColorStateList(
                new int[][]{
                        new int[]{}
                },
                new int[] {
                        getResources().getColor(R.color.primary_light),
                }
        );
        Button[] tablist = {mButtonListPerson,mButtonListNotified,mButtonListNews};
        if (!nextTab.equals(mCurrentTab)) {
            for (Button tab : tablist) {
                if (tab.equals(nextTab)) {
                    tab.setBackgroundTintList(myColorStateList);
                    mCurrentTab = tab;
                }else {
                    tab.setBackgroundTintList(null);
                }
            }
        }
        mTab = getTab();
    }
    private void setTab(int nextTab) {
        mCurrentTab = mButtonListPerson;
        switch (nextTab) {
            case TAB_1:
                mCurrentTab = mButtonListPerson;
                break;
            case TAB_2:
                mCurrentTab = mButtonListNotified;
                break;
            case TAB_3:
                mCurrentTab = mButtonListNews;
                break;
        }
        setTab(mCurrentTab);
    }
    private int getTab() {
        int retVal = -1;
        switch (((View)mCurrentTab).getTag().toString()) {
            case "bottom1":
                retVal = TAB_1;
                break;
            case "bottom2":
                retVal = TAB_2;
                break;
            case "bottom3":
                retVal = TAB_3;
                break;
        }
        return retVal;
    }

    public static Intent getIntent(Context context) {
        return new Intent(context, Activity_List_Person.class);
    }
}
