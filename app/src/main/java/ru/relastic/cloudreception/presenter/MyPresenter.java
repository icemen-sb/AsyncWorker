package ru.relastic.cloudreception.presenter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;

import javax.inject.Inject;
import ru.relastic.cloudreception.dagger2.App;
import ru.relastic.cloudreception.domain.IServiceConnectCallback;
import ru.relastic.cloudreception.domain.IServiceProvide;
import ru.relastic.cloudreception.domain.MainServiceConnection;
import ru.relastic.cloudreception.repository.AuthData;
import ru.relastic.cloudreception.repository.DataTransact;
import ru.relastic.cloudreception.repository.IDataTransactCallback;
import ru.relastic.cloudreception.repository.TransactData;
import ru.relastic.cloudreception.repository.TransactData.*;
import ru.relastic.cloudreception.repository.TransactData.ResponseData.*;


public class MyPresenter  implements IPreserterStarter {
    @Inject
    Context mContext;
    @Inject
    public Intent mMainServiceIntent;
    @Inject
    public AuthData mAuthData;

    private ArrayList<IPresenterUICallback> mListeners = new ArrayList<>();

    //Intent homeWindow = Activity_List_Person.getIntent(mContext);
    //Intent scheduleWindow = Activity_List_Schedule.getIntent(mContext);

    //Intent personWindow = Activity_List_Person.getIntent(mContext);
    //Intent dateWindow = Activity_Item_Schedule.getIntent(mContext);

    //Intent personToDateWindow = Activity_AddByPerson.getIntent(mContext);
    //Intent dateToPersonWindow = Activity_AddByDate.getIntent(mContext);

    //Intent propertiesWindow = ActivityPreferences.getIntent(mContext);

    private IServiceProvide mService;
    private IServiceConnectCallback mIServiceConnectCallback = new IServiceConnectCallback() {
        @Override
        public void onStateChange(boolean isConnected,
                                  IServiceProvide service,
                                  final MainServiceConnection connection) {
            if (isConnected) {
                mService = service;
                mService.addListener(this);
            } else {
                mService = null;
            }
        }
        @Override
        public void onChangeTask(int what, Object value) {
            //Обработка внешнего события
            if (what == IServiceConnectCallback.WHAT_SERVICE_EVENT_UPDATED_DATA) {
                for (IPresenterUICallback callback : mListeners) {
                    callback.onOccurredEvent(IPreserterStarter.EVENT_WHAT_OTHER, value);
                }

            }
        }
    };
    private MainServiceConnection mainServiceConnection = new MainServiceConnection(mIServiceConnectCallback);

    public MyPresenter (){
        App.getComponent().inject(this);
        //Подключаемся к сервису
        mContext.bindService(mMainServiceIntent,mainServiceConnection,Context.BIND_AUTO_CREATE);
    }
    @Override
    protected void finalize() throws Throwable {
        mService.removeListener(mIServiceConnectCallback);
    }

    @Override
    public void startUI(@Nullable IncomingCall incomingCall) {
        //Синхронизируем...
        //<...>
        if (false) {
        mContext.bindService(mMainServiceIntent, new MainServiceConnection(new IServiceConnectCallback() {
            @Override
            public void onStateChange(boolean isConnected, final IServiceProvide service, MainServiceConnection connection) {
                service.requestData(mAuthData.next().setType(200), new IDataTransactCallback() {
                    @Override
                    public void onResponseData(TransactData response) {
                        System.out.println("************Данные c cthdthf:"+response.getResponse_data().getClients().size());
                        if (response.getResponse_msg().getCode()==0) {
                            service.requestData(mAuthData.setType(AuthData.AUTH_TYPE_UPDATE).setBody(response.getResponse_data()),
                                    new IDataTransactCallback() {
                                @Override
                                public void onResponseData(TransactData response) {
                                    System.out.println("************Данные скачаны");
                                }
                            },true) ;
                        }
                    }
                },false);
            }

            @Override
            public void onChangeTask(int What, Object value) {

            }
        }),Context.BIND_AUTO_CREATE);
        }

        Intent homeActivity = Activity_List_Person.getIntent(mContext);
        if (incomingCall != null) {
            homeActivity.putExtra(Activity_List_Person.INTENT_TAB_PRESET_KEY, Activity_List_Person.TAB_2);
        }
        mContext.startActivity(homeActivity);
    }

