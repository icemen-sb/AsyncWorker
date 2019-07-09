package ru.relastic.cloudreception.presenter;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;
import javax.inject.Named;

import ru.relastic.cloudreception.R;
import ru.relastic.cloudreception.dagger2.App;
import ru.relastic.cloudreception.domain.MyReceiver;
import ru.relastic.cloudreception.repository.DataTransact;
import ru.relastic.cloudreception.repository.IDataTransactCallback;
import ru.relastic.cloudreception.repository.TransactData;
import ru.relastic.cloudreception.repository.TransactData.ResponseData;
import ru.relastic.cloudreception.repository.TransactData.ResponseData.*;
import ru.relastic.customviewlibrary.meet022customview.ButtonWA;


public class Activity_List_Person extends AppCompatActivity {
    public static final int INTENT_REQUEST_ADD_PERSON       = 0;
    public static final int INTENT_REQUEST_EDIT_PERSON      = 1;
    public static final int INTENT_REQUEST_PREFERENCES      = 2;
    public static final int TAB_1 = 1;
    public static final int TAB_2 = 2;
    public static final int TAB_3 = 3;
    public static final int INTENT_TAB_PRESET_DEFAULT       = TAB_1;
    public static final String INTENT_TAB_PRESET_KEY        = "tab";
    public static final int IDM_DIALING = 101;
    public static final int IDM_CURRENT = 102;
    public static final int IDM_EDIT = 103;
    public static final int IDM_ALL = 104;


    private View mButtonPreferences;
    private View mButtonAddPerson;
    private View mButtonGoToSchedule;
    private View mButtonListPerson;
    private View mButtonListNotified;
    private View mButtonListNews;
    private View mCurrentTab;

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