    @Override
    public void populateData(@NonNull final ZAdapter adapter, int what){
        switch (what) {
            case IPreserterStarter.WHAT_SELECT_PERSON_ALL:
                mService.requestData(
                        mAuthData.next().setType(AuthData.AUTH_TYPE_GETALL),
                        new IDataTransactCallback() {
                            @Override
                            public void onResponseData(TransactData response) {
                                if (response != null && response.getResponse_data() != null
                                                    && response.getResponse_data().getClients() != null) {
                                    adapter.setData(response.getResponse_data().getClients());
                                }
                            }},
                        true);
                break;
            case IPreserterStarter.WHAT_SELECT_PERSON_NOTIFIED:
                mService.requestData(
                        mAuthData.next().setType(AuthData.AUTH_TYPE_GETALL_NITUFIED),
                        new IDataTransactCallback() {
                            @Override
                            public void onResponseData(TransactData response) {
                                if (response != null && response.getResponse_data() != null
                                        && response.getResponse_data().getClients() != null) {
                                    adapter.setData(response.getResponse_data().getClients());
                                }
                            }},
                        true);
                break;
            case IPreserterStarter.WHAT_SELECT_INCOMING_ALL:
                mService.requestData(
                        mAuthData.next().setType(AuthData.AUTH_TYPE_GETALL_NEWES),
                        new IDataTransactCallback() {
                            @Override
                            public void onResponseData(TransactData response) {
                                if (response != null && response.getResponse_data() != null
                                        && response.getResponse_data().getCalls() != null) {
                                    adapter.setData(response.getResponse_data().getCalls());
                                }
                            }},
                        true);
                break;
        }
    }

    @Override
    public void selectData(int what, @NonNull IDataTransactCallback callback, @Nullable Object arg) {
        switch (what) {
            case IPreserterStarter.WHAT_SELECT_PERSON_ALL:
                break;
            case IPreserterStarter.WHAT_SELECT_PERSON_NOTIFIED:
                mService.requestData(mAuthData.next().setType(
                        AuthData.AUTH_TYPE_GETALL_NITUFIED),
                        callback,
                        true);
                break;
            case IPreserterStarter.WHAT_SELECT_INCOMING_ALL:
                break;
            case IPreserterStarter.WHAT_SELECT_SCHEDULEITEM_ALL:
                break;
        }
    }

    @Override
    public void updateDataItem(@NonNull Object arg) {
        mService.requestData(mAuthData.next().setType(
                AuthData.AUTH_TYPE_UPDATE).setBody(new ResponseData(arg)),
                null,
                true);
    }
    @Override
    public void updateData(int what, @Nullable Object arg) {
        if (what == IPreserterStarter.WHAT_UPDATE_ALL_NOTIFIED) {
            mService.requestData(mAuthData.next().setType(
                    AuthData.AUTH_TYPE_UPDATE_NOTIFIES_ALL),
                    null,
                    true);
        }
    }
    @Override
    public void insertDataItem(@NonNull Object arg) {
        IDataTransactCallback callback = null;
        if (arg.getClass().equals(Client.class)) {
            final String phone = ((Client)arg).getPhone();
            callback = new IDataTransactCallback() {
                @Override
                public void onResponseData(TransactData response) {
                    //ResponseData rd = new ResponseData(
                    //        new IncomingCall(((Client)arg).getPhone()));
                    if (response.getResponse_msg().getCode() == DataTransact.DATA_TRANSACT_RESULT_OK) {
                        mService.requestData(mAuthData.next().setType(
                                AuthData.AUTH_TYPE_DELETE_INCOMING_BY_PHONE)
                                        .setBody(new ResponseData(new IncomingCall(phone))),
                                null,
                                true);
                    }
                }
            };
        }
        mService.requestData(mAuthData.next().setType(
                AuthData.AUTH_TYPE_INSERT).setBody(new ResponseData(arg)),
                callback,
                true);
    }
    @Override
    public void deleteDataItem(@NonNull Object arg) {
        mService.requestData(
                mAuthData.next().setType(AuthData.AUTH_TYPE_DELETE).setBody(new ResponseData(arg)),
                null,
                true);
    }
    @Override
    public void deleteData (int what, @Nullable Object arg) {
        if (what == IPreserterStarter.WHAT_DELETE_ALL_INCOMING_CALL) {
            mService.requestData(
                    mAuthData.next().setType(AuthData.AUTH_TYPE_DELETE_INCOMING_ALL),
                    null,
                    true);
        }
    }

    @Override
    public void addListener(IPresenterUICallback iPresenterUICallback) {
        if (!mListeners.contains(iPresenterUICallback)) {mListeners.add(iPresenterUICallback);}
    }
    @Override
    public void removeListener(IPresenterUICallback iPresenterUICallback) {
        mListeners.remove(iPresenterUICallback);
    }
}