    private IPresenterUICallback mCallback = new IPresenterUICallback() {

        @Override
        public void onOccurredEvent(int what, Object value) {
            if (what == IPreserterStarter.EVENT_WHAT_UICALLBACK) {
                //edit person
                Client client;
                if (value.getClass().equals(IncomingCall.class)) {
                    client = IncomingCall.createClientByPhone(((IncomingCall)value).getPhone());
                    Intent personWindow = Activity_Item_Person
                            .getIntent(Activity_List_Person.this)
                            .putExtra(IPreserterStarter.INTENT_BUNDLE_KEY, Client.toBundle(client));
                    startActivityForResult(personWindow, INTENT_REQUEST_ADD_PERSON);
                }else if (value.getClass().equals(Client.class)) {
                    client = (Client) value;
                    Intent personWindow = Activity_Item_Person
                            .getIntent(Activity_List_Person.this)
                            .putExtra(IPreserterStarter.INTENT_BUNDLE_KEY, Client.toBundle(client));
                    startActivityForResult(personWindow, INTENT_REQUEST_EDIT_PERSON);
                }
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
                            setTab((View)value);
                            mRecyclerView.setAdapter(zAdapter);
                            myPresenter.populateData(zAdapter, IPreserterStarter.WHAT_SELECT_PERSON_ALL);
                        }
                        break;
                    case "bottom2":
                        if (getTab()!=TAB_2) {

                            setTab((View)value);
                            zAdapter.setData(null);
                            mRecyclerView.setAdapter(zAdapter);
                            myPresenter.populateData(zAdapter, IPreserterStarter.WHAT_SELECT_PERSON_NOTIFIED);

                        }
                        break;
                    case "bottom3":
                        if (getTab()!=TAB_3) {
                            setTab((View)value);
                            zAdapter2.setListener(mCallback);
                            mRecyclerView.setAdapter(zAdapter2);
                            myPresenter.populateData(zAdapter2, IPreserterStarter.WHAT_SELECT_INCOMING_ALL);
                        }
                        break;
                }
            } else {
                if (what == IPreserterStarter.EVENT_WHAT_OTHER) {
                    ResponseData responseData = (ResponseData)value;

                    if (responseData.getClients() != null)  {
                        for (Client client : responseData.getClients()) {
                            Intent intent = MyReceiver.getIntent(Activity_List_Person.this);
                            IncomingCall incomingCall = new IncomingCall();
                            incomingCall.setClient(client);
                            intent.putExtra(MyReceiver.INTENT_INCOMING_CALL_BUNDLE,incomingCall.getBundle());
                            Activity_List_Person.this.sendBroadcast(intent);
                        }
                    }

                    switch (getTab()) {
                        case TAB_1:
                            if (((responseData.getClients() != null) &&
                                    responseData.getClients().size()>0) ||
                                    ((responseData.getCalls() != null) &&
                                    responseData.getCalls().size()>0)) {

                                myPresenter.populateData(zAdapter,
                                        IPreserterStarter.WHAT_SELECT_PERSON_ALL);
                            }
                            break;
                        case TAB_2:
                            if (((responseData.getClients() != null) &&
                                    responseData.getClients().size()>0) ||
                                    ((responseData.getCalls() != null) &&
                                            responseData.getCalls().size()>0)) {
                                myPresenter.populateData(zAdapter,
                                        IPreserterStarter.WHAT_SELECT_PERSON_NOTIFIED);
                            }
                            break;
                        case TAB_3:
                            if ((responseData.getCalls() != null) &&
                                    responseData.getCalls().size()>0)  {
                                myPresenter.populateData(zAdapter2,
                                        IPreserterStarter.WHAT_SELECT_INCOMING_ALL);
                            }
                            break;
                    }
                }
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (data != null) {
            if ((requestCode==INTENT_REQUEST_ADD_PERSON) && (resultCode==IPreserterStarter.INTENT_RESULT_COMMIT)) {
                Bundle bundle = data.getBundleExtra(IPreserterStarter.INTENT_BUNDLE_KEY);
                if (bundle != null){
                    Client client = Client.fromBundle(bundle);
                    myPresenter.insertDataItem(client);
                }
            } else if((requestCode==INTENT_REQUEST_EDIT_PERSON) && (resultCode==IPreserterStarter.INTENT_RESULT_COMMIT)) {
                Bundle bundle = data.getBundleExtra(IPreserterStarter.INTENT_BUNDLE_KEY);
                if (bundle != null) {
                    Client client = Client.fromBundle(bundle);
                    myPresenter.updateDataItem(client);
                }
            }
        }else {
            //возврат ответа на запрос разрешений android.permission.CALL_PHONE
            //<...>
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
        mButtonPreferences = findViewById(R.id.person_button_top1);
        mButtonAddPerson = findViewById(R.id.person_button_top2);
        mButtonGoToSchedule = findViewById(R.id.person_button_top3);
        mButtonListPerson = findViewById(R.id.person_button_bottom1);
        mButtonListNotified = findViewById(R.id.person_button_bottom2);
        mButtonListNews = findViewById(R.id.person_button_bottom3);
        setTab(mTab);
        zAdapter.setListener(mCallback);
        zAdapter2.setListener(mCallback);
        mRecyclerView = new RecyclerView(this);
        //registerForContextMenu((View)findViewById(R.id.person_list_main_container));
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
                if (!v.equals(mCurrentTab)) {
                    mCallback.onOccurredEvent(IPreserterStarter.EVENT_WHAT_VIEW_REDIRECT,v);
                }
            }
        };
        mButtonPreferences.setOnClickListener(callbackOther);
        mButtonAddPerson.setOnClickListener(callbackOther);
        mButtonGoToSchedule.setOnClickListener(callbackOther);
        mButtonListPerson.setOnClickListener(callbackOther);
        mButtonListNotified.setOnClickListener(callbackOther);
        mButtonListNews.setOnClickListener(callbackOther);
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int swipeDir) {
                switch (mTab) {
                    case TAB_1:
                        Client client = (Client)zAdapter.getItemByPosition(viewHolder.getAdapterPosition());
                        client.setNotified(false);
                        myPresenter.updateDataItem(client);
                        break;
                    case TAB_2:
                        Client notify = (Client)zAdapter.getItemByPosition(viewHolder.getAdapterPosition());
                        notify.setNotified(false);
                        myPresenter.updateDataItem(notify);
                        break;
                    case TAB_3:
                        IncomingCall call = (IncomingCall)zAdapter2.getItemByPosition(viewHolder.getAdapterPosition());
                        myPresenter.deleteDataItem(call);
                        break;
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }
    private void init( ) {
        switch (mTab) {
            case TAB_1:
                mRecyclerView.setAdapter(zAdapter);
                myPresenter.populateData(zAdapter, IPreserterStarter.WHAT_SELECT_PERSON_ALL);
                break;
            case TAB_2:
                mRecyclerView.setAdapter(zAdapter);
                myPresenter.populateData(zAdapter, IPreserterStarter.WHAT_SELECT_PERSON_NOTIFIED);
                break;
            case TAB_3:
                mRecyclerView.setAdapter(zAdapter2);
                myPresenter.populateData(zAdapter2, IPreserterStarter.WHAT_SELECT_INCOMING_ALL);
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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        mRecyclerView.setTag(v.getTag());
        boolean enabledCurrentItem = (mRecyclerView.getTag() != null);

        if (enabledCurrentItem) {
            menu.add(Menu.NONE, IDM_DIALING, Menu.NONE, R.string.dialing);
            if (mRecyclerView.getTag().getClass().equals(Client.class)) {
                menu.add(Menu.NONE, IDM_CURRENT, Menu.NONE, R.string.mark_as_read).
                        setEnabled(((Client)mRecyclerView.getTag()).getNotified());
                menu.add(Menu.NONE, IDM_EDIT, Menu.NONE, R.string.edit_current);
            }else if (mRecyclerView.getTag().getClass().equals(IncomingCall.class)) {
                menu.add(Menu.NONE, IDM_CURRENT, Menu.NONE, R.string.delete_current);
                menu.add(Menu.NONE, IDM_EDIT, Menu.NONE, R.string.create_new_client);
            }
        }
        switch (getTab()) {
            case TAB_1:
                final MenuItem tmp = menu.add(Menu.NONE, IDM_ALL, Menu.NONE, R.string.mark_all_as_read);
                myPresenter.selectData(IPreserterStarter.WHAT_SELECT_PERSON_NOTIFIED, new IDataTransactCallback() {
                    @Override
                    public void onResponseData(TransactData response) {
                        if (response.getResponse_msg().getCode()== DataTransact.DATA_TRANSACT_RESULT_OK) {
                            tmp.setEnabled(response.getResponse_data().getClients().size()>0);
                        }
                    }
                },
                null);
                break;
            case TAB_2:
                menu.add(Menu.NONE, IDM_ALL, Menu.NONE, R.string.clear_list_notifies);
                break;
            case TAB_3:
                menu.add(Menu.NONE, IDM_ALL, Menu.NONE, R.string.clear_list_notifies);
                break;
        }
        //unregisterForContextMenu(v);
    }
    @Override
    public void onContextMenuClosed(Menu menu) {
        mRecyclerView.setTag(null);
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (mRecyclerView.getTag() != null) {
            //mCallback.onOccurredEvent(IPreserterStarter.EVENT_WHAT_VIEW_REDIRECT,v);
            //myPresenter.updateDataItem(IPreserterStarter.WHAT_UPDATE_CLIENT, client);
            //CharSequence message;
            switch (item.getItemId())
            {
                case IDM_DIALING:
                    String pone_by_source = null;
                    if (mRecyclerView.getTag().getClass().equals(Client.class)) {
                        pone_by_source = ((Client)mRecyclerView.getTag()).getPhone();
                    } else if(mRecyclerView.getTag().getClass().equals(IncomingCall.class)) {
                        pone_by_source = (((IncomingCall)mRecyclerView.getTag()).getPhone());
                    }
                    if (pone_by_source != null) {
                        //Позвонить
                        final String phone = (!pone_by_source.startsWith("8"))
                                ? "+" + pone_by_source
                                : pone_by_source;
                        if (ContextCompat.checkSelfPermission(this,
                                        Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+phone)));
                        } else {
                            //Запросить разрешение android.permission.CALL_PHONE
                            //<...>
                            //В данной версии просто уведомляем
                            new AlertDialog.Builder(this)
                                    .setTitle(getResources().getString(R.string.error))
                                    .setMessage(getResources().getString(R.string.requires_call_permission))
                                    .setNeutralButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface arg0, int arg1) {
                                            startActivity(new Intent(Intent.ACTION_DIAL,
                                                    Uri.parse("tel:"+phone)));}
                                    }).create().show();
                        }
                    }
                    break;
                case IDM_CURRENT:
                    if (mRecyclerView.getTag().getClass().equals(Client.class)) {
                        //Отметить как прочитанное
                        ((Client)mRecyclerView.getTag()).setNotified(false);
                        myPresenter.updateDataItem(mRecyclerView.getTag());
                    } else if(mRecyclerView.getTag().getClass().equals(IncomingCall.class)) {
                        //Удалить выбранное напоминание
                        myPresenter.deleteDataItem(mRecyclerView.getTag());
                    }
                    break;
                case IDM_EDIT:
                    if (mRecyclerView.getTag().getClass().equals(Client.class)) {
                        //Перейти к редактированию
                        mCallback.onOccurredEvent(IPreserterStarter.EVENT_WHAT_UICALLBACK,
                                mRecyclerView.getTag());
                    } else if(mRecyclerView.getTag().getClass().equals(IncomingCall.class)) {
                        //Создать нового клиента
                        mCallback.onOccurredEvent(IPreserterStarter.EVENT_WHAT_UICALLBACK,
                                mRecyclerView.getTag());
                    }
                    break;
                case IDM_ALL:
                    if (mRecyclerView.getTag().getClass().equals(Client.class)) {
                        //Отметить все как прочитанное (С ЗАПРОСОМ)
                        AlertDialog dialog = new AlertDialog.Builder(this)
                                .setTitle(getResources().getString(R.string.confirm_title_update))
                                .setMessage(getResources().getString(R.string.confirm_mark_all_read))
                                .setNegativeButton(android.R.string.no, null)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        myPresenter.updateData(
                                                IPreserterStarter.WHAT_UPDATE_ALL_NOTIFIED,null);
                                    }
                                }).create();
                        dialog.show();
                    } else if(mRecyclerView.getTag().getClass().equals(IncomingCall.class)) {
                        AlertDialog dialog = new AlertDialog.Builder(this)
                                .setTitle(getResources().getString(R.string.confirm_title_delete))
                                .setMessage(getResources().getString(R.string.confirm_clear_all_unknown))
                                .setNegativeButton(android.R.string.no, null)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        myPresenter.deleteData(
                                                IPreserterStarter.WHAT_DELETE_ALL_INCOMING_CALL,null);
                                    }
                                }).create();
                        dialog.show();
                    }
                    break;
                default:
                    return super.onContextItemSelected(item);
            }
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    private void setTab(View nextTab) {
        View[] tabList = {mButtonListPerson,mButtonListNotified,mButtonListNews};
        //if (!nextTab.equals(mCurrentTab)) {
            for (View tab : tabList) {
                if (tab.equals(nextTab)) {
                    ((ButtonWA)tab).setFrontColor(R.color.primary_light);
                    mCurrentTab = tab;
                }else {
                    ((ButtonWA)tab).setFrontColor(R.color.colorPrimary);
                }
            }
        //}
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
        switch (mCurrentTab.getTag().toString()) {
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
        Intent intent = new Intent(context, Activity_List_Person.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }
}
